package com.sena.cipa.controlador;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sena.cipa.dao.ActividadEvaluativaDao;
import com.sena.cipa.dao.AsignacionInstructorDao;
import com.sena.cipa.dao.EntregaDao;
import com.sena.cipa.dao.EvaluacionDao;
import com.sena.cipa.dao.InstructorDao;
import com.sena.cipa.dao.MatriculaDao;
import com.sena.cipa.dao.SesionFormacionDao;
import com.sena.cipa.modelo.ActividadEvaluativa;
import com.sena.cipa.modelo.AsignacionInstructor;
import com.sena.cipa.modelo.Instructor;
import com.sena.cipa.modelo.SesionFormacion;
import com.sena.cipa.modelo.enums.EstadoMatricula;
import com.sena.cipa.seguridad.UsuarioPrincipal;
import com.sena.cipa.util.Fechas;

@Controller
@RequestMapping("/mis-fichas")
public class AutogestionInstructorController {

    private final InstructorDao instructorDao;
    private final AsignacionInstructorDao asignacionDao;
    private final MatriculaDao matriculaDao;
    private final SesionFormacionDao sesionDao;
    private final ActividadEvaluativaDao actividadDao;
    private final EntregaDao entregaDao;
    private final EvaluacionDao evaluacionDao;

    public AutogestionInstructorController(InstructorDao instructorDao,
                                            AsignacionInstructorDao asignacionDao,
                                            MatriculaDao matriculaDao,
                                            SesionFormacionDao sesionDao,
                                            ActividadEvaluativaDao actividadDao,
                                            EntregaDao entregaDao,
                                            EvaluacionDao evaluacionDao) {
        this.instructorDao = instructorDao;
        this.asignacionDao = asignacionDao;
        this.matriculaDao = matriculaDao;
        this.sesionDao = sesionDao;
        this.actividadDao = actividadDao;
        this.entregaDao = entregaDao;
        this.evaluacionDao = evaluacionDao;
    }

    public record ResumenFicha(AsignacionInstructor asignacion, int aprendicesMatriculados,
                                List<SesionFormacion> sesiones, List<FilaActividad> actividades) {
    }

    public record FilaActividad(ActividadEvaluativa actividad, int entregas, int calificadas, boolean vigente) {
    }

    @GetMapping
    public String misFichas(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        var instructorOpt = instructorDao.findByUsuarioId(principal.getUsuario().getId());
        if (instructorOpt.isEmpty()) {
            model.addAttribute("sinPerfil", true);
            return "instructor/mis-fichas";
        }

        Instructor instructor = instructorOpt.get();
        List<AsignacionInstructor> asignaciones = asignacionDao.findByInstructorId(instructor.getId());

        List<ResumenFicha> resumenes = new ArrayList<>();
        for (AsignacionInstructor asignacion : asignaciones) {
            Long fichaId = asignacion.getFicha().getId();
            int matriculados = matriculaDao.findByFichaIdAndEstado(fichaId, EstadoMatricula.ACTIVA).size();
            List<SesionFormacion> sesiones = sesionDao.findByFichaId(fichaId).stream()
                    .sorted(Comparator.comparing(SesionFormacion::getFecha).reversed())
                    .toList();
            List<FilaActividad> actividades = actividadDao.findByFichaId(fichaId).stream()
                    .sorted(Comparator.comparing(ActividadEvaluativa::getFechaLimite).reversed())
                    .map(a -> new FilaActividad(a, entregaDao.findByActividadId(a.getId()).size(),
                            (int) evaluacionDao.findByActividadId(a.getId()).stream()
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
