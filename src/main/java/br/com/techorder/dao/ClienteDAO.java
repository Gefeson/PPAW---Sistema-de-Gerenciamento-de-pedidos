package br.com.techorder.dao;

import br.com.techorder.exception.ErroBancoException;
import br.com.techorder.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

  public Cliente salvar(Connection conn, Cliente cliente) {
    String sql = """
      INSERT INTO cliente (nome, email, senha_hash, telefone)
      VALUES (?, ?, ?, ?)
      RETURNING id_cliente
      """;

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, cliente.getNome());
      stmt.setString(2, cliente.getEmail());
      stmt.setString(3, cliente.getSenhaHash());
      stmt.setString(4, cliente.getTelefone());

      try (ResultSet rs = stmt.executeQuery()) {
        rs.next();
        cliente.setIdCliente(rs.getLong(1));
      }
      return cliente;
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao salvar cliente", e);
    }
  }

  public Cliente buscarPorId(Connection conn, Long id) {
    return buscarUm(conn, "SELECT * FROM cliente WHERE id_cliente = ?", id);
  }

  public Cliente buscarPorEmail(Connection conn, String email) {
    return buscarUm(conn, "SELECT * FROM cliente WHERE email = ?", email);
  }

  public List<Cliente> listarTodos(Connection conn) {
    List<Cliente> clientes = new ArrayList<>();
    try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM cliente ORDER BY id_cliente");
         ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        clientes.add(mapear(rs));
      }
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao listar clientes", e);
    }
    return clientes;
  }

  public void atualizar(Connection conn, Cliente cliente) {
    String sql = """
      UPDATE cliente
      SET nome = ?, email = ?, senha_hash = ?, telefone = ?
      WHERE id_cliente = ?
      """;

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, cliente.getNome());
      stmt.setString(2, cliente.getEmail());
      stmt.setString(3, cliente.getSenhaHash());
      stmt.setString(4, cliente.getTelefone());
      stmt.setLong(5, cliente.getIdCliente());
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao atualizar cliente", e);
    }
  }

  public void excluir(Connection conn, Long id) {
    try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM cliente WHERE id_cliente = ?")) {
      stmt.setLong(1, id);
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao excluir cliente", e);
    }
  }

  private Cliente buscarUm(Connection conn, String sql, Object parametro) {
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, parametro);
      try (ResultSet rs = stmt.executeQuery()) {
        return rs.next() ? mapear(rs) : null;
      }
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao buscar cliente", e);
    }
  }

  private Cliente mapear(ResultSet rs) throws SQLException {
    Cliente cliente = new Cliente();
    cliente.setIdCliente(rs.getLong("id_cliente"));
    cliente.setNome(rs.getString("nome"));
    cliente.setEmail(rs.getString("email"));
    cliente.setSenhaHash(rs.getString("senha_hash"));
    cliente.setTelefone(rs.getString("telefone"));
    return cliente;
  }
}
