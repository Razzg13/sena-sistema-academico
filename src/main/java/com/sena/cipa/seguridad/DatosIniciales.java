package com.sena.cipa.seguridad;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatosIniciales implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosIniciales.class);

    private record UsuarioDemo(String documento, String nombre, String correo, String clave, RolNombre rol) {
    }

    private static final List<UsuarioDemo> USUARIOS_DEMO = List.of(
            new UsuarioDemo("0000000001", "Administrador del Sistema", "admin@sena.edu.co", "admin123", RolNombre.ADMINISTRADOR),
            new UsuarioDemo("0000000002", "Coordinacion Demo", "coordinacion.demo@sena.edu.co", "coordinacion123", RolNombre.COORDINACION_ACADEMICA),
            new UsuarioDemo("0000000003", "Instructor Demo", "instructor.demo@sena.edu.co", "instructor123", RolNombre.INSTRUCTOR),
            new UsuarioDemo("0000000004", "Aprendiz Demo", "aprendiz.demo@sena.edu.co", "aprendiz123", RolNombre.APRENDIZ),
            new UsuarioDemo("0000000005", "Auditor Demo", "auditor.demo@sena.edu.co", "auditor123", RolNombre.CONSULTA_AUDITOR)
    );

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DatosIniciales(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        for (UsuarioDemo demo : USUARIOS_DEMO) {
            if (usuarioRepository.existsByCorreo(demo.correo())) {
                continue;
            }
            Usuario usuario = new Usuario(demo.documento(), demo.nombre(), demo.correo(),
                    null, passwordEncoder.encode(demo.clave()), demo.rol());
            usuarioRepository.save(usuario);
            log.info("Usuario creado -> correo: {} / clave: {} / rol: {}", demo.correo(), demo.clave(), demo.rol());
        }
    }
}
