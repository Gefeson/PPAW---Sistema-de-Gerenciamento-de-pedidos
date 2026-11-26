package br.com.techorder.dao;

import br.com.techorder.enums.StatusPedido;
import br.com.techorder.exception.ErroBancoException;
import br.com.techorder.model.Cliente;
import br.com.techorder.model.Endereco;
import br.com.techorder.model.Funcionario;
import br.com.techorder.model.ItemPedido;
import br.com.techorder.model.Pedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

  private final ItemPedidoDAO itemDAO;

  public PedidoDAO(ItemPedidoDAO itemDAO) {
    this.itemDAO = itemDAO;
  }

  /** Salva o pedido e todos os seus itens (use dentro de uma Transacao). */
  public Pedido salvar(Connection conn, Pedido pedido) {
    String sql = """
      INSERT INTO pedido (id_cliente, id_endereco, id_funcionario, data_pedido, valor_total, status)
      VALUES (?, ?, ?, ?, ?, ?)
      RETURNING id_pedido
      """;

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setLong(1, pedido.getCliente().getIdCliente());
      stmt.setLong(2, pedido.getEndereco().getIdEndereco());

      if (pedido.getFuncionario() != null) {
        stmt.setLong(3, pedido.getFuncionario().getIdFuncionario());
      } else {
        stmt.setNull(3, Types.INTEGER);
      }

      stmt.setTimestamp(4, java.sql.Timestamp.valueOf(pedido.getDataPedido()));
      stmt.setBigDecimal(5, pedido.getValorTotal());
      stmt.setString(6, pedido.getStatus().name());

      try (ResultSet rs = stmt.executeQuery()) {
        rs.next();
        pedido.setIdPedido(rs.getLong(1));
      }
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao salvar pedido", e);
    }

    for (ItemPedido item : pedido.getItens()) {
      itemDAO.salvar(conn, item);
    }
    return pedido;
  }

  public Pedido buscarPorId(Connection conn, Long id, boolean bloquear) {
    String sql = "SELECT * FROM pedido WHERE id_pedido = ?" + (bloquear ? " FOR UPDATE" : "");

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setLong(1, id);
      try (ResultSet rs = stmt.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        Pedido pedido = mapear(rs);
        pedido.getItens().addAll(itemDAO.listarPorPedido(conn, pedido));
        return pedido;
      }
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao buscar pedido", e);
    }
  }

  public List<Pedido> listarTodos(Connection conn) {
    return listar(conn, "SELECT * FROM pedido ORDER BY id_pedido", null);
  }

  public List<Pedido> listarPorCliente(Connection conn, Long idCliente) {
    return listar(conn, "SELECT * FROM pedido WHERE id_cliente = ? ORDER BY id_pedido", idCliente);
  }

  /** Atualiza status e o funcionário responsável (quem confirmou/cancelou). */
  public void atualizarStatus(Connection conn, Pedido pedido) {
    String sql = "UPDATE pedido SET status = ?, id_funcionario = ? WHERE id_pedido = ?";

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, pedido.getStatus().name());
      if (pedido.getFuncionario() != null) {
        stmt.setLong(2, pedido.getFuncionario().getIdFuncionario());
      } else {
        stmt.setNull(2, Types.INTEGER);
      }
      stmt.setLong(3, pedido.getIdPedido());
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao atualizar status do pedido", e);
    }
  }

  private List<Pedido> listar(Connection conn, String sql, Long parametro) {
    List<Pedido> pedidos = new ArrayList<>();
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      if (parametro != null) {
        stmt.setLong(1, parametro);
      }
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          pedidos.add(mapear(rs));
        }
      }
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao listar pedidos", e);
    }
    for (Pedido p : pedidos) {
      p.getItens().addAll(itemDAO.listarPorPedido(conn, p));
    }
    return pedidos;
  }

  private Pedido mapear(ResultSet rs) throws SQLException {
    Pedido pedido = new Pedido();
    pedido.setIdPedido(rs.getLong("id_pedido"));
    pedido.setDataPedido(rs.getTimestamp("data_pedido").toLocalDateTime());
    pedido.setValorTotal(rs.getBigDecimal("valor_total"));
    pedido.setStatus(StatusPedido.valueOf(rs.getString("status")));

    Cliente cliente = new Cliente();
    cliente.setIdCliente(rs.getLong("id_cliente"));
    pedido.setCliente(cliente);

    Endereco endereco = new Endereco();
    endereco.setIdEndereco(rs.getLong("id_endereco"));
    pedido.setEndereco(endereco);

    long idFuncionario = rs.getLong("id_funcionario");
    if (!rs.wasNull()) {
      Funcionario funcionario = new Funcionario();
      funcionario.setIdFuncionario(idFuncionario);
      pedido.setFuncionario(funcionario);
    }
    return pedido;
  }
}
