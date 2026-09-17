package com.sena.academico.seguridad;

import com.sena.academico.auditoria.Accion;
import com.sena.academico.auditoria.AuditoriaService;
import com.sena.academico.common.NegocioException;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Alta y gestion de cuentas de usuario (RF-01/RF-02). Solo ADMINISTRADOR
 * (ver SecurityConfig, rutas /admin/**): "Configura usuarios, roles..." (seccion 5 del documento).
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                           AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public Usuario crear(NuevoUsuarioForm form) {
        if (usuarioRepository.existsByCorreo(form.getCorreo())) {
            throw new NegocioException("Ya existe un usuario con el correo " + form.getCorreo() + ".");
        }
        if (usuarioRepository.existsByDocumento(form.getDocumento())) {
            throw new NegocioException("Ya existe un usuario con el documento " + form.getDocumento() + ".");
        }
        Usuario usuario = new Usuario(form.getDocumento(), form.getNombreCompleto(), form.getCorreo(),
                passwordEncoder.encode(form.getClave()), form.getRoles());
        usuario = usuarioRepository.save(usuario);
        auditoriaService.registrar(Accion.CREACION, "Usuario", usuario.getId(),
                "Usuario creado: " + usuario.getCorreo() + " (" + usuario.getRoles() + ")");
        return usuario;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll(Sort.by("nombreCompleto"));
    }

    public Usuario obtener(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NegocioException("El usuario solicitado no existe."));
    }

    @Transactional
    public void desactivar(Long id) {
        Usuario usuario = obtener(id);
        usuario.desactivar();
        auditoriaService.registrar(Accion.INACTIVACION, "Usuario", usuario.getId(),
                "Usuario inactivado: " + usuario.getCorreo());
    }

    @Transactional
    public void activar(Long id) {
        Usuario usuario = obtener(id);
        usuario.activar();
        auditoriaService.registrar(Accion.MODIFICACION, "Usuario", usuario.getId(),
                "Usuario reactivado: " + usuario.getCorreo());
    }
}
