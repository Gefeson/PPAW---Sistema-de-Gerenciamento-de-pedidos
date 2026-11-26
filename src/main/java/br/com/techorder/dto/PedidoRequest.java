package br.com.techorder.dto;

import java.util.List;

/** clienteId só é necessário quando o pedido é registrado por um funcionário. */
public record PedidoRequest(Long clienteId, Long enderecoId, List<ItemPedidoRequest> itens) {
}
