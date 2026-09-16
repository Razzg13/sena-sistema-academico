package com.sena.academico.reporte;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Encabezado comun para todos los reportes PDF (RF-14: "encabezado, filtros y
 * fecha de generacion").
 */
final class PdfEncabezadoHelper {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Font FUENTE_MARCA = new Font(Font.HELVETICA, 10, Font.NORMAL);
    private static final Font FUENTE_TITULO = new Font(Font.HELVETICA, 16, Font.BOLD);
    private static final Font FUENTE_FILTROS = new Font(Font.HELVETICA, 9, Font.ITALIC);

    private PdfEncabezadoHelper() {
    }

    static void escribir(Document documento, String titulo, String filtros) throws com.lowagie.text.DocumentException {
        Paragraph marca = new Paragraph("SENA | Sistema Académico", FUENTE_MARCA);
        documento.add(marca);

        Paragraph tituloParrafo = new Paragraph(titulo, FUENTE_TITULO);
        tituloParrafo.setSpacingBefore(4f);
        tituloParrafo.setSpacingAfter(4f);
        documento.add(tituloParrafo);

        Paragraph fecha = new Paragraph(
                "Fecha de generación: " + LocalDateTime.now().format(FORMATO_FECHA), FUENTE_FILTROS);
        documento.add(fecha);

        if (filtros != null && !filtros.isBlank()) {
            Paragraph filtrosParrafo = new Paragraph("Filtros: " + filtros, FUENTE_FILTROS);
            filtrosParrafo.setSpacingAfter(12f);
            documento.add(filtrosParrafo);
        } else {
            Paragraph espaciador = new Paragraph(" ");
            espaciador.setSpacingAfter(8f);
            documento.add(espaciador);
        }
    }

    static Font fuenteEncabezadoTabla() {
        return new Font(Font.HELVETICA, 10, Font.BOLD);
    }

    static Font fuenteCelda() {
        return new Font(Font.HELVETICA, 9, Font.NORMAL);
    }

    static Element tituloSeccion(String texto) {
        Paragraph p = new Paragraph(texto, new Font(Font.HELVETICA, 12, Font.BOLD));
        p.setSpacingBefore(14f);
        p.setSpacingAfter(6f);
        return p;
    }
}
