package com.sena.academico.evaluacion;

import com.sena.academico.evaluacion.ActividadEvaluativa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActividadEvaluativaRepository extends JpaRepository<ActividadEvaluativa, Long> {

    List<ActividadEvaluativa> findByFichaIdOrderByFechaLimiteAsc(Long fichaId);
}
