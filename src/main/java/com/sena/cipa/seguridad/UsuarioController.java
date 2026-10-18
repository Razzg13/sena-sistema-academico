package com.sena.cipa.seguridad;

import jakarta.validation.Valid;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        usuarioRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado.");
        return "redirect:/usuarios";
    }
}
