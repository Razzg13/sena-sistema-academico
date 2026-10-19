package com.sena.cipa.aprendiz;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sena.cipa.asistencia.Asistencia;
import com.sena.cipa.asistencia.AsistenciaRepository;
import com.sena.cipa.asistencia.SesionFormacion;
import com.sena.cipa.asistencia.SesionFormacionRepository;
import com.sena.cipa.evaluacion.ActividadEvaluativa;
import com.sena.cipa.evaluacion.ActividadEvaluativaRepository;
import com.sena.cipa.evaluacion.Evaluacion;
import com.sena.cipa.evaluacion.EvaluacionRepository;
import com.sena.cipa.matricula.Matricula;
import com.sena.cipa.matricula.MatriculaRepository;
import com.sena.cipa.seguridad.UsuarioPrincipal;

@Controller
@RequestMapping("/mis-datos")
public class AutogestionAprendizController {

    private final AprendizRepository aprendizRepository;
    private final MatriculaRepository matriculaRepository;
    private final SesionFormacionRepository sesionRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final ActividadEvaluativaRepository actividadRepository;
    private final EvaluacionRepository evaluacionRepository;

    public AutogestionAprendizController(AprendizRepository aprendizRepository, MatriculaRepository matriculaRepository,
                                          SesionFormacionRepository sesionRepository, AsistenciaRepository asistenciaRepository,
                                          ActividadEvaluativaRepository actividadRepository, EvaluacionRepository evaluacionRepository) {
        this.aprendizRepository = aprendizRepository;
        this.matriculaRepository = matriculaRepository;
        this.sesionRepository = sesionRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.actividadRepository = actividadRepository;
        this.evaluacionRepository = evaluacionRepository;
    }

    public record ItemAsistencia(SesionFormacion sesion, Asistencia asistencia) {
    }

    public record ItemEvaluacion(ActividadEvaluativa actividad, Evaluacion evaluacion) {
    }

    public record ResumenMatricula(Matricula matricula, List<ItemAsistencia> asistencias, List<ItemEvaluacion> evaluaciones) {
    }

    @GetMapping
    public String misDatos(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        var aprendizOpt = aprendizRepository.findByUsuarioId(principal.getUsuario().getId());
        if (aprendizOpt.isEmpty()) {
            model.addAttribute("sinPerfil", true);
            return "aprendiz/mis-datos";
        }

        Aprendiz aprendiz = aprendizOpt.get();
        List<Matricula> matriculas = matriculaRepository.findByAprendizId(aprendiz.getId());

        List<ResumenMatricula> resumenes = new ArrayList<>();
        for (Matricula matricula : matriculas) {
            Long fichaId = matricula.getFicha().getId();

            List<ItemAsistencia> asistencias = sesionRepository.findByFichaId(fichaId).stream()
                    .map(sesion -> new ItemAsistencia(sesion,
                            asistenciaRepository.findBySesionIdAndMatriculaId(sesion.getId(), matricula.getId()).orElse(null)))
                    .toList();

            List<ItemEvaluacion> evaluaciones = actividadRepository.findByFichaId(fichaId).stream()
                    .map(actividad -> new ItemEvaluacion(actividad,
                            evaluacionRepository.findByActividadIdAndMatriculaId(actividad.getId(), matricula.getId()).orElse(null)))
                    .toList();

            resumenes.add(new ResumenMatricula(matricula, asistencias, evaluaciones));
        }

        model.addAttribute("aprendiz", aprendiz);
        model.addAttribute("resumenes", resumenes);
        return "aprendiz/mis-datos";
    }
}
