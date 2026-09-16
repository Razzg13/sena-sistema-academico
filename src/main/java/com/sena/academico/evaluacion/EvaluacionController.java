package com.sena.academico.evaluacion;

import com.sena.academico.evaluacion.ActividadEvaluativa;
import com.sena.academico.evaluacion.Evaluacion;
import com.sena.academico.evaluacion.JuicioEvaluacion;
import com.sena.academico.evaluacion.EvaluacionService;
import com.sena.academico.evidencia.EstadoEvidencia;
import com.sena.academico.evidencia.Evidencia;
import com.sena.academico.evidencia.EvidenciaService;
import com.sena.academico.matricula.EstadoMatricula;
import com.sena.academico.matricula.Matricula;
import com.sena.academico.matricula.MatriculaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class EvaluacionController {

    private final EvaluacionService evaluacionService;
    private final MatriculaService matriculaService;
    private final EvidenciaService evidenciaService;

    public EvaluacionController(EvaluacionService evaluacionService, MatriculaService matriculaService,
                                 EvidenciaService evidenciaService) {
        this.evaluacionService = evaluacionService;
        this.matriculaService = matriculaService;
        this.evidenciaService = evidenciaService;
    }

    @PostMapping("/coordinacion/fichas/{fichaId}/actividades")
    public String crearActividad(@PathVariable Long fichaId,
                                  @RequestParam Long resultadoAprendizajeId,
                                  @RequestParam String nombre,
                                  @RequestParam(required = false) String descripcion,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaLimite,
                                  RedirectAttributes redirectAttributes) {
        evaluacionService.crearActividad(fichaId, resultadoAprendizajeId, nombre, descripcion, fechaLimite);
        redirectAttributes.addFlashAttribute("mensaje", "Actividad evaluativa creada.");
        return "redirect:/coordinacion/fichas/" + fichaId;
    }

    @GetMapping("/coordinacion/actividades/{actividadId}/calificar")
    public String calificar(@PathVariable Long actividadId, Model model) {
        ActividadEvaluativa actividad = evaluacionService.obtenerActividad(actividadId);
        List<Matricula> matriculas = matriculaService.listarPorFicha(actividad.getFicha().getId()).stream()
                .filter(m -> m.getEstado() == EstadoMatricula.ACTIVA)
                .toList();
        Map<Long, Evaluacion> evaluacionesPorMatricula = evaluacionService.listarEvaluacionesPorActividad(actividadId).stream()
                .collect(Collectors.toMap(e -> e.getMatricula().getId(), e -> e, (a, b) -> a, HashMap::new));

        Map<Long, Evidencia> evidenciasPorMatricula = evidenciaService.listarPorActividad(actividadId).stream()
                .collect(Collectors.toMap(e -> e.getMatricula().getId(), e -> e, (a, b) -> a, HashMap::new));

        model.addAttribute("actividad", actividad);
        model.addAttribute("matriculas", matriculas);
        model.addAttribute("evaluacionesPorMatricula", evaluacionesPorMatricula);
        model.addAttribute("juicios", JuicioEvaluacion.values());
        model.addAttribute("evidenciasPorMatricula", evidenciasPorMatricula);
        model.addAttribute("estadosEvidencia", EstadoEvidencia.values());
        return "evaluacion/calificar";
    }

    @PostMapping("/coordinacion/actividades/{actividadId}/calificar")
    public String guardarCalificaciones(@PathVariable Long actividadId,
                                         @RequestParam List<Long> matriculaId,
                                         @RequestParam List<String> calificacion,
                                         @RequestParam List<JuicioEvaluacion> juicio,
                                         @RequestParam List<String> retroalimentacion,
                                         RedirectAttributes redirectAttributes) {
        for (int i = 0; i < matriculaId.size(); i++) {
            BigDecimal nota = (calificacion.get(i) == null || calificacion.get(i).isBlank())
                    ? null : new BigDecimal(calificacion.get(i));
            evaluacionService.registrarEvaluacion(actividadId, matriculaId.get(i), nota, juicio.get(i),
                    retroalimentacion.get(i));
        }
        redirectAttributes.addFlashAttribute("mensaje", "Calificaciones guardadas.");
        return "redirect:/coordinacion/actividades/" + actividadId + "/calificar";
    }
}
