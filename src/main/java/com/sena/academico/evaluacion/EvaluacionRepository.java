package com.sena.academico.evaluacion;

import com.sena.academico.evaluacion.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {

    Optional<Evaluacion> findByActividadIdAndMatriculaId(Long actividadId, Long matriculaId);

    List<Evaluacion> findByActividadId(Long actividadId);

    List<Evaluacion> findByMatriculaId(Long matriculaId);
}
