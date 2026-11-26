package br.com.techorder.dto;

import br.com.techorder.model.ItemPedido;

import java.math.BigDecimal;

public record ItemPedidoResponse(Long produtoId, String produtoNome, Integer quantidade,
                                 BigDecimal precoUnitario, BigDecimal subtotal) {

  public static ItemPedidoResponse de(ItemPedido i) {
    return new ItemPedidoResponse(i.getProduto().getIdProduto(), i.getProduto().getNome(),
        i.getQuantidade(), i.getPrecoUnitario(), i.getSubtotal());
  }
}
