package br.com.techorder.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Executa uma operação dentro de uma transação: commit se tudo der certo,
 * rollback se qualquer exceção for lançada. Usado pelos Services.
 */
public class Transacao {

  @FunctionalInterface
  public interface Operacao<T> {
    T executar(Connection conn);
  }

  public static <T> T executar(Operacao<T> operacao) {
    try (Connection conn = ConnectionFactory.conectar()) {
      conn.setAutoCommit(false);
      try {
        T resultado = operacao.executar(conn);
        conn.commit();
        return resultado;
      } catch (RuntimeException e) {
        conn.rollback();
        throw e;
      }
    } catch (SQLException e) {
      throw new br.com.techorder.exception.ErroBancoException("Erro de conexão/transação", e);
    }
  }
}
