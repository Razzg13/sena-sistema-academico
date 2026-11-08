package com.sena.cipa.dto;

import java.util.List;

public record Dashboard(String titulo, String subtitulo, String aviso, List<Metrica> metricas,
                        List<Medidor> medidores, List<Lista> listas) {

    public record Metrica(String valor, String etiqueta, String detalle, String tono) {
    }

    public record Medidor(String etiqueta, int porcentaje, String detalle, String tono) {
    }

    public record Lista(String titulo, String vacio, List<Item> items) {
    }

    public record Item(String titulo, String detalle, String etiqueta, String claseEtiqueta, String enlace) {
    }
}
