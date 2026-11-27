package br.com.techorder.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fábrica de conexões. Os valores podem ser trocados por variáveis de ambiente,
 * assim a senha do banco não precisa ficar no código-fonte:
 * TECHORDER_DB_URL, TECHORDER_DB_USER, TECHORDER_DB_PASSWORD.
 */
public class ConnectionFactory {

    private static final String URL =
        env("TECHORDER_DB_URL", "jdbc:postgresql://localhost:1234/techorder_db");

    private static final String USER = env("TECHORDER_DB_USER", "postgres");

    private static final String PASSWORD = env("TECHORDER_DB_PASSWORD", "Ge123456");

    public static Connection conectar() throws SQLException {
      return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String env(String chave, String padrao) {
      String valor = System.getenv(chave);
      return (valor == null || valor.isBlank()) ? padrao : valor;
    }
}
