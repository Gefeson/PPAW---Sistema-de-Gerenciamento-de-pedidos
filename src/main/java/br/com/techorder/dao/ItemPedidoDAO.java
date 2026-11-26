package br.com.techorder.dao;

import br.com.techorder.exception.ErroBancoException;
import br.com.techorder.model.ItemPedido;
import br.com.techorder.model.Pedido;
import br.com.techorder.model.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemPedidoDAO {

  public ItemPedido salvar(Connection conn, ItemPedido item) {
    String sql = """
      INSERT INTO item_pedido (id_pedido, id_produto, quantidade, preco_unitario)
      VALUES (?, ?, ?, ?)
      RETURNING id_item
      """;

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setLong(1, item.getPedido().getIdPedido());
      stmt.setLong(2, item.getProduto().getIdProduto());
      stmt.setInt(3, item.getQuantidade());
      stmt.setBigDecimal(4, item.getPrecoUnitario());

      try (ResultSet rs = stmt.executeQuery()) {
        rs.next();
        item.setIdItemPedido(rs.getLong(1));
      }
      return item;
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao salvar item do pedido", e);
    }
  }

  /** Itens de um pedido, ordenados pelo id do produto (ordem fixa evita deadlock ao travar estoques). */
  public List<ItemPedido> listarPorPedido(Connection conn, Pedido pedido) {
    String sql = """
      SELECT i.id_item, i.quantidade, i.preco_unitario, p.id_produto, p.nome
      FROM item_pedido i
      JOIN produto p ON p.id_produto = i.id_produto
      WHERE i.id_pedido = ?
      ORDER BY p.id_produto
      """;

    List<ItemPedido> itens = new ArrayList<>();
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setLong(1, pedido.getIdPedido());
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Produto produto = new Produto();
          produto.setIdProduto(rs.getLong("id_produto"));
          produto.setNome(rs.getString("nome"));

          ItemPedido item = new ItemPedido();
          item.setIdItemPedido(rs.getLong("id_item"));
          item.setPedido(pedido);
          item.setProduto(produto);
          item.setQuantidade(rs.getInt("quantidade"));
          item.setPrecoUnitario(rs.getBigDecimal("preco_unitario"));
          itens.add(item);
        }
      }
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao listar itens do pedido", e);
    }
    return itens;
  }
}
