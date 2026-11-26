package br.com.techorder.exception;

import java.sql.SQLException;

/** Envolve SQLException mantendo o SQLState (usado para traduzir duplicidade, FK etc.). */
public class ErroBancoException extends RuntimeException {

  private final String sqlState;

  public ErroBancoException(String mensagem, SQLException causa) {
    super(mensagem + ": " + causa.getMessage(), causa);
    this.sqlState = causa.getSQLState();
  }

  public String getSqlState() {
    return sqlState;
  }
}
