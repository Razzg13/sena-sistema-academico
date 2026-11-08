package com.sena.cipa.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sena.cipa.modelo.Matricula;
import com.sena.cipa.modelo.enums.EstadoMatricula;

public interface MatriculaDao extends JpaRepository<Matricula, Long> {

    List<Matricula> findByAprendizId(Long aprendizId);

    List<Matricula> findByFichaId(Long fichaId);

    List<Matricula> findByFichaIdAndEstado(Long fichaId, EstadoMatricula estado);

    List<Matricula> findByFichaIdInAndEstado(Collection<Long> fichaIds, EstadoMatricula estado);

    long countByEstado(EstadoMatricula estado);
}
