package com.sena.cipa.controlador;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.sena.cipa.config.CuentasDemo;
import com.sena.cipa.seguridad.UsuarioPrincipal;
import com.sena.cipa.servicio.DashboardService;

@Controller
public class NavegacionController {

    private final DashboardService dashboardService;
    private final CuentasDemo cuentasDemo;

    public NavegacionController(DashboardService dashboardService, CuentasDemo cuentasDemo) {
        this.dashboardService = dashboardService;
        this.cuentasDemo = cuentasDemo;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("cuentasDemo", cuentasDemo.visibles());
        return "login";
    }

    @GetMapping({"/", "/inicio"})
    public String inicio(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        model.addAttribute("usuario", principal.getUsuario());
        model.addAttribute("dashboard", dashboardService.construir(principal.getUsuario()));
        return "inicio";
    }
}
