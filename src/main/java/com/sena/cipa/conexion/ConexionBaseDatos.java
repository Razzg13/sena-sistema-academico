package com.sena.cipa.conexion;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class ConexionBaseDatos {

    private static final Logger log = LoggerFactory.getLogger(ConexionBaseDatos.class);

    @Bean
    public DataSource dataSource(DataSourceProperties datos) {
        log.info("Conectando a la base de datos: {} (usuario: {})", datos.determineUrl(), datos.determineUsername());
        return datos.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    public ApplicationRunner comprobarConexion(DataSource dataSource) {
        return args -> {
            try (Connection conexion = dataSource.getConnection()) {
                DatabaseMetaData info = conexion.getMetaData();
                log.info("Base de datos conectada: {} {} -> {}", info.getDatabaseProductName(),
                        info.getDatabaseProductVersion(), info.getURL());
            }
        };
    }
}
