package com.sena.cipa.seguridad;

import com.sena.cipa.common.EstadoGeneral;
import com.sena.cipa.common.NegocioException;
import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listado(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "seguridad/usuarios";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("form", new NuevoUsuarioForm());
        model.addAttribute("roles", RolNombre.values());
        return "seguridad/nuevo-usuario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("form") NuevoUsuarioForm form, BindingResult resultado, Model model,
                           RedirectAttributes redirectAttributes) {
        if (usuarioRepository.existsByCorreo(form.getCorreo())) {
            resultado.rejectValue("correo", "correo.duplicado", "Ya existe un usuario con ese correo.");
        }
        if (resultado.hasErrors()) {
            model.addAttribute("roles", RolNombre.values());
            return "seguridad/nuevo-usuario";
        }

        Usuario usuario = new Usuario(form.getDocumento(), form.getNombreCompleto(), form.getCorreo(),
                form.getTelefono(), passwordEncoder.encode(form.getClave()), form.getRol());
        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute("mensaje", "Usuario creado correctamente.");
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal principal,
                                 RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        if (usuario.getId().equals(principal.getUsuario().getId())) {
            throw new NegocioException("No puedes desactivar tu propia cuenta.");
        }
        boolean activar = usuario.getEstado() != EstadoGeneral.ACTIVO;
        if (!activar && esUltimoAdministradorActivo(usuario)) {
            throw new NegocioException("Debe quedar al menos un administrador activo.");
        }
        usuario.setEstado(activar ? EstadoGeneral.ACTIVO : EstadoGeneral.INACTIVO);
        usuarioRepository.save(usuario);
        redirectAttributes.addFlashAttribute("mensaje", activar
                ? "Usuario activado: ya puede iniciar sesion."
                : "Usuario desactivado: ya no puede iniciar sesion.");
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/clave")
    public String formularioClave(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioRepository.findById(id).orElseThrow());
        return "seguridad/clave-usuario";
    }

    @PostMapping("/{id}/clave")
    public String restablecerClave(@PathVariable Long id, @RequestParam String clave,
                                    RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        if (clave == null || clave.length() < 6) {
            throw new NegocioException("La clave debe tener al menos 6 caracteres.", "/usuarios/" + id + "/clave");
        }
        usuario.setClave(passwordEncoder.encode(clave));
        usuarioRepository.save(usuario);
        redirectAttributes.addFlashAttribute("mensaje", "Clave de " + usuario.getNombreCompleto() + " restablecida.");
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal principal,
                            RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        if (usuario.getId().equals(principal.getUsuario().getId())) {
            throw new NegocioException("No puedes eliminar tu propia cuenta.");
        }
        if (esUltimoAdministradorActivo(usuario)) {
            throw new NegocioException("Debe quedar al menos un administrador activo.");
        }
        usuarioRepository.delete(usuario);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado.");
        return "redirect:/usuarios";
    }

    private boolean esUltimoAdministradorActivo(Usuario usuario) {
        return usuario.getRol() == RolNombre.ADMINISTRADOR && usuario.getEstado() == EstadoGeneral.ACTIVO
                && usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() == RolNombre.ADMINISTRADOR && u.getEstado() == EstadoGeneral.ACTIVO)
                .count() <= 1;
    }
}
