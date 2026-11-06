package com.sena.cipa.dashboard;

import java.util.List;

/** Modelo de vista generico del resumen de inicio; cada rol arma su propio contenido. */
public record Dashboard(String titulo, String subtitulo, String aviso, List<Metrica> metricas,
                        List<Medidor> medidores, List<Lista> listas) {

    /** tono: "" (neutro), "ok", "alerta" o "critico". */
    public record Metrica(String valor, String etiqueta, String detalle, String tono) {
    }

    public record Medidor(String etiqueta, int porcentaje, String detalle, String tono) {
    }

    public record Lista(String titulo, String vacio, List<Item> items) {
    }

    public record Item(String titulo, String detalle, String etiqueta, String claseEtiqueta, String enlace) {
    }
}
