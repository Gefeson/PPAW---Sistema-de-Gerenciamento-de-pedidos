package br.com.techorder.dao;

import br.com.techorder.enums.PerfilFuncionario;
import br.com.techorder.exception.ErroBancoException;
import br.com.techorder.model.Funcionario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {

  public Funcionario salvar(Connection conn, Funcionario funcionario) {
    String sql = """
      INSERT INTO funcionario (nome, email, senha_hash, perfil)
      VALUES (?, ?, ?, ?)
      RETURNING id_funcionario
      """;

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, funcionario.getNome());
      stmt.setString(2, funcionario.getEmail());
      stmt.setString(3, funcionario.getSenhaHash());
      stmt.setString(4, funcionario.getPerfil().name());

      try (ResultSet rs = stmt.executeQuery()) {
        rs.next();
        funcionario.setIdFuncionario(rs.getLong(1));
      }
      return funcionario;
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao salvar funcionário", e);
    }
  }

  public Funcionario buscarPorId(Connection conn, Long id) {
    return buscarUm(conn, "SELECT * FROM funcionario WHERE id_funcionario = ?", id);
  }

  public Funcionario buscarPorEmail(Connection conn, String email) {
    return buscarUm(conn, "SELECT * FROM funcionario WHERE email = ?", email);
  }

  public List<Funcionario> listarTodos(Connection conn) {
    List<Funcionario> lista = new ArrayList<>();
    try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM funcionario ORDER BY id_funcionario");
         ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        lista.add(mapear(rs));
      }
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao listar funcionários", e);
    }
    return lista;
  }

  public long contar(Connection conn) {
    try (PreparedStatement stmt = conn.prepareStatement("SELECT count(*) FROM funcionario");
         ResultSet rs = stmt.executeQuery()) {
      rs.next();
      return rs.getLong(1);
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao contar funcionários", e);
    }
  }

  public void atualizar(Connection conn, Funcionario funcionario) {
    String sql = """
      UPDATE funcionario
      SET nome = ?, email = ?, senha_hash = ?, perfil = ?
      WHERE id_funcionario = ?
      """;

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, funcionario.getNome());
      stmt.setString(2, funcionario.getEmail());
      stmt.setString(3, funcionario.getSenhaHash());
      stmt.setString(4, funcionario.getPerfil().name());
      stmt.setLong(5, funcionario.getIdFuncionario());
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao atualizar funcionário", e);
    }
  }

  public void excluir(Connection conn, Long id) {
    try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM funcionario WHERE id_funcionario = ?")) {
      stmt.setLong(1, id);
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao excluir funcionário", e);
    }
  }

  private Funcionario buscarUm(Connection conn, String sql, Object parametro) {
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, parametro);
      try (ResultSet rs = stmt.executeQuery()) {
        return rs.next() ? mapear(rs) : null;
      }
    } catch (SQLException e) {
      throw new ErroBancoException("Erro ao buscar funcionário", e);
    }
  }

  private Funcionario mapear(ResultSet rs) throws SQLException {
    Funcionario f = new Funcionario();
    f.setIdFuncionario(rs.getLong("id_funcionario"));
    f.setNome(rs.getString("nome"));
    f.setEmail(rs.getString("email"));
    f.setSenhaHash(rs.getString("senha_hash"));
    f.setPerfil(PerfilFuncionario.valueOf(rs.getString("perfil")));
    return f;
  }
}
