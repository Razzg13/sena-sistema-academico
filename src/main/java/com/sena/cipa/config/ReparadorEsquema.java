package com.sena.cipa.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Si la base de datos viene de una version anterior del sistema, sus tablas pueden tener columnas viejas
 * obligatorias que la aplicacion actual no llena; entonces cualquier INSERT falla con "Field 'x' doesn't
 * have a default value". Al arrancar se vuelven opcionales esas columnas sobrantes. No borra datos.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ReparadorEsquema implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ReparadorEsquema.class);

    private final JdbcTemplate jdbc;
    private final EntityManagerFactory entityManagerFactory;

    public ReparadorEsquema(JdbcTemplate jdbc, EntityManagerFactory entityManagerFactory) {
        this.jdbc = jdbc;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void run(String... args) {
        try {
            if (!esMySql()) {
                return;
            }
            for (EntityType<?> tipo : entityManagerFactory.getMetamodel().getEntities()) {
                Table tabla = tipo.getJavaType().getAnnotation(Table.class);
                if (tabla != null) {
                    repararTabla(tabla.name(), columnasConocidas(tipo.getJavaType()));
                }
            }
        } catch (RuntimeException e) {
            log.warn("No se pudo revisar el esquema de la base de datos: {}", e.getMessage());
        }
    }

    private boolean esMySql() {
        String producto = jdbc.execute((java.sql.Connection c) -> c.getMetaData().getDatabaseProductName());
        return producto != null && (producto.contains("MySQL") || producto.contains("MariaDB"));
    }

    private void repararTabla(String tabla, Set<String> conocidas) {
        List<Map<String, Object>> obligatorias = jdbc.queryForList(
                "SELECT COLUMN_NAME, COLUMN_TYPE FROM information_schema.COLUMNS "
                        + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND IS_NULLABLE = 'NO' "
                        + "AND COLUMN_DEFAULT IS NULL AND EXTRA NOT LIKE '%auto_increment%' AND EXTRA NOT LIKE '%GENERATED%'",
                tabla);
        for (Map<String, Object> columna : obligatorias) {
            String nombre = String.valueOf(columna.get("COLUMN_NAME"));
            if (conocidas.contains(nombre.toLowerCase(Locale.ROOT))) {
                continue;
            }
            jdbc.execute("ALTER TABLE `" + tabla + "` MODIFY COLUMN `" + nombre + "` " + columna.get("COLUMN_TYPE") + " NULL");
            log.warn("Columna heredada '{}.{}' ya no se usa: se dejo como opcional.", tabla, nombre);
        }
    }

    private Set<String> columnasConocidas(Class<?> entidad) {
        Set<String> nombres = new HashSet<>();
        for (Field campo : entidad.getDeclaredFields()) {
            if (Modifier.isStatic(campo.getModifiers())) {
                continue;
            }
            JoinColumn union = campo.getAnnotation(JoinColumn.class);
            Column columna = campo.getAnnotation(Column.class);
            String nombre = union != null && !union.name().isBlank() ? union.name()
                    : columna != null && !columna.name().isBlank() ? columna.name()
                    : aSnakeCase(campo.getName());
            nombres.add(nombre.toLowerCase(Locale.ROOT));
        }
        return nombres;
    }

    private String aSnakeCase(String nombre) {
        return nombre.replaceAll("([a-z0-9])([A-Z])", "$1_$2");
    }
}
