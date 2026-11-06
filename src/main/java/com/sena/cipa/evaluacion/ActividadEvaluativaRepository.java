package com.sena.cipa.evaluacion;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ActividadEvaluativaRepository extends JpaRepository<ActividadEvaluativa, Long> {

    List<ActividadEvaluativa> findByFichaId(Long fichaId);

    List<ActividadEvaluativa> findByFichaIdIn(Collection<Long> fichaIds);

    List<ActividadEvaluativa> findByFechaLimiteBetweenOrderByFechaLimiteAsc(LocalDate desde, LocalDate hasta);
}
