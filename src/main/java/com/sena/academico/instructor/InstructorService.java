package com.sena.academico.instructor;

import com.sena.academico.auditoria.Accion;
import com.sena.academico.auditoria.AuditoriaService;
import com.sena.academico.common.EstadoRegistro;
import com.sena.academico.common.NegocioException;
import com.sena.academico.instructor.Instructor;
import com.sena.academico.instructor.InstructorRepository;
import com.sena.academico.seguridad.RolNombre;
import com.sena.academico.seguridad.Usuario;
import com.sena.academico.seguridad.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;

    public InstructorService(InstructorRepository instructorRepository, UsuarioRepository usuarioRepository,
                              AuditoriaService auditoriaService) {
        this.instructorRepository = instructorRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public Instructor crear(String correoUsuario, String especialidad) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new NegocioException("No existe un usuario con el correo " + correoUsuario + "."));
        if (instructorRepository.existsByUsuarioId(usuario.getId())) {
            throw new NegocioException("Ese usuario ya tiene un perfil de instructor.");
        }
        if (!usuario.getRoles().contains(RolNombre.INSTRUCTOR)) {
            throw new NegocioException("El usuario debe tener el rol INSTRUCTOR asignado antes de crear su perfil.");
        }
        Instructor instructor = new Instructor(usuario, especialidad);
        instructor = instructorRepository.save(instructor);
        auditoriaService.registrar(Accion.CREACION, "Instructor", instructor.getId(),
                "Perfil de instructor creado para " + usuario.getCorreo());
        return instructor;
    }

    public List<Instructor> listarActivos() {
        return instructorRepository.findByEstado(EstadoRegistro.ACTIVO);
    }

    public Instructor obtener(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new NegocioException("El instructor solicitado no existe."));
    }
}
