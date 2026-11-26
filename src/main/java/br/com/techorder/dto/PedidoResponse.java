package br.com.techorder.dto;

import br.com.techorder.enums.StatusPedido;
import br.com.techorder.model.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(Long id, Long clienteId, Long enderecoId, Long funcionarioId,
                             LocalDateTime dataPedido, BigDecimal valorTotal, StatusPedido status,
                             List<ItemPedidoResponse> itens) {

  public static PedidoResponse de(Pedido p) {
    return new PedidoResponse(
        p.getIdPedido(),
        p.getCliente() == null ? null : p.getCliente().getIdCliente(),
        p.getEndereco() == null ? null : p.getEndereco().getIdEndereco(),
        p.getFuncionario() == null ? null : p.getFuncionario().getIdFuncionario(),
        p.getDataPedido(),
        p.getValorTotal(),
        p.getStatus(),
        p.getItens().stream().map(ItemPedidoResponse::de).toList());
  }
}
