package com.sena.academico.aprendiz;

import com.sena.academico.asistencia.Asistencia;
import com.sena.academico.asistencia.SesionFormacion;
import com.sena.academico.asistencia.AsistenciaService;
import com.sena.academico.evaluacion.ActividadEvaluativa;
import com.sena.academico.evaluacion.Evaluacion;
import com.sena.academico.evaluacion.EvaluacionService;
import com.sena.academico.evidencia.Evidencia;
import com.sena.academico.evidencia.EvidenciaService;
import com.sena.academico.matricula.Matricula;
import com.sena.academico.matricula.MatriculaService;
import com.sena.academico.seguridad.UsuarioPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Autogestion del aprendiz (RF: "Aprendiz consulta su informacion, asistencia,
 * actividades, resultados y carga evidencias cuando la actividad lo permita" — seccion 5).
 * Todo aqui esta acotado a la propia matricula del usuario autenticado.
 */
@Controller
public class AutogestionAprendizController {

    private final MatriculaService matriculaService;
    private final AsistenciaService asistenciaService;
    private final EvaluacionService evaluacionService;
    private final EvidenciaService evidenciaService;

    public AutogestionAprendizController(MatriculaService matriculaService, AsistenciaService asistenciaService,
                                          EvaluacionService evaluacionService, EvidenciaService evidenciaService) {
        this.matriculaService = matriculaService;
        this.asistenciaService = asistenciaService;
        this.evaluacionService = evaluacionService;
        this.evidenciaService = evidenciaService;
    }

    @GetMapping("/aprendiz/fichas")
    public String misFichas(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        List<Matricula> matriculas = matriculaService.listarPorAprendizUsuario(principal.getUsuario().getId());
        model.addAttribute("matriculas", matriculas);
        return "aprendiz/fichas";
    }

    @GetMapping("/aprendiz/fichas/{fichaId}")
    public String miFicha(@PathVariable Long fichaId, @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        Matricula matricula = matriculaService.obtenerPropia(fichaId, principal.getUsuario().getId());

        List<SesionFormacion> sesiones = asistenciaService.listarSesionesPorFicha(fichaId);
        Map<Long, Asistencia> asistenciaPorSesion = asistenciaService.listarPorMatricula(matricula.getId()).stream()
                .collect(Collectors.toMap(a -> a.getSesion().getId(), a -> a, (a, b) -> a, HashMap::new));

        List<ActividadEvaluativa> actividades = evaluacionService.listarActividadesPorFicha(fichaId);
        Map<Long, Evaluacion> evaluacionPorActividad = evaluacionService.listarPorMatricula(matricula.getId()).stream()
                .collect(Collectors.toMap(e -> e.getActividad().getId(), e -> e, (a, b) -> a, HashMap::new));
        Map<Long, Evidencia> evidenciaPorActividad = evidenciaService.listarPorMatricula(matricula.getId()).stream()
                .collect(Collectors.toMap(e -> e.getActividad().getId(), e -> e, (a, b) -> a, HashMap::new));

        model.addAttribute("matricula", matricula);
        model.addAttribute("ficha", matricula.getFicha());
        model.addAttribute("sesiones", sesiones);
        model.addAttribute("asistenciaPorSesion", asistenciaPorSesion);
        model.addAttribute("actividades", actividades);
        model.addAttribute("evaluacionPorActividad", evaluacionPorActividad);
        model.addAttribute("evidenciaPorActividad", evidenciaPorActividad);
        return "aprendiz/ficha-detalle";
    }

    @PostMapping("/aprendiz/actividades/{actividadId}/evidencias")
    public String entregarEvidencia(@PathVariable Long actividadId,
                                     @RequestParam Long fichaId,
                                     @RequestParam("archivo") MultipartFile archivo,
                                     @AuthenticationPrincipal UsuarioPrincipal principal,
                                     RedirectAttributes redirectAttributes) {
        Matricula matricula = matriculaService.obtenerPropia(fichaId, principal.getUsuario().getId());
        evidenciaService.entregar(actividadId, matricula.getId(), archivo);
        redirectAttributes.addFlashAttribute("mensaje", "Evidencia entregada correctamente.");
        return "redirect:/aprendiz/fichas/" + fichaId;
    }
}
