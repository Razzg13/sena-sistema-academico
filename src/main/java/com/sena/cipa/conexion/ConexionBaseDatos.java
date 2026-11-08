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

/**
 * CONEXION A LA BASE DE DATOS.
 *
 * Aqui se crea la unica conexion que usa toda la aplicacion (un "pool" de conexiones
 * Hikari, que reutiliza las conexiones en vez de abrir una nueva en cada consulta).
 *
 * De donde salen la URL, el usuario y la clave (archivos en src/main/resources):
 *   - application.properties          sin perfil: base H2 en memoria (para pruebas).
 *   - application-mysql.properties     perfil "mysql": MySQL local (DB_URL, DB_USERNAME, DB_PASSWORD).
 *   - application-railway.properties   perfil "railway": MySQL de Railway (variables MYSQL*).
 *
 * Quien usa esta conexion: Hibernate (JPA) la toma para que los DAO de com.sena.cipa.dao
 * puedan guardar y consultar. Solo ReparadorEsquema (config) la usa directo, al arrancar.
 */
@Configuration
public class ConexionBaseDatos {

    private static final Logger log = LoggerFactory.getLogger(ConexionBaseDatos.class);

    /** Crea la conexion con los datos del archivo .properties del perfil activo. */
    @Bean
    public DataSource dataSource(DataSourceProperties datos) {
        log.info("Conectando a la base de datos: {} (usuario: {})", datos.determineUrl(), datos.determineUsername());
        return datos.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    /** Al arrancar, confirma en la consola que la base responde y cual es. */
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
