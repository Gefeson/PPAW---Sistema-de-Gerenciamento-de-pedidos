package br.com.techorder.dao;

import br.com.techorder.exception.ErroBancoException;
import br.com.techorder.model.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

  public Produto salvar(Connection conn, Produto produto) {
    String sql = """
      INSERT INTO produto (nome, descricao, preco, quantidade_estoque, imagem_url, ativo)
      VALUES (?, ?, ?, ?, ?, ?)
      RETURNING id_produto
      """;

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, produto.getNome());
      stmt.setString(2, produto.getDescricao());
      stmt.setBigDecimal(3, produto.getPreco());
      stmt.setInt(4, produto.getQuantidadeEstoque());
      stmt.setString(5, produto.getImagemUrl());
      stmt.setBoolean(6, produto.getAtivo() == null || produto.getAtivo());

      try (ResultSet rs = stmt.executeQuery()) {
        rs.next();
        produto.setIdProduto(rs.getLong(1));
      }
      return produto;
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao salvar produto", e);
    }
  }

  public Produto buscarPorId(Connection conn, Long id) {
    return buscarUm(conn, "SELECT * FROM produto WHERE id_produto = ?", id);
  }

  /** Igual a buscarPorId, mas trava a linha até o fim da transação (evita venda do mesmo estoque em paralelo). */
  public Produto buscarPorIdParaAtualizar(Connection conn, Long id) {
    return buscarUm(conn, "SELECT * FROM produto WHERE id_produto = ? FOR UPDATE", id);
  }

  public List<Produto> listar(Connection conn, boolean apenasAtivos) {
    List<Produto> produtos = new ArrayList<>();
    String sql = apenasAtivos
        ? "SELECT * FROM produto WHERE ativo = TRUE ORDER BY id_produto"
        : "SELECT * FROM produto ORDER BY id_produto";

    try (PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        produtos.add(mapear(rs));
      }
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao listar produtos", e);
    }
    return produtos;
  }

  public void atualizar(Connection conn, Produto produto) {
    String sql = """
      UPDATE produto
      SET nome = ?, descricao = ?, preco = ?, quantidade_estoque = ?, imagem_url = ?, ativo = ?
      WHERE id_produto = ?
      """;

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, produto.getNome());
      stmt.setString(2, produto.getDescricao());
      stmt.setBigDecimal(3, produto.getPreco());
      stmt.setInt(4, produto.getQuantidadeEstoque());
      stmt.setString(5, produto.getImagemUrl());
      stmt.setBoolean(6, produto.getAtivo() == null || produto.getAtivo());
      stmt.setLong(7, produto.getIdProduto());
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao atualizar produto", e);
    }
  }

  public void atualizarEstoque(Connection conn, Long idProduto, int novaQuantidade) {
    try (PreparedStatement stmt = conn.prepareStatement(
        "UPDATE produto SET quantidade_estoque = ? WHERE id_produto = ?")) {
      stmt.setInt(1, novaQuantidade);
      stmt.setLong(2, idProduto);
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao atualizar estoque", e);
    }
  }

  public void excluir(Connection conn, Long id) {
    try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM produto WHERE id_produto = ?")) {
      stmt.setLong(1, id);
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao excluir produto", e);
    }
  }

  private Produto buscarUm(Connection conn, String sql, Long id) {
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setLong(1, id);
      try (ResultSet rs = stmt.executeQuery()) {
        return rs.next() ? mapear(rs) : null;
      }
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao buscar produto", e);
    }
  }

  private Produto mapear(ResultSet rs) throws SQLException {
    Produto p = new Produto();
    p.setIdProduto(rs.getLong("id_produto"));
    p.setNome(rs.getString("nome"));
    p.setDescricao(rs.getString("descricao"));
    p.setPreco(rs.getBigDecimal("preco"));
    p.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
    p.setImagemUrl(rs.getString("imagem_url"));
    p.setAtivo(rs.getBoolean("ativo"));
    return p;
  }
}
