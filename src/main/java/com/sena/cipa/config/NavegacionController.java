package com.sena.cipa.config;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.sena.cipa.dashboard.DashboardService;
import com.sena.cipa.seguridad.UsuarioPrincipal;

@Controller
public class NavegacionController {

    private final DashboardService dashboardService;

    public NavegacionController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping({"/", "/inicio"})
    public String inicio(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        model.addAttribute("usuario", principal.getUsuario());
        model.addAttribute("dashboard", dashboardService.construir(principal.getUsuario()));
        return "inicio";
    }
}
