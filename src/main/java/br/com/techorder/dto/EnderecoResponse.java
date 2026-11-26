package br.com.techorder.dto;

import br.com.techorder.model.Endereco;

public record EnderecoResponse(Long id, Long clienteId, String cep, String logradouro, String numero,
                               String complemento, String bairro, String cidade, String estado) {

  public static EnderecoResponse de(Endereco e) {
    return new EnderecoResponse(e.getIdEndereco(), e.getCliente().getIdCliente(), e.getCep(), e.getLogradouro(),
        e.getNumero(), e.getComplemento(), e.getBairro(), e.getCidade(), e.getEstado());
  }
}
