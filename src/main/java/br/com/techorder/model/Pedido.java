package br.com.techorder.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import br.com.techorder.enums.StatusPedido;
import br.com.techorder.exception.RegraNegocioException;

public class Pedido {
  private Long idPedido;
  private Cliente cliente;
  private Endereco endereco;
  private Funcionario funcionario;
  private LocalDateTime dataPedido;
  private BigDecimal valorTotal;
  private StatusPedido status;
  private List<ItemPedido> itens;

  public Pedido() {
    this.itens = new ArrayList<>();
    this.dataPedido = LocalDateTime.now();
    this.status = StatusPedido.PENDENTE;
    this.valorTotal = BigDecimal.ZERO;
  }

  public void adicionarItem(ItemPedido item) {
    itens.add(item);
    calcularTotal();
  }

  public void removerItem(ItemPedido item) {
    itens.remove(item);
    calcularTotal();
  }

  public void calcularTotal() {
    this.valorTotal = itens.stream()
    .map(ItemPedido::getSubtotal)
    .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /** Confirma um pedido pendente (a baixa de estoque é feita pelo PedidoService). */
  public void confirmar(Funcionario funcionario) {
    if (status != StatusPedido.PENDENTE) {
      throw new RegraNegocioException("Somente pedidos PENDENTES podem ser confirmados (status atual: " + status + ")");
    }
    this.funcionario = funcionario;
    this.status = StatusPedido.CONFIRMADO;
  }

  /** Cancela um pedido pendente ou confirmado (a devolução ao estoque é feita pelo PedidoService). */
  public void cancelar(Funcionario funcionario) {
    if (status != StatusPedido.PENDENTE && status != StatusPedido.CONFIRMADO) {
      throw new RegraNegocioException("Pedido com status " + status + " não pode ser cancelado");
    }
    this.funcionario = funcionario;
    this.status = StatusPedido.CANCELADO;
  }

  // getters e setters
  public Long getIdPedido() {
    return idPedido;
  }

  public Cliente getCliente() {
    return cliente;
  }

  public Endereco getEndereco() {
    return endereco;
  }

  public Funcionario getFuncionario() {
    return funcionario;
  }

  public LocalDateTime getDataPedido() {
    return dataPedido;
  }

  public BigDecimal getValorTotal() {
    return valorTotal;
  }

  public StatusPedido getStatus() {
    return status;
  }

  public List<ItemPedido> getItens() {
    return itens;
  }

  public void setIdPedido(Long idPedido) {
    this.idPedido = idPedido;
  }

  public void setCliente(Cliente cliente) {
    this.cliente = cliente;
  }

  public void setEndereco(Endereco endereco) {
    this.endereco = endereco;
  }

  public void setFuncionario(Funcionario funcionario) {
    this.funcionario = funcionario;
  }

  public void setValorTotal(BigDecimal valorTotal) {
    this.valorTotal = valorTotal;
  }

  public void setStatus(StatusPedido status) {
    this.status = status;
  }

  public void setDataPedido(LocalDateTime dataPedido) {
    this.dataPedido = dataPedido;
  }

}
