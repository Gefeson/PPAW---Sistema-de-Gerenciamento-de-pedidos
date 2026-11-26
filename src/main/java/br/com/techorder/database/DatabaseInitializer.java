package br.com.techorder.database;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    /** Cria as tabelas (CREATE TABLE IF NOT EXISTS). Lança exceção se algo falhar. */
    public static void inicializar() {

        try (InputStream arquivo = DatabaseInitializer.class
                .getClassLoader()
                .getResourceAsStream("database/schema.sql")) {

            if (arquivo == null) {
                throw new IllegalStateException(
                        "Arquivo database/schema.sql não encontrado no classpath (deve ficar em src/main/resources/database/)."
                );
            }

            String sql = new String(arquivo.readAllBytes(), StandardCharsets.UTF_8);

            try (Connection conexao = ConnectionFactory.conectar();
                 Statement statement = conexao.createStatement()) {

                for (String comando : sql.split(";")) {
                    if (!comando.isBlank()) {
                        statement.execute(comando);
                    }
                }
            }

            System.out.println("Banco de dados inicializado com sucesso!");

        } catch (Exception e) {
            throw new IllegalStateException("Erro ao inicializar o banco de dados: " + e.getMessage(), e);
        }
    }
}
