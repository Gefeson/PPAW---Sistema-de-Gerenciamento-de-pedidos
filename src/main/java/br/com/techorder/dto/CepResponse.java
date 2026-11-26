package br.com.techorder.dto;

/** Resposta da nossa API para GET /cep/{cep} (dados já tratados). */
public record CepResponse(String cep, String logradouro, String complemento, String bairro,
                          String cidade, String estado) {

  public static CepResponse de(ViaCepResponse v) {
    return new CepResponse(v.cep(), v.logradouro(), v.complemento(), v.bairro(), v.localidade(), v.uf());
  }
}
