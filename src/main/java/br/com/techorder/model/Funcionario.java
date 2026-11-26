package br.com.techorder.model;

import br.com.techorder.enums.PerfilFuncionario;

public class Funcionario {
  private Long idFuncionario;
  private String nome;
  private String email;
  private String senhaHash;
  private PerfilFuncionario perfil;

  public Funcionario() {
  }

  //construtor
  public Funcionario(Long idFuncionario, String nome, String email, String senhaHash, PerfilFuncionario perfil) {
    this.idFuncionario = idFuncionario;
    this.nome = nome;
    this.email = email;
    this.senhaHash = senhaHash;
    this.perfil = perfil;
  }

  public boolean ehAdministrador() {
    return perfil == PerfilFuncionario.ADMINISTRADOR;
  }

  // getters e setters
  public Long getIdFuncionario() { return idFuncionario; }
  public String getNome() { return nome; }
  public String getEmail() { return email; }
  public String getSenhaHash() { return senhaHash; }
  public PerfilFuncionario getPerfil() { return perfil; }

  public void setIdFuncionario(Long idFuncionario) { this.idFuncionario = idFuncionario; }
  public void setNome(String nome) { this.nome = nome; }
  public void setEmail(String email) { this.email = email; }
  public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
  public void setPerfil(PerfilFuncionario perfil) { this.perfil = perfil; }
}
