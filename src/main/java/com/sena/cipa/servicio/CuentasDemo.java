package com.sena.cipa.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sena.cipa.modelo.RolNombre;
import com.sena.cipa.repositorio.UsuarioRepository;

/**
 * Cuentas de prueba del sistema. En modo demo (base H2 o app.cuentas-demo=true)
 * se muestran en la pantalla de login y se dejan siempre con su clave conocida.
 * En produccion (Railway) el modo demo esta apagado.
 */
@Component
public class CuentasDemo {

    public record Cuenta(String documento, String nombre, String correo, String clave, RolNombre rol,
                         String descripcion, boolean seCreaSola) {
    }

    /** Las 5 primeras se crean al arrancar; las otras vienen en el script de datos de ejemplo. */
    public static final List<Cuenta> CUENTAS = List.of(
            new Cuenta("0000000001", "Administrador del Sistema", "admin@sena.edu.co", "admin123", RolNombre.ADMINISTRADOR, "Administrador", true),
            new Cuenta("0000000002", "Coordinacion Demo", "coordinacion.demo@sena.edu.co", "coordinacion123", RolNombre.COORDINACION_ACADEMICA, "Coordinacion", true),
            new Cuenta("0000000003", "Instructor Demo", "instructor.demo@sena.edu.co", "instructor123", RolNombre.INSTRUCTOR, "Instructor sin fichas", true),
            new Cuenta("0000000004", "Aprendiz Demo", "aprendiz.demo@sena.edu.co", "aprendiz123", RolNombre.APRENDIZ, "Aprendiz sin ficha", true),
            new Cuenta("0000000005", "Auditor Demo", "auditor.demo@sena.edu.co", "auditor123", RolNombre.CONSULTA_AUDITOR, "Auditor", true),
            new Cuenta("1010101", "Ines Instructora", "ines@sena.edu.co", "sena1234", RolNombre.INSTRUCTOR, "Instructora ficha 2900001", false),
            new Cuenta("1010102", "Carlos Contador", "carlos@sena.edu.co", "sena1234", RolNombre.INSTRUCTOR, "Instructor ficha 2900002", false),
            new Cuenta("2020201", "Alberto Aprendiz", "alberto@correo.com", "sena1234", RolNombre.APRENDIZ, "Aprendiz ficha 2900001", false),
            new Cuenta("2020202", "Beatriz Aprendiz", "beatriz@correo.com", "sena1234", RolNombre.APRENDIZ, "Aprendiz ficha 2900001", false),
            new Cuenta("2020203", "Camilo Aprendiz", "camilo@correo.com", "sena1234", RolNombre.APRENDIZ, "Aprendiz ficha 2900002", false)
    );

    private final boolean activo;
    private final UsuarioRepository usuarioRepository;

    public CuentasDemo(@Value("${app.cuentas-demo:false}") boolean forzado,
                       @Value("${spring.datasource.url:}") String urlBase,
                       UsuarioRepository usuarioRepository) {
        this.activo = forzado || urlBase.startsWith("jdbc:h2:");
        this.usuarioRepository = usuarioRepository;
    }

    public boolean activo() {
        return activo;
    }

    /** Cuentas que existen en la base, para mostrarlas en el login (vacio fuera del modo demo). */
    public List<Cuenta> visibles() {
        if (!activo) {
            return List.of();
        }
        return CUENTAS.stream().filter(c -> usuarioRepository.existsByCorreo(c.correo())).toList();
    }
}
