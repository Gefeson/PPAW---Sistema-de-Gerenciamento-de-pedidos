package br.com.techorder.dao;

import br.com.techorder.exception.ErroBancoException;
import br.com.techorder.model.Cliente;
import br.com.techorder.model.Endereco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnderecoDAO {

  public Endereco salvar(Connection conn, Endereco e) {
    String sql = """
      INSERT INTO endereco (id_cliente, cep, logradouro, numero, complemento, bairro, cidade, estado)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?)
      RETURNING id_endereco
      """;

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setLong(1, e.getCliente().getIdCliente());
      stmt.setString(2, e.getCep());
      stmt.setString(3, e.getLogradouro());
      stmt.setString(4, e.getNumero());
      stmt.setString(5, e.getComplemento());
      stmt.setString(6, e.getBairro());
      stmt.setString(7, e.getCidade());
      stmt.setString(8, e.getEstado());

      try (ResultSet rs = stmt.executeQuery()) {
        rs.next();
        e.setIdEndereco(rs.getLong(1));
      }
      return e;
    } catch (SQLException ex) {
      throw new ErroBancoException("Erro ao salvar endereço", ex);
    }
  }

  public Endereco buscarPorId(Connection conn, Long id) {
    try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM endereco WHERE id_endereco = ?")) {
      stmt.setLong(1, id);
      try (ResultSet rs = stmt.executeQuery()) {
        return rs.next() ? mapear(rs) : null;
      }
    } catch (SQLException ex) {
      throw new ErroBancoException("Erro ao buscar endereço", ex);
    }
  }

  public List<Endereco> listarPorCliente(Connection conn, Long idCliente) {
    List<Endereco> lista = new ArrayList<>();
    try (PreparedStatement stmt = conn.prepareStatement(
        "SELECT * FROM endereco WHERE id_cliente = ? ORDER BY id_endereco")) {
      stmt.setLong(1, idCliente);
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          lista.add(mapear(rs));
        }
      }
    } catch (SQLException ex) {
      throw new ErroBancoException("Erro ao listar endereços", ex);
    }
    return lista;
  }

  private Endereco mapear(ResultSet rs) throws SQLException {
    Endereco e = new Endereco();
    e.setIdEndereco(rs.getLong("id_endereco"));
    Cliente cliente = new Cliente();
    cliente.setIdCliente(rs.getLong("id_cliente"));
    e.setCliente(cliente);
    e.setCep(rs.getString("cep"));
    e.setLogradouro(rs.getString("logradouro"));
    e.setNumero(rs.getString("numero"));
    e.setComplemento(rs.getString("complemento"));
    e.setBairro(rs.getString("bairro"));
    e.setCidade(rs.getString("cidade"));
    e.setEstado(rs.getString("estado"));
    return e;
  }
}
