package br.com.techorder.dto;

import br.com.techorder.enums.PerfilFuncionario;
import br.com.techorder.model.Funcionario;

public record FuncionarioResponse(Long id, String nome, String email, PerfilFuncionario perfil) {

  public static FuncionarioResponse de(Funcionario f) {
    return new FuncionarioResponse(f.getIdFuncionario(), f.getNome(), f.getEmail(), f.getPerfil());
  }
}
