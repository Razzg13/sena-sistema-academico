package com.sena.cipa.common;

import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.RequestDispatcher;
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

    @ExceptionHandler(NegocioException.class)
    public String reglaDeNegocio(NegocioException e, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:" + (e.getDestino() != null ? e.getDestino() : seccionRaiz(request));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String sinPermiso(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error",
                "No tienes permiso para acceder a ese recurso con tu rol o con tus fichas asignadas.");
        return "redirect:/inicio";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String archivoMuyGrande(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "El archivo supera el tamano maximo permitido (5 MB).");
        // El limite se detecta al leer el formulario, antes del controlador: ahi la URI original llega como atributo del error.
        String uri = uriOriginal(request);
        return "redirect:" + (uri.endsWith("/entregar") ? uri.substring(0, uri.length() - "/entregar".length())
                : seccionRaiz(request));
    }

    private String seccionRaiz(HttpServletRequest request) {
        String[] partes = uriOriginal(request).split("/");
        return partes.length > 1 && !partes[1].isBlank() ? "/" + partes[1] : "/inicio";
    }

    private String uriOriginal(HttpServletRequest request) {
        Object original = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        return original instanceof String uri ? uri : request.getRequestURI();
    }
}
