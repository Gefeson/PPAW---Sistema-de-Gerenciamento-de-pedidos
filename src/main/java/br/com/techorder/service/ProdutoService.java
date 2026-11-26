package br.com.techorder.service;

import br.com.techorder.dao.ProdutoDAO;
import br.com.techorder.database.Transacao;
import br.com.techorder.dto.ProdutoRequest;
import br.com.techorder.dto.ProdutoResponse;
import br.com.techorder.exception.ApiException;
import br.com.techorder.model.Produto;
import br.com.techorder.util.Validacao;

import java.util.List;

public class ProdutoService {

  private final ProdutoDAO produtoDAO;

  public ProdutoService(ProdutoDAO produtoDAO) {
    this.produtoDAO = produtoDAO;
  }

  public ProdutoResponse criar(ProdutoRequest req) {
    Produto produto = new Produto();
    produto.setNome(Validacao.texto(req.nome(), "nome", 150));
    produto.setDescricao(req.descricao());
    produto.setPreco(Validacao.preco(req.preco()));
    produto.setQuantidadeEstoque(Validacao.estoque(req.quantidadeEstoque()));
    produto.setImagemUrl(req.imagemUrl());
    produto.setAtivo(req.ativo() == null || req.ativo());

    return Transacao.executar(conn -> ProdutoResponse.de(produtoDAO.salvar(conn, produto)));
  }

  /** Clientes e visitantes veem só produtos ativos; funcionários veem todos. */
  public List<ProdutoResponse> listar(boolean incluirInativos) {
    return Transacao.executar(conn ->
        produtoDAO.listar(conn, !incluirInativos).stream().map(ProdutoResponse::de).toList());
  }

  public ProdutoResponse buscar(Long id, boolean incluirInativos) {
    return Transacao.executar(conn -> {
      Produto produto = produtoDAO.buscarPorId(conn, id);
      if (produto == null || (!incluirInativos && !Boolean.TRUE.equals(produto.getAtivo()))) {
        throw ApiException.naoEncontrado("Produto não encontrado: " + id);
      }
      return ProdutoResponse.de(produto);
    });
  }

  /** Atualização parcial: só os campos enviados são alterados. */
  public ProdutoResponse atualizar(Long id, ProdutoRequest req) {
    return Transacao.executar(conn -> {
      Produto produto = produtoDAO.buscarPorId(conn, id);
      if (produto == null) {
        throw ApiException.naoEncontrado("Produto não encontrado: " + id);
      }

      if (req.nome() != null) {
        produto.setNome(Validacao.texto(req.nome(), "nome", 150));
      }
      if (req.descricao() != null) {
        produto.setDescricao(req.descricao());
      }
      if (req.preco() != null) {
        produto.setPreco(Validacao.preco(req.preco()));
      }
      if (req.quantidadeEstoque() != null) {
        produto.setQuantidadeEstoque(Validacao.estoque(req.quantidadeEstoque()));
      }
      if (req.imagemUrl() != null) {
        produto.setImagemUrl(req.imagemUrl());
      }
      if (req.ativo() != null) {
        produto.setAtivo(req.ativo());
      }

      produtoDAO.atualizar(conn, produto);
      return ProdutoResponse.de(produto);
    });
  }

  public void excluir(Long id) {
    Transacao.executar(conn -> {
      if (produtoDAO.buscarPorId(conn, id) == null) {
        throw ApiException.naoEncontrado("Produto não encontrado: " + id);
      }
      produtoDAO.excluir(conn, id);
      return null;
    });
  }
}
