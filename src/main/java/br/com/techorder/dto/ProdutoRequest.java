package br.com.techorder.dto;

import java.math.BigDecimal;

public record ProdutoRequest(String nome, String descricao, BigDecimal preco,
                             Integer quantidadeEstoque, String imagemUrl, Boolean ativo) {
}
