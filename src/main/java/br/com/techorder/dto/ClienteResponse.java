package br.com.techorder.dto;

import br.com.techorder.model.Cliente;

public record ClienteResponse(Long id, String nome, String email, String telefone) {

  public static ClienteResponse de(Cliente c) {
    return new ClienteResponse(c.getIdCliente(), c.getNome(), c.getEmail(), c.getTelefone());
  }
}
