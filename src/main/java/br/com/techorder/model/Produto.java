package br.com.techorder.model;

import java.math.BigDecimal;

public class Produto {
  private Long idProduto;
  private String nome;
  private String descricao;
  private BigDecimal preco;
  private Integer quantidadeEstoque;
  private String imagemUrl;
  private Boolean ativo;

  public Produto() {
  }

  //construtor
  public Produto (Long idProduto, String nome, String descricao, BigDecimal preco, Integer quantidadeEstoque, String imagemUrl, Boolean ativo) {
    this.idProduto = idProduto;
    this.nome = nome;
    this.descricao = descricao;
    this.preco = preco;
    this.quantidadeEstoque = quantidadeEstoque;
    this.imagemUrl = imagemUrl;
    this.ativo = ativo;
  }

  public BigDecimal calcularValorEstoque() {
    return preco.multiply(BigDecimal.valueOf(quantidadeEstoque));
  }

  public boolean possuiEstoque(int quantidade) {
    return quantidadeEstoque >= quantidade;
  }

  public void baixarEstoque(int quantidade) {
    this.quantidadeEstoque -= quantidade;
  }

  public void adicionarEstoque(int quantidade) {
    this.quantidadeEstoque += quantidade;
  }

  
  // getters e setters
  public Long getIdProduto() {
    return idProduto;
  }

  public String getNome(){
    return nome;
  }

  public String getDescricao(){
    return descricao;
  }

  public BigDecimal getPreco(){
    return preco;
  }

  public Integer getQuantidadeEstoque(){
    return quantidadeEstoque;
  }

  public String getImagemUrl(){
    return imagemUrl;
  }

  public Boolean getAtivo(){
    return ativo;
  }

  public void setIdProduto(Long idProduto) {
    this.idProduto = idProduto;
  }

  public void setNome(String nome){
    this.nome = nome;
  }

  public void setDescricao(String descricao){
    this.descricao = descricao;
  }

  public void setPreco(BigDecimal preco){
    this.preco = preco;
  }

  public void setQuantidadeEstoque(Integer quantidadeEstoque){
    this.quantidadeEstoque = quantidadeEstoque;
  }

  public void setImagemUrl(String imagemUrl){
    this.imagemUrl = imagemUrl;
  }

  public void setAtivo(Boolean ativo){
    this.ativo = ativo;
  }

}
