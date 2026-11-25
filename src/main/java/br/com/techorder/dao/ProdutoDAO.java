package br.com.techorder.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.techorder.database.ConnectionFactory;
import br.com.techorder.model.Produto;

public class ProdutoDAO {

  public void salvar(Produto produto) {

    String sql = """
      INSERT INTO produto(
        nome,
        descricao,
        preco,
        quantidade_estoque,
        imagem_url,
        ativo
      )VALUES (?,?,?,?,?,?)
    """;

    try(Connection conn = ConnectionFactory.conectar();
      PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, produto.getNome());
      stmt.setString(2, produto.getDescricao());
      stmt.setBigDecimal(3, produto.getPreco());
      stmt.setInt(4, produto.getQuantidadeEstoque());
      stmt.setString(5,produto.getImagemUrl());
      stmt.setBoolean(6, produto.getAtivo());

      stmt.executeUpdate();

    } catch(SQLException e) {
      throw new RuntimeException("Erro ao salvar produto: " + e.getMessage(), e);
    }
  }

  public Produto buscarPorId(Long id) {

    String sql = "SELECT * FROM produto WHERE id_produto = ?";

      try(Connection conn = ConnectionFactory.conectar();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setLong(1, id);

        ResultSet rs = stmt.executeQuery();

        if(rs.next()) {

          Produto produto = new Produto();

          produto.setIdProduto(rs.getLong("id_produto"));
          produto.setNome(rs.getString("nome"));
          produto.setDescricao(rs.getString("descricao"));
          produto.setPreco(rs.getBigDecimal("preco"));
          produto.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
          produto.setImagemUrl(rs.getString("imagem_url"));
          produto.setAtivo(rs.getBoolean("ativo"));

          return produto;
        }

      } catch(SQLException e) {
        throw new RuntimeException("Erro ao buscar produto: " + e.getMessage(), e);
      }

      return null;
  }

  public List<Produto> listarTodos() {

    List<Produto> produtos = new ArrayList<>();

    String sql = "SELECT * FROM produto";

    try(Connection conn = ConnectionFactory.conectar();
      PreparedStatement stmt = conn.prepareStatement(sql);
      ResultSet rs = stmt.executeQuery()) {

      while(rs.next()) {

          Produto produto = new Produto();

          produto.setIdProduto(rs.getLong("id_produto"));
          produto.setNome(rs.getString("nome"));
          produto.setDescricao(rs.getString("descricao"));
          produto.setPreco(rs.getBigDecimal("preco"));
          produto.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
          produto.setImagemUrl(rs.getString("imagem_url"));
          produto.setAtivo(rs.getBoolean("ativo"));

          produtos.add(produto);
      }

    } catch(SQLException e) {
      throw new RuntimeException("Erro ao listar produtos: " + e.getMessage(), e);
    }

    return produtos;
  }

  public void atualizar(Produto produto) {

      String sql = """
        UPDATE produto
        SET nome = ?,
        descricao = ?,
        preco = ?,
        quantidade_estoque = ?,
        imagem_url = ?,
        ativo = ?
        WHERE id_produto = ?
      """;

      try(Connection conn = ConnectionFactory.conectar();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, produto.getNome());
        stmt.setString(2, produto.getDescricao());
        stmt.setBigDecimal(3, produto.getPreco());
        stmt.setInt(4, produto.getQuantidadeEstoque());
        stmt.setString(5,produto.getImagemUrl());
        stmt.setBoolean(6, produto.getAtivo());
        stmt.setLong(7, produto.getIdProduto());

        stmt.executeUpdate();

      } catch(SQLException e) {
        throw new RuntimeException("Erro ao atualizar produto: " + e.getMessage(), e);
      }
  }

  public void excluir(Long id) {

    String sql = "DELETE FROM produto WHERE id_produto = ?";

    try(Connection conn = ConnectionFactory.conectar();
      PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, id);
      stmt.executeUpdate();

    } catch(SQLException e) {
      throw new RuntimeException("Erro ao excluir produto: " + e.getMessage(), e);
    }
  }
}
