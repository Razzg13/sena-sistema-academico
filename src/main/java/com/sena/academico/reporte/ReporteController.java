package com.sena.academico.reporte;

import com.sena.academico.auditoria.Accion;
import com.sena.academico.auditoria.FiltroAuditoria;
import com.sena.academico.reporte.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/reportes/fichas/{fichaId}/consolidado")
    public ResponseEntity<byte[]> consolidado(@PathVariable Long fichaId) {
        return pdf(reporteService.generarConsolidadoFicha(fichaId), "consolidado-ficha-" + fichaId + ".pdf");
    }

    @GetMapping("/reportes/fichas/{fichaId}/asistencia")
    public ResponseEntity<byte[]> asistencia(@PathVariable Long fichaId) {
        return pdf(reporteService.generarAsistencia(fichaId), "asistencia-ficha-" + fichaId + ".pdf");
    }

    @GetMapping("/reportes/fichas/{fichaId}/calificaciones")
    public ResponseEntity<byte[]> calificaciones(@PathVariable Long fichaId) {
        return pdf(reporteService.generarCalificaciones(fichaId), "calificaciones-ficha-" + fichaId + ".pdf");
    }

    @GetMapping("/reportes/fichas/{fichaId}/evidencias")
    public ResponseEntity<byte[]> evidencias(@PathVariable Long fichaId) {
        return pdf(reporteService.generarEvidencias(fichaId), "evidencias-ficha-" + fichaId + ".pdf");
    }

    @GetMapping("/reportes/auditoria")
    public ResponseEntity<byte[]> trazabilidad(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String entidad,
            @RequestParam(required = false) Accion accion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        FiltroAuditoria filtro = new FiltroAuditoria(usuario, entidad, accion,
                desde != null ? desde.atStartOfDay() : null,
                hasta != null ? hasta.atTime(LocalTime.MAX) : null);
        return pdf(reporteService.generarTrazabilidad(filtro), "trazabilidad.pdf");
    }

    private ResponseEntity<byte[]> pdf(byte[] contenido, String nombreArchivo) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombreArchivo + "\"")
                .body(contenido);
    }
}
