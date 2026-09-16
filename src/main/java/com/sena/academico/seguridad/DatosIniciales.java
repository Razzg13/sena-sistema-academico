package com.sena.academico.seguridad;

import com.sena.academico.auditoria.Accion;
import com.sena.academico.auditoria.AuditoriaService;
import com.sena.academico.seguridad.RolNombre;
import com.sena.academico.seguridad.Usuario;
import com.sena.academico.seguridad.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

/**
 * Crea usuarios de arranque para poder entrar a la aplicacion la primera vez.
 * El perfil de instructor (tabla instructores) para INSTRUCTOR_DEMO se crea aparte,
 * ver instructor.DatosInicialesInstructor (se ejecuta despues, @Order(2)).
 */
@Component
@Order(1)
public class DatosIniciales implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosIniciales.class);
    private static final String CORREO_ADMIN = "admin@sena.edu.co";
    private static final String CLAVE_ADMIN = "admin123";
    public static final String CORREO_INSTRUCTOR_DEMO = "instructor.demo@sena.edu.co";
    private static final String CLAVE_INSTRUCTOR_DEMO = "instructor123";
    public static final String CORREO_APRENDIZ_DEMO = "aprendiz.demo@sena.edu.co";
    private static final String CLAVE_APRENDIZ_DEMO = "aprendiz123";
    private static final String CORREO_COORDINACION_DEMO = "coordinacion.demo@sena.edu.co";
    private static final String CLAVE_COORDINACION_DEMO = "coordinacion123";
    private static final String CORREO_AUDITOR_DEMO = "auditor.demo@sena.edu.co";
    private static final String CLAVE_AUDITOR_DEMO = "auditor123";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public DatosIniciales(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                           AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    @Override
    public void run(String... args) {
        crearSiNoExiste(CORREO_ADMIN, "0000000001", "Administrador del Sistema", CLAVE_ADMIN,
                EnumSet.of(RolNombre.ADMINISTRADOR));
        crearSiNoExiste(CORREO_INSTRUCTOR_DEMO, "0000000002", "Instructor Demo", CLAVE_INSTRUCTOR_DEMO,
                EnumSet.of(RolNombre.INSTRUCTOR));
        crearSiNoExiste(CORREO_APRENDIZ_DEMO, "0000000003", "Aprendiz Demo", CLAVE_APRENDIZ_DEMO,
                EnumSet.of(RolNombre.APRENDIZ));
        crearSiNoExiste(CORREO_COORDINACION_DEMO, "0000000004", "Coordinacion Demo", CLAVE_COORDINACION_DEMO,
                EnumSet.of(RolNombre.COORDINACION_ACADEMICA));
        crearSiNoExiste(CORREO_AUDITOR_DEMO, "0000000005", "Auditor Demo", CLAVE_AUDITOR_DEMO,
                EnumSet.of(RolNombre.CONSULTA_AUDITOR));
    }

    private void crearSiNoExiste(String correo, String documento, String nombre, String clave,
                                  EnumSet<RolNombre> roles) {
        if (usuarioRepository.existsByCorreo(correo)) {
            return;
        }
        Usuario usuario = new Usuario(documento, nombre, correo, passwordEncoder.encode(clave), roles);
        usuario = usuarioRepository.save(usuario);
        auditoriaService.registrar(Accion.CREACION, "Usuario", usuario.getId(),
                "Creacion automatica de usuario inicial (" + roles + ") al arrancar la aplicacion.");
        log.info("Usuario creado -> correo: {} / clave: {} / roles: {}", correo, clave, roles);
    }
}
