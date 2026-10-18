package com.sena.cipa.evaluacion;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {

    Optional<Evaluacion> findByActividadIdAndMatriculaId(Long actividadId, Long matriculaId);

    List<Evaluacion> findByActividadId(Long actividadId);

    List<Evaluacion> findByMatriculaIdIn(Collection<Long> matriculaIds);

    List<Evaluacion> findByActividadFichaIdIn(Collection<Long> fichaIds);
}
