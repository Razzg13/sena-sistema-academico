package com.sena.academico.aprendiz;

import com.sena.academico.aprendiz.Aprendiz;
import com.sena.academico.aprendiz.EstadoFormativo;
import com.sena.academico.aprendiz.AprendizRepository;
import com.sena.academico.auditoria.Accion;
import com.sena.academico.auditoria.AuditoriaService;
import com.sena.academico.common.EstadoRegistro;
import com.sena.academico.common.NegocioException;
import com.sena.academico.seguridad.RolNombre;
import com.sena.academico.seguridad.Usuario;
import com.sena.academico.seguridad.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AprendizService {

    private final AprendizRepository aprendizRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;

    public AprendizService(AprendizRepository aprendizRepository, UsuarioRepository usuarioRepository,
                            AuditoriaService auditoriaService) {
        this.aprendizRepository = aprendizRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public Aprendiz crear(String correoUsuario) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new NegocioException("No existe un usuario con el correo " + correoUsuario + "."));
        if (aprendizRepository.existsByUsuarioId(usuario.getId())) {
            throw new NegocioException("Ese usuario ya tiene un perfil de aprendiz.");
        }
        if (!usuario.getRoles().contains(RolNombre.APRENDIZ)) {
            throw new NegocioException("El usuario debe tener el rol APRENDIZ asignado antes de crear su perfil.");
        }
        Aprendiz aprendiz = new Aprendiz(usuario);
        aprendiz = aprendizRepository.save(aprendiz);
        auditoriaService.registrar(Accion.CREACION, "Aprendiz", aprendiz.getId(),
                "Perfil de aprendiz creado para " + usuario.getCorreo());
        return aprendiz;
    }

    public List<Aprendiz> listarActivos() {
        return aprendizRepository.findByEstado(EstadoRegistro.ACTIVO);
    }

    public Aprendiz obtener(Long id) {
        return aprendizRepository.findById(id)
                .orElseThrow(() -> new NegocioException("El aprendiz solicitado no existe."));
    }

    @Transactional
    public void cambiarEstadoFormativo(Long id, EstadoFormativo nuevoEstado) {
        Aprendiz aprendiz = obtener(id);
        aprendiz.cambiarEstadoFormativo(nuevoEstado);
        auditoriaService.registrar(Accion.MODIFICACION, "Aprendiz", aprendiz.getId(),
                "Estado formativo cambiado a " + nuevoEstado);
    }
}
