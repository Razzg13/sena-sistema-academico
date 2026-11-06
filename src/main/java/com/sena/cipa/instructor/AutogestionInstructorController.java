package com.sena.cipa.instructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sena.cipa.asistencia.SesionFormacion;
import com.sena.cipa.asistencia.SesionFormacionRepository;
import com.sena.cipa.evaluacion.ActividadEvaluativa;
import com.sena.cipa.evaluacion.ActividadEvaluativaRepository;
import com.sena.cipa.common.Fechas;
import com.sena.cipa.entrega.EntregaRepository;
import com.sena.cipa.evaluacion.EvaluacionRepository;
import com.sena.cipa.matricula.EstadoMatricula;
import com.sena.cipa.matricula.MatriculaRepository;
import com.sena.cipa.seguridad.UsuarioPrincipal;

@Controller
@RequestMapping("/mis-fichas")
public class AutogestionInstructorController {

    private final InstructorRepository instructorRepository;
    private final AsignacionInstructorRepository asignacionRepository;
    private final MatriculaRepository matriculaRepository;
    private final SesionFormacionRepository sesionRepository;
    private final ActividadEvaluativaRepository actividadRepository;
    private final EntregaRepository entregaRepository;
    private final EvaluacionRepository evaluacionRepository;

    public AutogestionInstructorController(InstructorRepository instructorRepository,
                                            AsignacionInstructorRepository asignacionRepository,
                                            MatriculaRepository matriculaRepository,
                                            SesionFormacionRepository sesionRepository,
                                            ActividadEvaluativaRepository actividadRepository,
                                            EntregaRepository entregaRepository,
                                            EvaluacionRepository evaluacionRepository) {
        this.instructorRepository = instructorRepository;
        this.asignacionRepository = asignacionRepository;
        this.matriculaRepository = matriculaRepository;
        this.sesionRepository = sesionRepository;
        this.actividadRepository = actividadRepository;
        this.entregaRepository = entregaRepository;
        this.evaluacionRepository = evaluacionRepository;
    }

    public record ResumenFicha(AsignacionInstructor asignacion, int aprendicesMatriculados,
                                List<SesionFormacion> sesiones, List<FilaActividad> actividades) {
    }

    public record FilaActividad(ActividadEvaluativa actividad, int entregas, int calificadas, boolean vigente) {
    }

    @GetMapping
    public String misFichas(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        var instructorOpt = instructorRepository.findByUsuarioId(principal.getUsuario().getId());
        if (instructorOpt.isEmpty()) {
            model.addAttribute("sinPerfil", true);
            return "instructor/mis-fichas";
        }

        Instructor instructor = instructorOpt.get();
        List<AsignacionInstructor> asignaciones = asignacionRepository.findByInstructorId(instructor.getId());

        List<ResumenFicha> resumenes = new ArrayList<>();
        for (AsignacionInstructor asignacion : asignaciones) {
            Long fichaId = asignacion.getFicha().getId();
            int matriculados = matriculaRepository.findByFichaIdAndEstado(fichaId, EstadoMatricula.ACTIVA).size();
            List<SesionFormacion> sesiones = sesionRepository.findByFichaId(fichaId).stream()
                    .sorted(Comparator.comparing(SesionFormacion::getFecha).reversed())
                    .toList();
            List<FilaActividad> actividades = actividadRepository.findByFichaId(fichaId).stream()
                    .sorted(Comparator.comparing(ActividadEvaluativa::getFechaLimite).reversed())
                    .map(a -> new FilaActividad(a, entregaRepository.findByActividadId(a.getId()).size(),
                            (int) evaluacionRepository.findByActividadId(a.getId()).stream()
                                    .filter(e -> e.getCalificacion() != null).count(),
                            !a.getFechaLimite().isBefore(Fechas.hoy())))
                    .toList();
            resumenes.add(new ResumenFicha(asignacion, matriculados, sesiones, actividades));
        }

        model.addAttribute("instructor", instructor);
        model.addAttribute("resumenes", resumenes);
        return "instructor/mis-fichas";
    }
}
