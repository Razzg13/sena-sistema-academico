package com.sena.academico.seguridad;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/admin/usuarios")
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "seguridad/usuarios";
    }

    @GetMapping("/admin/usuarios/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("form", new NuevoUsuarioForm());
        model.addAttribute("roles", RolNombre.values());
        return "seguridad/nuevo-usuario";
    }

    @PostMapping("/admin/usuarios")
    public String crear(@Valid @ModelAttribute("form") NuevoUsuarioForm form, BindingResult resultado,
                         Model model, RedirectAttributes redirectAttributes) {
        if (resultado.hasErrors()) {
            model.addAttribute("roles", RolNombre.values());
            return "seguridad/nuevo-usuario";
        }
        Usuario usuario = usuarioService.crear(form);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario " + usuario.getCorreo() + " creado correctamente.");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/admin/usuarios/{id}/desactivar")
    public String desactivar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        usuarioService.desactivar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario inactivado.");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/admin/usuarios/{id}/activar")
    public String activar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        usuarioService.activar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario reactivado.");
        return "redirect:/admin/usuarios";
    }
}
