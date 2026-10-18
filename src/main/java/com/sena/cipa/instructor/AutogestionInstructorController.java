package com.sena.cipa.instructor;

import java.util.ArrayList;
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

    public AutogestionInstructorController(InstructorRepository instructorRepository,
                                            AsignacionInstructorRepository asignacionRepository,
                                            MatriculaRepository matriculaRepository,
                                            SesionFormacionRepository sesionRepository,
                                            ActividadEvaluativaRepository actividadRepository) {
        this.instructorRepository = instructorRepository;
        this.asignacionRepository = asignacionRepository;
        this.matriculaRepository = matriculaRepository;
        this.sesionRepository = sesionRepository;
        this.actividadRepository = actividadRepository;
    }

    public record ResumenFicha(AsignacionInstructor asignacion, int aprendicesMatriculados,
                                List<SesionFormacion> sesiones, List<ActividadEvaluativa> actividades) {
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
            int matriculados = matriculaRepository.findByFichaId(fichaId).size();
            List<SesionFormacion> sesiones = sesionRepository.findByFichaId(fichaId);
            List<ActividadEvaluativa> actividades = actividadRepository.findByFichaId(fichaId);
            resumenes.add(new ResumenFicha(asignacion, matriculados, sesiones, actividades));
        }

        model.addAttribute("instructor", instructor);
        model.addAttribute("resumenes", resumenes);
        return "instructor/mis-fichas";
    }
}
