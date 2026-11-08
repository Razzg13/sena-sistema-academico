package com.sena.cipa.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sena.cipa.modelo.Asistencia;

public interface AsistenciaDao extends JpaRepository<Asistencia, Long> {

    List<Asistencia> findBySesionId(Long sesionId);

    Optional<Asistencia> findBySesionIdAndMatriculaId(Long sesionId, Long matriculaId);

    List<Asistencia> findByMatriculaIdIn(Collection<Long> matriculaIds);

    List<Asistencia> findBySesionFichaIdIn(Collection<Long> fichaIds);
}
