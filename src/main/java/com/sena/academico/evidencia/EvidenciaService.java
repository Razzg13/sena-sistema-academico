package com.sena.academico.evidencia;

import com.sena.academico.archivo.Archivo;
import com.sena.academico.archivo.ArchivoAlmacenamientoService;
import com.sena.academico.auditoria.Accion;
import com.sena.academico.auditoria.AuditoriaService;
import com.sena.academico.common.NegocioException;
import com.sena.academico.evaluacion.ActividadEvaluativa;
import com.sena.academico.evaluacion.EvaluacionService;
import com.sena.academico.evidencia.EstadoEvidencia;
import com.sena.academico.evidencia.Evidencia;
import com.sena.academico.evidencia.EvidenciaRepository;
import com.sena.academico.matricula.Matricula;
import com.sena.academico.matricula.MatriculaService;
import com.sena.academico.seguridad.RolNombre;
import com.sena.academico.seguridad.UsuarioPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
public class EvidenciaService {

    private final EvidenciaRepository evidenciaRepository;
    private final ArchivoAlmacenamientoService archivoAlmacenamientoService;
    private final EvaluacionService evaluacionService;
    private final MatriculaService matriculaService;
    private final AuditoriaService auditoriaService;

    public EvidenciaService(EvidenciaRepository evidenciaRepository,
                             ArchivoAlmacenamientoService archivoAlmacenamientoService,
                             EvaluacionService evaluacionService, MatriculaService matriculaService,
                             AuditoriaService auditoriaService) {
        this.evidenciaRepository = evidenciaRepository;
        this.archivoAlmacenamientoService = archivoAlmacenamientoService;
        this.evaluacionService = evaluacionService;
        this.matriculaService = matriculaService;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public Evidencia entregar(Long actividadId, Long matriculaId, MultipartFile archivoSubido) {
        ActividadEvaluativa actividad = evaluacionService.obtenerActividad(actividadId);
        Matricula matricula = matriculaService.obtener(matriculaId);

        if (evidenciaRepository.findByActividadIdAndMatriculaId(actividadId, matriculaId).isPresent()) {
            throw new NegocioException("Ya existe una evidencia entregada para este aprendiz en esta actividad.");
        }

        Archivo archivo = archivoAlmacenamientoService.guardar(archivoSubido);
        Evidencia evidencia = new Evidencia(actividad, matricula, archivo, LocalDate.now());
        evidencia = evidenciaRepository.save(evidencia);
        auditoriaService.registrar(Accion.CREACION, "Evidencia", evidencia.getId(),
                "Evidencia recibida de " + matricula.getAprendiz().getUsuario().getNombreCompleto()
                        + " para " + actividad.getNombre() + " (" + archivo.getNombreOriginal() + ")");
        return evidencia;
    }

    public List<Evidencia> listarPorActividad(Long actividadId) {
        return evidenciaRepository.findByActividadId(actividadId);
    }

    public List<Evidencia> listarPorMatricula(Long matriculaId) {
        return evidenciaRepository.findByMatriculaId(matriculaId);
    }

    public Evidencia obtener(Long id) {
        return evidenciaRepository.findById(id)
                .orElseThrow(() -> new NegocioException("La evidencia solicitada no existe."));
    }

    @Transactional
    public void cambiarEstado(Long evidenciaId, EstadoEvidencia nuevoEstado, String observacion) {
        Evidencia evidencia = obtener(evidenciaId);
        evidencia.cambiarEstado(nuevoEstado, observacion);
        auditoriaService.registrar(Accion.MODIFICACION, "Evidencia", evidencia.getId(),
                "Evidencia " + nuevoEstado + ": " + evidencia.getMatricula().getAprendiz().getUsuario().getNombreCompleto());
    }

    /**
     * RF-13: solo puede descargar el archivo el personal administrativo/coordinacion,
     * el instructor asignado a la ficha de la actividad, o el propio aprendiz dueno de la evidencia.
     */
    public boolean puedeDescargar(Evidencia evidencia, UsuarioPrincipal principal) {
        if (principal.getRoles().contains(RolNombre.ADMINISTRADOR)
                || principal.getRoles().contains(RolNombre.COORDINACION_ACADEMICA)
                || principal.getRoles().contains(RolNombre.CONSULTA_AUDITOR)) {
            return true;
        }
        Long usuarioId = principal.getUsuario().getId();
        if (principal.getRoles().contains(RolNombre.APRENDIZ)) {
            return evidencia.getMatricula().getAprendiz().getUsuario().getId().equals(usuarioId);
        }
        if (principal.getRoles().contains(RolNombre.INSTRUCTOR)) {
            return evidencia.getActividad().getFicha().getAsignaciones().stream()
                    .anyMatch(a -> a.getInstructor().getUsuario().getId().equals(usuarioId));
        }
        return false;
    }
}
