package br.com.techorder.database;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void inicializar() {

        try {
            InputStream arquivo = DatabaseInitializer.class
                    .getClassLoader()
                    .getResourceAsStream("database/schema.sql");

            if (arquivo == null) {
                throw new RuntimeException(
                        "Arquivo schema.sql não encontrado."
                );
            }

            String sql = new String(
                    arquivo.readAllBytes(),
                    StandardCharsets.UTF_8
            );

            try (Connection conexao = ConnectionFactory.conectar();
                 Statement statement = conexao.createStatement()) {

                String[] comandos = sql.split(";");

                for (String comando : comandos) {
                    if (!comando.isBlank()) {
                        statement.execute(comando);
                    }
                }
            }

            System.out.println(
                    "Banco de dados inicializado com sucesso!"
            );

        } catch (Exception e) {
            System.out.println(
                    "Erro ao inicializar o banco de dados:"
            );

            e.printStackTrace();
        }
    }
}

