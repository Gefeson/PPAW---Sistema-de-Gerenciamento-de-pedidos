package br.com.techorder.http;

import br.com.techorder.exception.ApiException;
import br.com.techorder.util.Json;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/** Visão simplificada de uma requisição HTTP: método, parâmetros de rota, query string, cabeçalhos e corpo. */
public class Requisicao {

  private final HttpExchange exchange;
  private final Map<String, String> params;
  private final Map<String, String> query = new HashMap<>();
  private String corpo;

  public Requisicao(HttpExchange exchange, Map<String, String> params) {
    this.exchange = exchange;
    this.params = params;

    String bruta = exchange.getRequestURI().getRawQuery();
    if (bruta != null) {
      for (String par : bruta.split("&")) {
        String[] kv = par.split("=", 2);
        query.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
            kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "");
      }
    }
  }

  public String metodo() {
    return exchange.getRequestMethod();
  }

  public String param(String nome) {
    return params.get(nome);
  }

  /** Parâmetro de rota numérico (ex.: {id}); responde 400 se não for número. */
  public Long paramId(String nome) {
    try {
      return Long.valueOf(params.get(nome));
    } catch (NumberFormatException e) {
      throw ApiException.requisicaoInvalida("O parâmetro '" + nome + "' deve ser numérico");
    }
  }

  public String query(String nome) {
    return query.get(nome);
  }

  public String header(String nome) {
    return exchange.getRequestHeaders().getFirst(nome);
  }

  public <T> T corpoComo(Class<T> tipo) {
    try {
      if (corpo == null) {
        corpo = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
      }
      if (corpo.isBlank()) {
        throw ApiException.requisicaoInvalida("Corpo da requisição (JSON) é obrigatório");
      }
      T valor = Json.deJson(corpo, tipo);
      if (valor == null) {
        throw ApiException.requisicaoInvalida("Corpo da requisição (JSON) é obrigatório");
      }
      return valor;
    } catch (JsonParseException e) {
      throw ApiException.requisicaoInvalida("JSON inválido: " + e.getMessage());
    } catch (IOException e) {
      throw ApiException.requisicaoInvalida("Não foi possível ler o corpo da requisição");
    }
  }
}
