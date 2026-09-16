package com.sena.academico.config;

import com.sena.academico.common.NegocioException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(NegocioException.class)
    public RedirectView manejarNegocioException(NegocioException ex, HttpServletRequest request,
                                                  RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        String volver = request.getHeader("Referer");
        return new RedirectView(volver != null ? volver : "/inicio");
    }
}
