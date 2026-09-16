package com.sena.academico.asistencia;

import com.sena.academico.asistencia.Asistencia;
import com.sena.academico.asistencia.EstadoAsistencia;
import com.sena.academico.asistencia.SesionFormacion;
import com.sena.academico.asistencia.AsistenciaService;
import com.sena.academico.matricula.Matricula;
import com.sena.academico.matricula.EstadoMatricula;
import com.sena.academico.matricula.MatriculaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AsistenciaController {

    private final AsistenciaService asistenciaService;
    private final MatriculaService matriculaService;

    public AsistenciaController(AsistenciaService asistenciaService, MatriculaService matriculaService) {
        this.asistenciaService = asistenciaService;
        this.matriculaService = matriculaService;
    }

    @PostMapping("/coordinacion/fichas/{fichaId}/sesiones")
    public String programarSesion(@PathVariable Long fichaId,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaInicio,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaFin,
                                   @RequestParam String tema,
                                   RedirectAttributes redirectAttributes) {
        asistenciaService.programarSesion(fichaId, fecha, horaInicio, horaFin, tema);
        redirectAttributes.addFlashAttribute("mensaje", "Sesión programada.");
        return "redirect:/coordinacion/fichas/" + fichaId;
    }

    @GetMapping("/coordinacion/sesiones/{sesionId}/llamado")
    public String llamado(@PathVariable Long sesionId, Model model) {
        SesionFormacion sesion = asistenciaService.obtenerSesion(sesionId);
        List<Matricula> matriculas = matriculaService.listarPorFicha(sesion.getFicha().getId()).stream()
                .filter(m -> m.getEstado() == EstadoMatricula.ACTIVA)
                .toList();
        Map<Long, Asistencia> asistenciasPorMatricula = asistenciaService.listarAsistenciasPorSesion(sesionId).stream()
                .collect(Collectors.toMap(a -> a.getMatricula().getId(), a -> a, (a, b) -> a, HashMap::new));

        model.addAttribute("sesion", sesion);
        model.addAttribute("matriculas", matriculas);
        model.addAttribute("asistenciasPorMatricula", asistenciasPorMatricula);
        model.addAttribute("estados", EstadoAsistencia.values());
        return "asistencia/llamado";
    }

    @PostMapping("/coordinacion/sesiones/{sesionId}/llamado")
    public String guardarLlamado(@PathVariable Long sesionId,
                                  @RequestParam List<Long> matriculaId,
                                  @RequestParam List<EstadoAsistencia> estado,
                                  @RequestParam List<String> observacion,
                                  RedirectAttributes redirectAttributes) {
        for (int i = 0; i < matriculaId.size(); i++) {
            asistenciaService.registrarAsistencia(sesionId, matriculaId.get(i), estado.get(i), observacion.get(i), null);
        }
        redirectAttributes.addFlashAttribute("mensaje", "Asistencia registrada.");
        return "redirect:/coordinacion/sesiones/" + sesionId + "/llamado";
    }
}
