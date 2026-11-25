package br.com.techorder.model;

import java.math.BigDecimal;

public class ItemPedido {
  private Long idItemPedido;
  private Pedido pedido;
  private Produto produto;
  private Integer quantidade;
  private BigDecimal precoUnitario;

  public ItemPedido(Long idItemPedido, Pedido pedido, Produto produto, Integer quantidade, BigDecimal precoUnitario) {
    this.idItemPedido = idItemPedido;
    this.pedido = pedido;
    this.produto = produto;
    this.quantidade = quantidade;
    this.precoUnitario = precoUnitario;
  }

  
  public BigDecimal getSubtotal() {
    return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
  }
  
  // getters e setters
  public Long getIdItemPedido() {
    return idItemPedido;
  }

  public Pedido getPedido() {
    return pedido;
  }

  public Produto getProduto() {
    return produto;
  }

  public Integer getQuantidade() {
    return quantidade;
  }

  public BigDecimal getPrecoUnitario() {
    return precoUnitario;
  }

  public void setIdItemPedido(Long idItemPedido) {
    this.idItemPedido = idItemPedido;
  }

  public void setPedido(Pedido pedido) {
    this.pedido = pedido;
  }

  public void setProduto(Produto produto) {
    this.produto = produto;
  }

  public void setQuantidade(Integer quantidade) {
    this.quantidade = quantidade;
  }

  public void setPrecoUnitario(BigDecimal precoUnitario) {
    this.precoUnitario = precoUnitario;
  }
}