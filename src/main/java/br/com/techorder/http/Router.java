package br.com.techorder.http;

import br.com.techorder.dto.ErroResponse;
import br.com.techorder.exception.ApiException;
import br.com.techorder.exception.ErroBancoException;
import br.com.techorder.exception.RegraNegocioException;
import br.com.techorder.util.Json;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Front Controller: recebe toda requisição do HttpServer do JDK, encontra a rota
 * (método + caminho com {parametros}), chama o Handler do Controller e converte
 * a Resposta (ou qualquer erro) em JSON.
 */
public class Router implements HttpHandler {

  private record Rota(String metodo, String[] segmentos, Handler handler) {
  }

  private final List<Rota> rotas = new ArrayList<>();

  public void get(String padrao, Handler h)    { rotas.add(new Rota("GET", dividir(padrao), h)); }
  public void post(String padrao, Handler h)   { rotas.add(new Rota("POST", dividir(padrao), h)); }
  public void put(String padrao, Handler h)    { rotas.add(new Rota("PUT", dividir(padrao), h)); }
  public void delete(String padrao, Handler h) { rotas.add(new Rota("DELETE", dividir(padrao), h)); }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    Resposta resposta;
    try {
      resposta = despachar(exchange);
    } catch (Throwable erro) {
      resposta = traduzirErro(erro);
    }
    enviar(exchange, resposta);
  }

  private Resposta despachar(HttpExchange exchange) throws Exception {
    String[] caminho = dividir(exchange.getRequestURI().getPath());
    String metodo = exchange.getRequestMethod();
    boolean caminhoExiste = false;

    for (Rota rota : rotas) {
      Map<String, String> params = casar(rota.segmentos(), caminho);
      if (params == null) {
        continue;
      }
      caminhoExiste = true;
      if (rota.metodo().equals(metodo)) {
        return rota.handler().tratar(new Requisicao(exchange, params));
      }
    }

    if (caminhoExiste) {
      throw new ApiException(405, "Método " + metodo + " não permitido para este caminho");
    }
    throw ApiException.naoEncontrado("Rota não encontrada: " + exchange.getRequestURI().getPath());
  }

  private Map<String, String> casar(String[] padrao, String[] caminho) {
    if (padrao.length != caminho.length) {
      return null;
    }
    Map<String, String> params = new HashMap<>();
    for (int i = 0; i < padrao.length; i++) {
      if (padrao[i].startsWith("{") && padrao[i].endsWith("}")) {
        params.put(padrao[i].substring(1, padrao[i].length() - 1), caminho[i]);
      } else if (!padrao[i].equals(caminho[i])) {
        return null;
      }
    }
    return params;
  }

  private String[] dividir(String caminho) {
    String limpo = caminho.replaceAll("^/+|/+$", "");
    return limpo.isEmpty() ? new String[0] : limpo.split("/");
  }

  private Resposta traduzirErro(Throwable erro) {
    if (erro instanceof ApiException e) {
      return erroJson(e.getStatus(), e.getMessage());
    }
    if (erro instanceof RegraNegocioException e) {
      return erroJson(422, e.getMessage());
    }
    if (erro instanceof JsonParseException e) {
      return erroJson(400, "JSON inválido: " + e.getMessage());
    }
    if (erro instanceof ErroBancoException e) {
      String estado = e.getSqlState() == null ? "" : e.getSqlState();
      switch (estado) {
        case "23505": return erroJson(409, "Registro duplicado (ex.: e-mail já cadastrado)");
        case "23503": return erroJson(409, "Operação bloqueada: o registro possui vínculos com outros dados");
        case "23514": return erroJson(422, "Dados violam uma restrição do banco (ex.: valor ou estoque negativo)");
        default: break;
      }
    }
    erro.printStackTrace();
    return erroJson(500, "Erro interno do servidor");
  }

  private Resposta erroJson(int status, String mensagem) {
    return new Resposta(status, new ErroResponse(status, mensagem));
  }

  private void enviar(HttpExchange exchange, Resposta resposta) throws IOException {
    try (exchange) {
      if (resposta.corpo() == null) {
        exchange.sendResponseHeaders(resposta.status(), -1);
        return;
      }
      byte[] bytes = Json.paraJson(resposta.corpo()).getBytes(StandardCharsets.UTF_8);
      exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
      exchange.sendResponseHeaders(resposta.status(), bytes.length);
      try (OutputStream out = exchange.getResponseBody()) {
        out.write(bytes);
      }
    }
  }
}
