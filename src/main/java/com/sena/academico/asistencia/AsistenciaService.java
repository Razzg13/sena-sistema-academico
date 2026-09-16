package com.sena.academico.asistencia;

import com.sena.academico.asistencia.Asistencia;
import com.sena.academico.asistencia.EstadoAsistencia;
import com.sena.academico.asistencia.SesionFormacion;
import com.sena.academico.asistencia.AsistenciaRepository;
import com.sena.academico.asistencia.SesionFormacionRepository;
import com.sena.academico.auditoria.Accion;
import com.sena.academico.auditoria.AuditoriaService;
import com.sena.academico.common.NegocioException;
import com.sena.academico.ficha.Ficha;
import com.sena.academico.ficha.FichaService;
import com.sena.academico.matricula.Matricula;
import com.sena.academico.matricula.MatriculaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AsistenciaService {

    private final SesionFormacionRepository sesionRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final FichaService fichaService;
    private final MatriculaService matriculaService;
    private final AuditoriaService auditoriaService;

    public AsistenciaService(SesionFormacionRepository sesionRepository, AsistenciaRepository asistenciaRepository,
                              FichaService fichaService, MatriculaService matriculaService,
                              AuditoriaService auditoriaService) {
        this.sesionRepository = sesionRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.fichaService = fichaService;
        this.matriculaService = matriculaService;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public SesionFormacion programarSesion(Long fichaId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                                            String tema) {
        if (!horaFin.isAfter(horaInicio)) {
            throw new NegocioException("La hora de fin debe ser posterior a la hora de inicio.");
        }
        Ficha ficha = fichaService.obtener(fichaId);
        SesionFormacion sesion = new SesionFormacion(ficha, fecha, horaInicio, horaFin, tema);
        sesion = sesionRepository.save(sesion);
        auditoriaService.registrar(Accion.CREACION, "SesionFormacion", sesion.getId(),
                "Sesión programada para ficha " + ficha.getNumero() + " el " + fecha + " (" + tema + ")");
        return sesion;
    }

    public List<SesionFormacion> listarSesionesPorFicha(Long fichaId) {
        return sesionRepository.findByFichaIdOrderByFechaDescHoraInicioDesc(fichaId);
    }

    public SesionFormacion obtenerSesion(Long id) {
        return sesionRepository.findById(id)
                .orElseThrow(() -> new NegocioException("La sesión solicitada no existe."));
    }

    public List<Asistencia> listarAsistenciasPorSesion(Long sesionId) {
        return asistenciaRepository.findBySesionId(sesionId);
    }

    public List<Asistencia> listarPorMatricula(Long matriculaId) {
        return asistenciaRepository.findByMatriculaId(matriculaId);
    }

    @Transactional
    public void registrarAsistencia(Long sesionId, Long matriculaId, EstadoAsistencia estado,
                                     String observacion, String justificacion) {
        SesionFormacion sesion = obtenerSesion(sesionId);
        Matricula matricula = matriculaService.obtener(matriculaId);

        Asistencia asistencia = asistenciaRepository.findBySesionIdAndMatriculaId(sesionId, matriculaId)
                .map(existente -> {
                    existente.actualizar(estado, observacion, justificacion);
                    return existente;
                })
                .orElseGet(() -> new Asistencia(sesion, matricula, estado, observacion, justificacion));

        asistenciaRepository.save(asistencia);
        auditoriaService.registrar(Accion.MODIFICACION, "Asistencia", asistencia.getId(),
                "Asistencia registrada: " + matricula.getAprendiz().getUsuario().getNombreCompleto()
                        + " - " + estado + " (sesión " + sesion.getFecha() + ")");
    }
}
