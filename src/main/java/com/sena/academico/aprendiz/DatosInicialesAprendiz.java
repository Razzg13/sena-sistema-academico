package com.sena.academico.aprendiz;

import com.sena.academico.seguridad.UsuarioRepository;
import com.sena.academico.seguridad.DatosIniciales;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Crea el perfil de aprendiz para el usuario APRENDIZ_DEMO sembrado en
 * {@link DatosIniciales}, para poder probar el modulo sin crear el perfil manualmente
 * en cada arranque limpio.
 */
@Component
@Order(2)
public class DatosInicialesAprendiz implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosInicialesAprendiz.class);

    private final AprendizService aprendizService;
    private final UsuarioRepository usuarioRepository;

    public DatosInicialesAprendiz(AprendizService aprendizService, UsuarioRepository usuarioRepository) {
        this.aprendizService = aprendizService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        usuarioRepository.findByCorreo(DatosIniciales.CORREO_APRENDIZ_DEMO).ifPresent(usuario -> {
            boolean yaExiste = aprendizService.listarActivos().stream()
                    .anyMatch(a -> a.getUsuario().getId().equals(usuario.getId()));
            if (!yaExiste) {
                aprendizService.crear(usuario.getCorreo());
                log.info("Perfil de aprendiz demo creado para {}", usuario.getCorreo());
            }
        });
    }
}
