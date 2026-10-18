package com.sena.cipa.evaluacion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ActividadEvaluativaRepository extends JpaRepository<ActividadEvaluativa, Long> {

    List<ActividadEvaluativa> findByFichaId(Long fichaId);
}
