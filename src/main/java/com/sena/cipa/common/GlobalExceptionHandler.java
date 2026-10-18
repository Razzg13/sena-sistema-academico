package com.sena.cipa.common;

import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public String noEncontrado(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "El registro solicitado no existe o ya fue eliminado.");
        return "redirect:" + seccionRaiz(request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String conflictoDeDatos(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error",
                "No se pudo completar la accion: el dato ya existe (codigo, numero, correo o documento duplicado) "
                        + "o el registro esta siendo usado por otros datos relacionados.");
        return "redirect:" + seccionRaiz(request);
    }

    private String seccionRaiz(HttpServletRequest request) {
        String[] partes = request.getRequestURI().split("/");
        return partes.length > 1 && !partes[1].isBlank() ? "/" + partes[1] : "/inicio";
    }
}
