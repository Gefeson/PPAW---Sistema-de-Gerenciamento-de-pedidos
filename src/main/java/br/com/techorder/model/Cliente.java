package br.com.techorder.model;

public class Cliente {
    private Long idCliente;
    private String nome;
    private String email;
    private String senhaHash;
    private String telefone;

    public Cliente() {
    }

    //construtor
    public Cliente(Long idCliente, String nome, String email, String senhaHash, String telefone) {
      this.idCliente = idCliente;
      this.nome = nome;
      this.email = email;
      this.senhaHash = senhaHash;
      this.telefone = telefone;
    }

    // getters e setters
    public Long getIdCliente() {
      return idCliente;
    }

    public String getNome() {
      return nome;
    }
    
    public String getEmail() {
      return email;
    }
    
    public String getSenhaHash() {
      return senhaHash;
    }
    
    public String getTelefone() {
      return telefone;
    }

    public void setIdCliente(Long idCliente) {
      this.idCliente = idCliente;
    }

    public void setNome(String nome) {
      this.nome = nome;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public void setSenhaHash(String senhaHash) {
      this.senhaHash = senhaHash;
    }

    public void setTelefone(String telefone) {
      this.telefone = telefone;
    }
}
