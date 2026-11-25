package br.com.techorder.dao;

import br.com.techorder.model.Cliente;
import br.com.techorder.database.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

  public void salvar(Cliente cliente) {

      String sql = """
        INSERT INTO cliente
        (nome,email,senha_hash,telefone)
        VALUES (?,?,?,?)
        """;

      try(Connection conn = ConnectionFactory.conectar();
      PreparedStatement stmt = conn.prepareStatement(sql)){

        stmt.setString(1, cliente.getNome());
        stmt.setString(2, cliente.getEmail());
        stmt.setString(3, cliente.getSenhaHash());
        stmt.setString(4, cliente.getTelefone());

        stmt.executeUpdate();

      } catch (SQLException e) {
        throw new RuntimeException("Erro ao salvar cliente: " + e.getMessage(), e);
      }
  }

  public Cliente buscarPorId(Long id) {

    String sql = "SELECT * FROM cliente WHERE id_cliente = ?";

      try(Connection conn = ConnectionFactory.conectar();
      PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setLong(1, id);
        ResultSet rs = stmt.executeQuery();

        if(rs.next()) {

          Cliente cliente = new Cliente();

          cliente.setIdCliente(rs.getLong("id_cliente"));
          cliente.setNome(rs.getString("nome"));
          cliente.setEmail(rs.getString("email"));
          cliente.setSenhaHash(rs.getString("senha_hash"));
          cliente.setTelefone(rs.getString("telefone"));

          return cliente;
        }

      } catch (SQLException e) {
        throw new RuntimeException("Erro ao buscar cliente: " + e.getMessage(), e);
      }

      return null;
  }

  public List<Cliente> listarTodos() {

    List<Cliente> clientes = new ArrayList<>();

    String sql = "SELECT * FROM cliente";

    try(Connection conn = ConnectionFactory.conectar();
      PreparedStatement stmt = conn.prepareStatement(sql);
      ResultSet rs = stmt.executeQuery()) {

        while(rs.next()) {

          Cliente cliente = new Cliente();

          cliente.setIdCliente(rs.getLong("id_cliente"));
          cliente.setNome(rs.getString("nome"));
          cliente.setEmail(rs.getString("email"));
          cliente.setSenhaHash(rs.getString("senha_hash"));
          cliente.setTelefone(rs.getString("telefone"));

          clientes.add(cliente);
        }

    } catch (SQLException e) {
      throw new RuntimeException("Erro ao listar clientes: " + e.getMessage(), e);
    }

    return clientes;
  }

  public void atualizar(Cliente cliente) {

      String sql = 
      """
        UPDATE cliente SET nome = ?, email = ?, senha_hash = ?, telefone = ?WHERE id_cliente = ?
      """;

      try(Connection conn = ConnectionFactory.conectar();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, cliente.getNome());
        stmt.setString(2, cliente.getEmail());
        stmt.setString(3, cliente.getSenhaHash());
        stmt.setString(4, cliente.getTelefone());
        stmt.setLong(5, cliente.getIdCliente());

        stmt.executeUpdate();

      } catch(SQLException e) {
          throw new RuntimeException("Erro ao atualizar cliente: " + e.getMessage(), e);
      }
  }

  public void excluir(Long id) {

    String sql ="DELETE FROM cliente WHERE id_cliente = ?";

    try(Connection conn = ConnectionFactory.conectar();
      PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, id);

      stmt.executeUpdate();

    } catch(SQLException e) {
      throw new RuntimeException("Erro ao excluir cliente: " + e.getMessage(), e);
    }
  }
}