package com.example.Proyecto;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication  
public class ProyectoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProyectoApplication.class, args);
    }

    @Bean
    public CommandLineRunner verifyDatabase(DataSource dataSource) {
        return args -> {
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(5)) {
                    System.out.println("[DB] Conexión SQLite válida: " + connection.getMetaData().getURL());
                } else {
                    System.err.println("[DB] No se pudo validar la conexión SQLite.");
                }
            } catch (Exception error) {
                System.err.println("[DB] Error al conectar con SQLite: " + error.getMessage());
                error.printStackTrace();
            }
        };
    }
}