package com.sena.cipa.config;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.sena.cipa.seguridad.UsuarioPrincipal;

@Controller
public class NavegacionController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping({"/", "/inicio"})
    public String inicio(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        model.addAttribute("usuario", principal.getUsuario());
        return "inicio";
    }
}
