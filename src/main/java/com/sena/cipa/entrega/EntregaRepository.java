package com.sena.cipa.entrega;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EntregaRepository extends JpaRepository<Entrega, Long> {

    Optional<Entrega> findByActividadIdAndMatriculaId(Long actividadId, Long matriculaId);

    List<Entrega> findByActividadId(Long actividadId);

    List<Entrega> findByMatriculaIdIn(Collection<Long> matriculaIds);

    List<Entrega> findByActividadFichaIdIn(Collection<Long> fichaIds);
}
