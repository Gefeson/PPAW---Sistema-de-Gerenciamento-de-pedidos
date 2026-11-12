package br.com.techorder;

import java.sql.Connection;
import java.sql.SQLException;

import br.com.techorder.database.ConnectionFactory;

public class Main {

    public static void main(String[] args) {

        try (Connection conexao = ConnectionFactory.conectar()) {

            System.out.println("Conexão com PostgreSQL realizada!");

        } catch (SQLException e) {
            System.out.println("Erro ao conectar com o banco:");
            System.out.println(e.getMessage());
        }
    }
}
