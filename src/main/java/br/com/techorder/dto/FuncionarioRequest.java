package br.com.techorder.dto;

import br.com.techorder.enums.PerfilFuncionario;

public record FuncionarioRequest(String nome, String email, String senha, PerfilFuncionario perfil) {
}
