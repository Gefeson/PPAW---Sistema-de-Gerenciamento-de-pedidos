package br.com.techorder;

import br.com.techorder.database.DatabaseInitializer;

public class Main {

    public static void main(String[] args) {

        System.out.println("Iniciando TechOrder...");

        DatabaseInitializer.inicializar();

        System.out.println("TechOrder finalizado.");
    }
}
