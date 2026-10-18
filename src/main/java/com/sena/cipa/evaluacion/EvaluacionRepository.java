package com.sena.cipa.evaluacion;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {

    Optional<Evaluacion> findByActividadIdAndMatriculaId(Long actividadId, Long matriculaId);
}
