package br.com.techorder.dto;

import br.com.techorder.model.Produto;

import java.math.BigDecimal;

public record ProdutoResponse(Long id, String nome, String descricao, BigDecimal preco,
                              Integer quantidadeEstoque, String imagemUrl, Boolean ativo) {

  public static ProdutoResponse de(Produto p) {
    return new ProdutoResponse(p.getIdProduto(), p.getNome(), p.getDescricao(), p.getPreco(),
        p.getQuantidadeEstoque(), p.getImagemUrl(), p.getAtivo());
  }
}
