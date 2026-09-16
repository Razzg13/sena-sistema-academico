package com.sena.academico.instructor;

import com.sena.academico.seguridad.UsuarioRepository;
import com.sena.academico.seguridad.DatosIniciales;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Crea el perfil de instructor para el usuario INSTRUCTOR_DEMO sembrado en
 * {@link DatosIniciales}, para poder probar la asignacion de instructores a fichas
 * sin tener que crear el perfil manualmente en cada arranque limpio.
 */
@Component
@Order(2)
public class DatosInicialesInstructor implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosInicialesInstructor.class);

    private final InstructorService instructorService;
    private final UsuarioRepository usuarioRepository;

    public DatosInicialesInstructor(InstructorService instructorService, UsuarioRepository usuarioRepository) {
        this.instructorService = instructorService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        usuarioRepository.findByCorreo(DatosIniciales.CORREO_INSTRUCTOR_DEMO).ifPresent(usuario -> {
            boolean yaExiste = instructorService.listarActivos().stream()
                    .anyMatch(i -> i.getUsuario().getId().equals(usuario.getId()));
            if (!yaExiste) {
                instructorService.crear(usuario.getCorreo(), "Desarrollo de Software");
                log.info("Perfil de instructor demo creado para {}", usuario.getCorreo());
            }
        });
    }
}
