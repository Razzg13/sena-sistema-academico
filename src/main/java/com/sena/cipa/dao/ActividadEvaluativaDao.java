package com.sena.cipa.dao;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sena.cipa.modelo.ActividadEvaluativa;

public interface ActividadEvaluativaDao extends JpaRepository<ActividadEvaluativa, Long> {

    List<ActividadEvaluativa> findByFichaId(Long fichaId);

    List<ActividadEvaluativa> findByFichaIdIn(Collection<Long> fichaIds);

    List<ActividadEvaluativa> findByFechaLimiteBetweenOrderByFechaLimiteAsc(LocalDate desde, LocalDate hasta);
}
