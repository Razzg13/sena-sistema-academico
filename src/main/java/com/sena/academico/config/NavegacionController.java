package com.sena.academico.config;

import com.sena.academico.seguridad.UsuarioPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavegacionController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/inicio")
    public String inicio(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        model.addAttribute("nombreCompleto", principal.getUsuario().getNombreCompleto());
        return "inicio";
    }
}
