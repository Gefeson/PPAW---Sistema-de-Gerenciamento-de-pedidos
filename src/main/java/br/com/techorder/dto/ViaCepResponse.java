package br.com.techorder.dto;

/** Formato do JSON devolvido pela API externa ViaCEP (https://viacep.com.br/). */
public record ViaCepResponse(String cep, String logradouro, String complemento, String bairro,
                             String localidade, String uf, Boolean erro) {
}
