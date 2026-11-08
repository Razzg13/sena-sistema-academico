package com.sena.cipa.repositorio;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sena.cipa.modelo.EstadoMatricula;
import com.sena.cipa.modelo.Matricula;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    List<Matricula> findByAprendizId(Long aprendizId);

    List<Matricula> findByFichaId(Long fichaId);

    List<Matricula> findByFichaIdAndEstado(Long fichaId, EstadoMatricula estado);

    List<Matricula> findByFichaIdInAndEstado(Collection<Long> fichaIds, EstadoMatricula estado);

    long countByEstado(EstadoMatricula estado);
}
