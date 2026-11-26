package br.com.techorder.model;

public class Endereco {
 
  private Cliente cliente;
  private Long idEndereco;
  private String cep;
  private String logradouro;
  private String numero;
  private String complemento;
  private String bairro;
  private String cidade;
  private String estado;

  public Endereco() {
  }

  //construtor
  public Endereco(Long idEndereco, String cep, String logradouro, String numero, String complemento, String bairro, String cidade, String estado) {
    this.idEndereco = idEndereco;
    this.cep = cep;
    this.logradouro = logradouro;
    this.numero = numero;
    this.complemento = complemento;
    this.bairro = bairro;
    this.cidade = cidade;
    this.estado = estado;
  }

  // getters e setters
  public Cliente getCliente() {
    return cliente;
  }

  public Long getIdEndereco() {
    return idEndereco;
  }

  public String getCep() {
    return cep;
  }

  public String getLogradouro() {
    return logradouro;
  }

  public String getNumero() {
    return numero;
  }
  
  public String getComplemento() {
    return complemento;
  }

  public String getBairro() {
    return bairro;
  }

  public String getCidade() {
    return cidade;
  }

  public String getEstado() {
    return estado;
  }

  public void setCliente(Cliente cliente) {
    this.cliente = cliente;
  }

  public void setIdEndereco(Long idEndereco) {
    this.idEndereco = idEndereco;
  }

  public void setCep(String cep) {
    this.cep = cep;
  }

  public void setLogradouro(String logradouro) {
    this.logradouro = logradouro;
  }

  public void setNumero(String numero) {
    this.numero = numero;
  }

  public void setComplemento(String complemento) {
    this.complemento = complemento;
  }

  public void setBairro(String bairro) {
    this.bairro = bairro;
  }

  public void setCidade(String cidade) {
    this.cidade = cidade;
  }

  

  public void setEstado(String estado) {
    this.estado = estado;
  }
}
