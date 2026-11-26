package br.com.techorder.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import br.com.techorder.enums.FormaPagamento;
import br.com.techorder.enums.StatusPagamento;

public class Pagamento {
  private Long idPagamento;
  private Pedido pedido;
  private FormaPagamento formaPagamento;
  private BigDecimal valor;
  private StatusPagamento status;
  private LocalDateTime dataPagamento;

  public Pagamento() {
  }

  //construtor
  public Pagamento(Long idPagamento, Pedido pedido, FormaPagamento formaPagamento, BigDecimal valor, StatusPagamento status, LocalDateTime dataPagamento) {
    this.idPagamento = idPagamento;
    this.pedido = pedido;
    this.formaPagamento = formaPagamento;
    this.valor = valor;
    this.status = status;
    this.dataPagamento = dataPagamento;
  }

  public boolean pagamentoAprovado() {
    return status == StatusPagamento.APROVADO;
  }

  // getters e setters
  public Long getIdPagamento() {
    return idPagamento;
  }

  public Pedido getPedido() {
    return pedido;
  }

  public FormaPagamento getFormaPagamento() {
    return formaPagamento;
  }

  public BigDecimal getValor() {
    return valor;
  }

  public StatusPagamento getStatus() {
    return status;
  }

  public LocalDateTime getDataPagamento() {
    return dataPagamento;
  }

  public void setIdPagamento(Long idPagamento) {
    this.idPagamento = idPagamento;
  }

  public void setPedido(Pedido pedido) {
    this.pedido = pedido;
  }

  public void setFormaPagamento(FormaPagamento formaPagamento) {
    this.formaPagamento = formaPagamento;
  }

  public void setValor(BigDecimal valor) {
    this.valor = valor;
  }

  public void setStatus(StatusPagamento status) {
    this.status = status;
  }

  public void setDataPagamento(LocalDateTime dataPagamento) {
    this.dataPagamento = dataPagamento;
  }

}
