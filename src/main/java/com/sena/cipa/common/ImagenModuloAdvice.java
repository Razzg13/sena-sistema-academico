package com.sena.cipa.common;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ImagenModuloAdvice {

    private static final List<String> IMAGENES = List.of(
            "/img/modulos/modulo-taller.png",
            "/img/modulos/modulo-guaviare.webp",
            "/img/modulos/modulo-fachada-roja.png",
            "/img/modulos/modulo-etnico.png",
            "/img/modulos/modulo-industrias-creativas.webp"
    );

    @ModelAttribute("imagenModulo")
    public String imagenAleatoria() {
        int indice = ThreadLocalRandom.current().nextInt(IMAGENES.size());
        return IMAGENES.get(indice);
    }
}
