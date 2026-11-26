package br.com.techorder.service;

import br.com.techorder.dto.ViaCepResponse;
import br.com.techorder.exception.ApiException;
import br.com.techorder.util.Json;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Cliente da API externa ViaCEP (https://viacep.com.br/ws/{cep}/json/), feito com
 * java.net.http.HttpClient (JDK). A URL base pode ser trocada por TECHORDER_VIACEP_URL (útil em testes).
 */
public class ViaCepClient {

  private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
  private final String urlBase;

  public ViaCepClient() {
    String env = System.getenv("TECHORDER_VIACEP_URL");
    this.urlBase = (env == null || env.isBlank()) ? "https://viacep.com.br/ws/" : env;
  }

  /** @param cep somente os 8 dígitos */
  public ViaCepResponse consultar(String cep) {
    HttpRequest requisicao = HttpRequest.newBuilder(URI.create(urlBase + cep + "/json/"))
        .timeout(Duration.ofSeconds(8))
        .header("Accept", "application/json")
        .GET()
        .build();

    try {
      HttpResponse<String> resposta = http.send(requisicao, HttpResponse.BodyHandlers.ofString());

      if (resposta.statusCode() == 400) {
        throw ApiException.requisicaoInvalida("CEP inválido");
      }
      if (resposta.statusCode() != 200) {
        throw new ApiException(502, "Serviço ViaCEP indisponível (HTTP " + resposta.statusCode() + ")");
      }

      ViaCepResponse dados = Json.deJson(resposta.body(), ViaCepResponse.class);
      if (dados == null || Boolean.TRUE.equals(dados.erro())) {
        throw ApiException.naoEncontrado("CEP não encontrado");
      }
      return dados;

    } catch (IOException e) {
      throw new ApiException(502, "Não foi possível consultar o ViaCEP: " + e.getMessage());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ApiException(502, "Consulta ao ViaCEP interrompida");
    } catch (JsonParseException e) {
      throw new ApiException(502, "Resposta inesperada do ViaCEP");
    }
  }
}
