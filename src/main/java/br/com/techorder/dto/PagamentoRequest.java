package br.com.techorder.dto;

import br.com.techorder.enums.FormaPagamento;

public record PagamentoRequest(FormaPagamento formaPagamento) {
}