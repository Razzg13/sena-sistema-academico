package com.sena.cipa.seguridad;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sena.cipa.common.NegocioException;

/** Cualquier usuario autenticado puede cambiar su propia clave. */
@Controller
@RequestMapping("/cuenta")
public class CuentaController {

    private static final String DESTINO = "/cuenta/clave";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public CuentaController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/clave")
    public String formulario() {
        return "seguridad/cambiar-clave";
    }

    @PostMapping("/clave")
    public String cambiar(@RequestParam String actual, @RequestParam String nueva, @RequestParam String confirmacion,
                           @AuthenticationPrincipal UsuarioPrincipal principal, RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioRepository.findById(principal.getUsuario().getId()).orElseThrow();
        if (!passwordEncoder.matches(actual, usuario.getClave())) {
            throw new NegocioException("La clave actual no es correcta.", DESTINO);
        }
        if (nueva.length() < 6) {
            throw new NegocioException("La clave nueva debe tener al menos 6 caracteres.", DESTINO);
        }
        if (!nueva.equals(confirmacion)) {
            throw new NegocioException("La confirmacion no coincide con la clave nueva.", DESTINO);
        }
        usuario.setClave(passwordEncoder.encode(nueva));
        usuarioRepository.save(usuario);
        redirectAttributes.addFlashAttribute("mensaje", "Clave actualizada. Usala la proxima vez que inicies sesion.");
        return "redirect:/inicio";
    }
}
