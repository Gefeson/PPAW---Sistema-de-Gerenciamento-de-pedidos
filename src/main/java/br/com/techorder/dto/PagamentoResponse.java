package br.com.techorder.dto;

import br.com.techorder.model.Pagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagamentoResponse(
        Long id,
        Long pedidoId,
        String formaPagamento,
        BigDecimal valor,
        String status,
        LocalDateTime dataPagamento
) {
    public static PagamentoResponse de(Pagamento pagamento) {
        return new PagamentoResponse(
                pagamento.getIdPagamento(),
                pagamento.getPedido().getIdPedido(),
                pagamento.getFormaPagamento().name(),
                pagamento.getValor(),
                pagamento.getStatus().name(),
                pagamento.getDataPagamento()
        );
    }
}