package com.sena.cipa.matricula;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    List<Matricula> findByAprendizId(Long aprendizId);

    List<Matricula> findByFichaId(Long fichaId);

    List<Matricula> findByFichaIdAndEstado(Long fichaId, EstadoMatricula estado);

    List<Matricula> findByFichaIdInAndEstado(Collection<Long> fichaIds, EstadoMatricula estado);

    long countByEstado(EstadoMatricula estado);
}
