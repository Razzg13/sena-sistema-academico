package com.sena.academico.reporte;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sena.academico.asistencia.Asistencia;
import com.sena.academico.asistencia.SesionFormacion;
import com.sena.academico.asistencia.AsistenciaService;
import com.sena.academico.auditoria.RegistroAuditoria;
import com.sena.academico.auditoria.AuditoriaService;
import com.sena.academico.auditoria.FiltroAuditoria;
import com.sena.academico.common.NegocioException;
import com.sena.academico.evaluacion.ActividadEvaluativa;
import com.sena.academico.evaluacion.Evaluacion;
import com.sena.academico.evaluacion.EvaluacionService;
import com.sena.academico.evidencia.Evidencia;
import com.sena.academico.evidencia.EvidenciaService;
import com.sena.academico.ficha.Ficha;
import com.sena.academico.ficha.FichaService;
import com.sena.academico.matricula.EstadoMatricula;
import com.sena.academico.matricula.Matricula;
import com.sena.academico.matricula.MatriculaService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final FichaService fichaService;
    private final MatriculaService matriculaService;
    private final AsistenciaService asistenciaService;
    private final EvaluacionService evaluacionService;
    private final EvidenciaService evidenciaService;
    private final AuditoriaService auditoriaService;

    public ReporteService(FichaService fichaService, MatriculaService matriculaService,
                           AsistenciaService asistenciaService, EvaluacionService evaluacionService,
                           EvidenciaService evidenciaService, AuditoriaService auditoriaService) {
        this.fichaService = fichaService;
        this.matriculaService = matriculaService;
        this.asistenciaService = asistenciaService;
        this.evaluacionService = evaluacionService;
        this.evidenciaService = evidenciaService;
        this.auditoriaService = auditoriaService;
    }

    public byte[] generarConsolidadoFicha(Long fichaId) {
        Ficha ficha = fichaService.obtener(fichaId);
        List<Matricula> matriculasActivas = matriculaService.listarPorFicha(fichaId).stream()
                .filter(m -> m.getEstado() == EstadoMatricula.ACTIVA)
                .toList();
        List<SesionFormacion> sesiones = asistenciaService.listarSesionesPorFicha(fichaId);
        List<ActividadEvaluativa> actividades = evaluacionService.listarActividadesPorFicha(fichaId);

        return generar("Consolidado de ficha " + ficha.getNumero(), "Ficha " + ficha.getNumero(), documento -> {
            try {
                documento.add(new Paragraph("Programa: " + ficha.getPrograma().getCodigo() + " - "
                        + ficha.getPrograma().getNombre()));
                documento.add(new Paragraph("Jornada: " + ficha.getJornada() + "   Estado: " + ficha.getEstado()));
                documento.add(new Paragraph("Fechas: " + ficha.getFechaInicio() + " a " + ficha.getFechaFin()
                        + "   Ambiente: " + ficha.getAmbiente()));

                documento.add(PdfEncabezadoHelper.tituloSeccion("Resumen"));
                PdfPTable tabla = tablaSimple(new String[]{"Indicador", "Valor"});
                agregarFilaSimple(tabla, "Aprendices matriculados (activos)", String.valueOf(matriculasActivas.size()));
                agregarFilaSimple(tabla, "Instructores asignados", String.valueOf(ficha.getAsignaciones().size()));
                agregarFilaSimple(tabla, "Sesiones de formación programadas", String.valueOf(sesiones.size()));
                agregarFilaSimple(tabla, "Actividades evaluativas", String.valueOf(actividades.size()));
                documento.add(tabla);

                documento.add(PdfEncabezadoHelper.tituloSeccion("Aprendices matriculados"));
                if (matriculasActivas.isEmpty()) {
                    documento.add(new Paragraph("Sin aprendices matriculados."));
                } else {
                    PdfPTable tablaAprendices = tablaSimple(new String[]{"Aprendiz", "Correo", "Fecha matrícula"});
                    for (Matricula m : matriculasActivas) {
                        agregarFila(tablaAprendices, m.getAprendiz().getUsuario().getNombreCompleto(),
                                m.getAprendiz().getUsuario().getCorreo(), m.getFechaMatricula().toString());
                    }
                    documento.add(tablaAprendices);
                }
            } catch (DocumentException e) {
                throw new NegocioException("No se pudo generar el reporte.");
            }
        });
    }

    public byte[] generarAsistencia(Long fichaId) {
        Ficha ficha = fichaService.obtener(fichaId);
        List<SesionFormacion> sesiones = asistenciaService.listarSesionesPorFicha(fichaId);

        return generar("Reporte de asistencia", "Ficha " + ficha.getNumero(), documento -> {
            try {
                if (sesiones.isEmpty()) {
                    documento.add(new Paragraph("Esta ficha no tiene sesiones programadas."));
                    return;
                }
                for (SesionFormacion sesion : sesiones) {
                    documento.add(PdfEncabezadoHelper.tituloSeccion(
                            sesion.getFecha() + " (" + sesion.getHoraInicio() + " - " + sesion.getHoraFin()
                                    + ") — " + sesion.getTema()));
                    List<Asistencia> registros = asistenciaService.listarAsistenciasPorSesion(sesion.getId());
                    if (registros.isEmpty()) {
                        documento.add(new Paragraph("Sin asistencia registrada."));
                        continue;
                    }
                    PdfPTable tabla = tablaSimple(new String[]{"Aprendiz", "Estado", "Observación"});
                    for (Asistencia a : registros) {
                        agregarFila(tabla, a.getMatricula().getAprendiz().getUsuario().getNombreCompleto(),
                                a.getEstado().toString(), a.getObservacion() == null ? "" : a.getObservacion());
                    }
                    documento.add(tabla);
                }
            } catch (DocumentException e) {
                throw new NegocioException("No se pudo generar el reporte.");
            }
        });
    }

    public byte[] generarCalificaciones(Long fichaId) {
        Ficha ficha = fichaService.obtener(fichaId);
        List<ActividadEvaluativa> actividades = evaluacionService.listarActividadesPorFicha(fichaId);

        return generar("Reporte de calificaciones", "Ficha " + ficha.getNumero(), documento -> {
            try {
                if (actividades.isEmpty()) {
                    documento.add(new Paragraph("Esta ficha no tiene actividades evaluativas."));
                    return;
                }
                for (ActividadEvaluativa actividad : actividades) {
                    documento.add(PdfEncabezadoHelper.tituloSeccion(
                            actividad.getNombre() + " (RA " + actividad.getResultadoAprendizaje().getCodigo() + ")"));
                    List<Evaluacion> evaluaciones = evaluacionService.listarEvaluacionesPorActividad(actividad.getId());
                    if (evaluaciones.isEmpty()) {
                        documento.add(new Paragraph("Sin calificaciones registradas."));
                        continue;
                    }
                    PdfPTable tabla = tablaSimple(new String[]{"Aprendiz", "Calificación", "Juicio", "Retroalimentación"});
                    for (Evaluacion e : evaluaciones) {
                        agregarFila(tabla, e.getMatricula().getAprendiz().getUsuario().getNombreCompleto(),
                                e.getCalificacion() == null ? "-" : e.getCalificacion().toString(),
                                e.getJuicioEvaluacion().toString(),
                                e.getRetroalimentacion() == null ? "" : e.getRetroalimentacion());
                    }
                    documento.add(tabla);
                }
            } catch (DocumentException ex) {
                throw new NegocioException("No se pudo generar el reporte.");
            }
        });
    }

    public byte[] generarEvidencias(Long fichaId) {
        Ficha ficha = fichaService.obtener(fichaId);
        List<ActividadEvaluativa> actividades = evaluacionService.listarActividadesPorFicha(fichaId);

        return generar("Reporte de evidencias", "Ficha " + ficha.getNumero(), documento -> {
            try {
                boolean hayEvidencias = false;
                PdfPTable tabla = tablaSimple(new String[]{"Actividad", "Aprendiz", "Archivo", "Estado", "Fecha entrega"});
                for (ActividadEvaluativa actividad : actividades) {
                    for (Evidencia ev : evidenciaService.listarPorActividad(actividad.getId())) {
                        hayEvidencias = true;
                        agregarFila(tabla, actividad.getNombre(),
                                ev.getMatricula().getAprendiz().getUsuario().getNombreCompleto(),
                                ev.getArchivo().getNombreOriginal(), ev.getEstado().toString(),
                                ev.getFechaEntrega().toString());
                    }
                }
                documento.add(hayEvidencias ? tabla : new Paragraph("No hay evidencias entregadas en esta ficha."));
            } catch (DocumentException e) {
                throw new NegocioException("No se pudo generar el reporte.");
            }
        });
    }

    public byte[] generarTrazabilidad(FiltroAuditoria filtro) {
        List<RegistroAuditoria> registros = auditoriaService.buscar(filtro,
                Pageable.ofSize(500)).getContent();

        String filtros = describirFiltros(filtro);
        return generar("Reporte de trazabilidad", filtros, documento -> {
            try {
                if (registros.isEmpty()) {
                    documento.add(new Paragraph("No hay registros para los filtros seleccionados."));
                    return;
                }
                PdfPTable tabla = tablaSimple(new String[]{"Fecha", "Usuario", "Acción", "Entidad", "ID", "Detalle"});
                for (RegistroAuditoria r : registros) {
                    agregarFila(tabla, r.getFecha().format(FORMATO_FECHA), r.getUsuario(), r.getAccion().toString(),
                            r.getEntidad(), r.getEntidadId() == null ? "" : r.getEntidadId().toString(),
                            r.getDetalle() == null ? "" : r.getDetalle());
                }
                documento.add(tabla);
            } catch (DocumentException e) {
                throw new NegocioException("No se pudo generar el reporte.");
            }
        });
    }

    private String describirFiltros(FiltroAuditoria filtro) {
        Map<String, String> partes = new java.util.LinkedHashMap<>();
        if (filtro.usuario() != null && !filtro.usuario().isBlank()) partes.put("usuario", filtro.usuario());
        if (filtro.entidad() != null && !filtro.entidad().isBlank()) partes.put("entidad", filtro.entidad());
        if (filtro.accion() != null) partes.put("acción", filtro.accion().toString());
        if (filtro.desde() != null) partes.put("desde", filtro.desde().toLocalDate().toString());
        if (filtro.hasta() != null) partes.put("hasta", filtro.hasta().toLocalDate().toString());
        return partes.isEmpty() ? "sin filtros" : partes.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(", "));
    }

    private byte[] generar(String titulo, String filtros, java.util.function.Consumer<Document> cuerpo) {
        Document documento = new Document(PageSize.A4, 36, 36, 54, 36);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(documento, salida);
            documento.open();
            PdfEncabezadoHelper.escribir(documento, titulo, filtros);
            cuerpo.accept(documento);
        } catch (DocumentException e) {
            throw new NegocioException("No se pudo generar el reporte.");
        } finally {
            documento.close();
        }
        return salida.toByteArray();
    }

    private PdfPTable tablaSimple(String[] encabezados) {
        PdfPTable tabla = new PdfPTable(encabezados.length);
        tabla.setWidthPercentage(100);
        for (String encabezado : encabezados) {
            PdfPCell celda = new PdfPCell(new com.lowagie.text.Phrase(encabezado, PdfEncabezadoHelper.fuenteEncabezadoTabla()));
            celda.setGrayFill(0.9f);
            tabla.addCell(celda);
        }
        return tabla;
    }

    private void agregarFila(PdfPTable tabla, String... valores) {
        for (String valor : valores) {
            tabla.addCell(new PdfPCell(new com.lowagie.text.Phrase(valor, PdfEncabezadoHelper.fuenteCelda())));
        }
    }

    private void agregarFilaSimple(PdfPTable tabla, String etiqueta, String valor) {
        agregarFila(tabla, etiqueta, valor);
    }
}
