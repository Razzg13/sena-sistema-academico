package com.sena.cipa.controlador;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sena.cipa.dao.ActividadEvaluativaDao;
import com.sena.cipa.dao.AprendizDao;
import com.sena.cipa.dao.AsistenciaDao;
import com.sena.cipa.dao.EvaluacionDao;
import com.sena.cipa.dao.MatriculaDao;
import com.sena.cipa.dao.SesionFormacionDao;
import com.sena.cipa.modelo.ActividadEvaluativa;
import com.sena.cipa.modelo.Aprendiz;
import com.sena.cipa.modelo.Asistencia;
import com.sena.cipa.modelo.Evaluacion;
import com.sena.cipa.modelo.Matricula;
import com.sena.cipa.modelo.SesionFormacion;
import com.sena.cipa.seguridad.UsuarioPrincipal;

@Controller
@RequestMapping("/mis-datos")
public class AutogestionAprendizController {

    private final AprendizDao aprendizDao;
    private final MatriculaDao matriculaDao;
    private final SesionFormacionDao sesionDao;
    private final AsistenciaDao asistenciaDao;
    private final ActividadEvaluativaDao actividadDao;
    private final EvaluacionDao evaluacionDao;

    public AutogestionAprendizController(AprendizDao aprendizDao, MatriculaDao matriculaDao,
                                          SesionFormacionDao sesionDao, AsistenciaDao asistenciaDao,
                                          ActividadEvaluativaDao actividadDao, EvaluacionDao evaluacionDao) {
        this.aprendizDao = aprendizDao;
        this.matriculaDao = matriculaDao;
        this.sesionDao = sesionDao;
        this.asistenciaDao = asistenciaDao;
        this.actividadDao = actividadDao;
        this.evaluacionDao = evaluacionDao;
    }

    public record ItemAsistencia(SesionFormacion sesion, Asistencia asistencia) {
    }

    public record ItemEvaluacion(ActividadEvaluativa actividad, Evaluacion evaluacion) {
    }

    public record ResumenMatricula(Matricula matricula, List<ItemAsistencia> asistencias, List<ItemEvaluacion> evaluaciones) {
    }

    @GetMapping
    public String misDatos(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        var aprendizOpt = aprendizDao.findByUsuarioId(principal.getUsuario().getId());
        if (aprendizOpt.isEmpty()) {
            model.addAttribute("sinPerfil", true);
            return "aprendiz/mis-datos";
        }

        Aprendiz aprendiz = aprendizOpt.get();
        List<Matricula> matriculas = matriculaDao.findByAprendizId(aprendiz.getId());

        List<ResumenMatricula> resumenes = new ArrayList<>();
        for (Matricula matricula : matriculas) {
            Long fichaId = matricula.getFicha().getId();

            List<ItemAsistencia> asistencias = sesionDao.findByFichaId(fichaId).stream()
                    .map(sesion -> new ItemAsistencia(sesion,
                            asistenciaDao.findBySesionIdAndMatriculaId(sesion.getId(), matricula.getId()).orElse(null)))
                    .toList();

            List<ItemEvaluacion> evaluaciones = actividadDao.findByFichaId(fichaId).stream()
                    .map(actividad -> new ItemEvaluacion(actividad,
                            evaluacionDao.findByActividadIdAndMatriculaId(actividad.getId(), matricula.getId()).orElse(null)))
                    .toList();

            resumenes.add(new ResumenMatricula(matricula, asistencias, evaluaciones));
        }

        model.addAttribute("aprendiz", aprendiz);
        model.addAttribute("resumenes", resumenes);
        return "aprendiz/mis-datos";
    }
}
