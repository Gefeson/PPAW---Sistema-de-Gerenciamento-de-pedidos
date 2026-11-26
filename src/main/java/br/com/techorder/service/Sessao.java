package br.com.techorder.service;

import br.com.techorder.enums.PerfilFuncionario;

/** Quem está fazendo a requisição (resultado da autenticação). */
public record Sessao(Tipo tipo, Long id, PerfilFuncionario perfil) {

  public enum Tipo { CLIENTE, FUNCIONARIO }

  public static Sessao cliente(Long id) {
    return new Sessao(Tipo.CLIENTE, id, null);
  }

  public static Sessao funcionario(Long id, PerfilFuncionario perfil) {
    return new Sessao(Tipo.FUNCIONARIO, id, perfil);
  }

  public boolean ehCliente() {
    return tipo == Tipo.CLIENTE;
  }

  public boolean ehFuncionario() {
    return tipo == Tipo.FUNCIONARIO;
  }

  public boolean ehAdministrador() {
    return ehFuncionario() && perfil == PerfilFuncionario.ADMINISTRADOR;
  }
}
