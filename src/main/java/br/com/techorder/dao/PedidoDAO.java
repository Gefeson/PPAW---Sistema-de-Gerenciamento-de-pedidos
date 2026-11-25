package br.com.techorder.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.com.techorder.database.ConnectionFactory;
import br.com.techorder.enums.StatusPedido;
import br.com.techorder.model.Pedido;

public class PedidoDAO {

  public void salvar(Pedido pedido) {

    String sql = """
      INSERT INTO pedido
      (
        id_cliente,
        id_endereco,
        id_funcionario,
        valor_total,
        status
      ) VALUES (?,?,?,?,?)
    """;

    try(Connection conn = ConnectionFactory.conectar();
      PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, pedido.getCliente().getIdCliente());
      stmt.setLong(2, pedido.getEndereco().getIdEndereco());

      if(pedido.getFuncionario() != null) {
        stmt.setLong(3, pedido.getFuncionario().getIdFuncionario());
      } else {
        stmt.setNull(3, Types.INTEGER);
      }

      stmt.setBigDecimal(4, pedido.getValorTotal());
      stmt.setString(5, pedido.getStatus().name());  

      stmt.executeUpdate();

    } catch(SQLException e) {
      throw new RuntimeException("Erro ao salvar pedido: " + e.getMessage(), e);
    }
  }

  public Pedido buscarPorId(Long id) {

    String sql ="SELECT * FROM pedido WHERE id_pedido = ?";

    try(Connection conn = ConnectionFactory.conectar();
      PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, id);

      ResultSet rs = stmt.executeQuery();

      if(rs.next()) {

        Pedido pedido = new Pedido();

        pedido.setIdPedido(rs.getLong("id_pedido"));
        pedido.setDataPedido(rs.getTimestamp("data_pedido").toLocalDateTime());
        pedido.setValorTotal(rs.getBigDecimal("valor_total"));
        pedido.setStatus(StatusPedido.valueOf(rs.getString("status")));

        return pedido;
      }

    } catch(SQLException e) {
      throw new RuntimeException("Erro ao buscar pedido: " + e.getMessage(), e);
    }

    return null;
  }

  public List<Pedido> listarTodos() {

    List<Pedido> pedidos = new ArrayList<>();

    String sql = "SELECT * FROM pedido";

    try(Connection conn = ConnectionFactory.conectar();
      PreparedStatement stmt = conn.prepareStatement(sql);
      ResultSet rs = stmt.executeQuery()) {

      while(rs.next()) {

        Pedido pedido = new Pedido();

        pedido.setIdPedido(rs.getLong("id_pedido"));
        pedido.setDataPedido(rs.getTimestamp("data_pedido").toLocalDateTime());
        pedido.setValorTotal(rs.getBigDecimal("valor_total"));
        pedido.setStatus(StatusPedido.valueOf(rs.getString("status")));

        pedidos.add(pedido);
      }

    } catch(SQLException e) {
      throw new RuntimeException("Erro ao listar pedidos: " + e.getMessage(), e);
    }

    return pedidos;
  }

  public void atualizarStatus(Long idPedido, StatusPedido status) {

    String sql = """
      UPDATE pedido
      SET status = ?
      WHERE id_pedido = ?
    """;

    try(Connection conn = ConnectionFactory.conectar();
      PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, status.name());
      stmt.setLong(2, idPedido);

      stmt.executeUpdate();

    } catch(SQLException e) {
        throw new RuntimeException("Erro ao atualizar status do pedido: " + e.getMessage(), e);
    }
  }

  public void excluir(Long id) {

    String sql = "DELETE FROM pedido WHERE id_pedido = ?";

    try(Connection conn = ConnectionFactory.conectar();
      PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, id);

      stmt.executeUpdate();

    } catch(SQLException e) {
      throw new RuntimeException("Erro ao excluir pedido: " + e.getMessage(), e);
    }
  }
}
