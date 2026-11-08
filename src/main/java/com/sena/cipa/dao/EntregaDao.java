package com.sena.cipa.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sena.cipa.modelo.Entrega;

public interface EntregaDao extends JpaRepository<Entrega, Long> {

    Optional<Entrega> findByActividadIdAndMatriculaId(Long actividadId, Long matriculaId);

    List<Entrega> findByActividadId(Long actividadId);

    List<Entrega> findByMatriculaIdIn(Collection<Long> matriculaIds);

    List<Entrega> findByActividadFichaIdIn(Collection<Long> fichaIds);
}
