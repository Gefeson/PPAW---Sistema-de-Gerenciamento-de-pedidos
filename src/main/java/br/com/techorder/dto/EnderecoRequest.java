package br.com.techorder.dto;

/** O cliente informa só CEP, número e complemento; o restante vem do ViaCEP. */
public record EnderecoRequest(String cep, String numero, String complemento) {
}
