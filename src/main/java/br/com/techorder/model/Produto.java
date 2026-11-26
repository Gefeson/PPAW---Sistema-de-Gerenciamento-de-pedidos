package br.com.techorder.model;

import java.math.BigDecimal;
import br.com.techorder.exception.RegraNegocioException;

public class Produto {
  private Long idProduto;
  private String nome;
  private String descricao;
  private BigDecimal preco;
  private Integer quantidadeEstoque;
  private String imagemUrl;
  private Boolean ativo;

  public Produto() {
    this.ativo = true;
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
    if (preco == null || quantidadeEstoque == null) {
      return BigDecimal.ZERO;
    }
    return preco.multiply(BigDecimal.valueOf(quantidadeEstoque));
  }

  public boolean possuiEstoque(int quantidade) {
    return quantidade > 0 && quantidadeEstoque != null && quantidadeEstoque >= quantidade;
  }

  public void baixarEstoque(int quantidade) {
    validarQuantidade(quantidade);
    if (!possuiEstoque(quantidade)) {
      throw new RegraNegocioException("Estoque insuficiente para o produto '" + nome + "'");
    }
    this.quantidadeEstoque -= quantidade;
  }

  public void adicionarEstoque(int quantidade) {
    validarQuantidade(quantidade);
    this.quantidadeEstoque = (quantidadeEstoque == null ? 0 : quantidadeEstoque) + quantidade;
  }

  private void validarQuantidade(int quantidade) {
    if (quantidade <= 0) {
      throw new RegraNegocioException("A quantidade deve ser maior que zero");
    }
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
