package com.sena.academico.evaluacion;

import com.sena.academico.auditoria.Accion;
import com.sena.academico.auditoria.AuditoriaService;
import com.sena.academico.common.NegocioException;
import com.sena.academico.evaluacion.ActividadEvaluativa;
import com.sena.academico.evaluacion.Evaluacion;
import com.sena.academico.evaluacion.JuicioEvaluacion;
import com.sena.academico.evaluacion.ActividadEvaluativaRepository;
import com.sena.academico.evaluacion.EvaluacionRepository;
import com.sena.academico.ficha.Ficha;
import com.sena.academico.ficha.FichaService;
import com.sena.academico.matricula.Matricula;
import com.sena.academico.matricula.MatriculaService;
import com.sena.academico.programa.ResultadoAprendizaje;
import com.sena.academico.programa.ResultadoAprendizajeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class EvaluacionService {

    private final ActividadEvaluativaRepository actividadRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final FichaService fichaService;
    private final MatriculaService matriculaService;
    private final AuditoriaService auditoriaService;
    private final ResultadoAprendizajeRepository resultadoAprendizajeRepository;

    public EvaluacionService(ActividadEvaluativaRepository actividadRepository, EvaluacionRepository evaluacionRepository,
                              FichaService fichaService, MatriculaService matriculaService,
                              AuditoriaService auditoriaService, ResultadoAprendizajeRepository resultadoAprendizajeRepository) {
        this.actividadRepository = actividadRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.fichaService = fichaService;
        this.matriculaService = matriculaService;
        this.auditoriaService = auditoriaService;
        this.resultadoAprendizajeRepository = resultadoAprendizajeRepository;
    }

    @Transactional
    public ActividadEvaluativa crearActividad(Long fichaId, Long resultadoAprendizajeId, String nombre,
                                               String descripcion, LocalDate fechaLimite) {
        Ficha ficha = fichaService.obtener(fichaId);
        ResultadoAprendizaje resultado = resultadoAprendizajeRepository.findById(resultadoAprendizajeId)
                .orElseThrow(() -> new NegocioException("El resultado de aprendizaje solicitado no existe."));

        ActividadEvaluativa actividad = new ActividadEvaluativa(ficha, resultado, nombre, descripcion, fechaLimite);
        actividad = actividadRepository.save(actividad);
        auditoriaService.registrar(Accion.CREACION, "ActividadEvaluativa", actividad.getId(),
                "Actividad creada: " + nombre + " (RA " + resultado.getCodigo() + ") en ficha " + ficha.getNumero());
        return actividad;
    }

    public List<ActividadEvaluativa> listarActividadesPorFicha(Long fichaId) {
        return actividadRepository.findByFichaIdOrderByFechaLimiteAsc(fichaId);
    }

    public ActividadEvaluativa obtenerActividad(Long id) {
        return actividadRepository.findById(id)
                .orElseThrow(() -> new NegocioException("La actividad evaluativa solicitada no existe."));
    }

    public List<Evaluacion> listarEvaluacionesPorActividad(Long actividadId) {
        return evaluacionRepository.findByActividadId(actividadId);
    }

    public List<Evaluacion> listarPorMatricula(Long matriculaId) {
        return evaluacionRepository.findByMatriculaId(matriculaId);
    }

    @Transactional
    public void registrarEvaluacion(Long actividadId, Long matriculaId, BigDecimal calificacion,
                                     JuicioEvaluacion juicio, String retroalimentacion) {
        ActividadEvaluativa actividad = obtenerActividad(actividadId);
        Matricula matricula = matriculaService.obtener(matriculaId);
        LocalDate hoy = LocalDate.now();

        Evaluacion evaluacion = evaluacionRepository.findByActividadIdAndMatriculaId(actividadId, matriculaId)
                .map(existente -> {
                    existente.actualizar(calificacion, juicio, retroalimentacion, hoy);
                    return existente;
                })
                .orElseGet(() -> new Evaluacion(actividad, matricula, calificacion, juicio, retroalimentacion, hoy));

        evaluacionRepository.save(evaluacion);
        auditoriaService.registrar(Accion.EVALUACION, "Evaluacion", evaluacion.getId(),
                "Evaluación registrada: " + matricula.getAprendiz().getUsuario().getNombreCompleto()
                        + " - " + juicio + " (actividad " + actividad.getNombre() + ")");
    }
}
