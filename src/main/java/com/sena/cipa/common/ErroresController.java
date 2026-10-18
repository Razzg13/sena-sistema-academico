package com.sena.cipa.common;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/** Pagina de error amigable para lo que no alcanza a atender el GlobalExceptionHandler (404, limite de subida, etc.). */
@Controller
public class ErroresController implements ErrorController {

    @RequestMapping("/error")
    public String error(HttpServletRequest request, Model model) {
        Object estado = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int codigo = estado instanceof Integer i ? i : 500;
        Throwable causa = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        String mensaje;
        if (esLimiteDeSubida(causa)) {
            codigo = 413;
            mensaje = "El archivo supera el tamano maximo permitido (5 MB). Vuelve atras y elige uno mas liviano.";
        } else if (codigo == 404) {
            mensaje = "La pagina que buscas no existe.";
        } else if (codigo == 403) {
            mensaje = "No tienes permiso para acceder a este recurso.";
        } else {
            mensaje = "Ocurrio un error inesperado. Intenta de nuevo o vuelve al inicio.";
        }
        model.addAttribute("codigo", codigo);
        model.addAttribute("mensaje", mensaje);
        return "error";
    }

    private boolean esLimiteDeSubida(Throwable causa) {
        for (Throwable t = causa; t != null; t = t.getCause()) {
            if (t instanceof MaxUploadSizeExceededException
                    || t.getClass().getSimpleName().contains("SizeLimitExceeded")) {
                return true;
            }
        }
        return false;
    }
}
