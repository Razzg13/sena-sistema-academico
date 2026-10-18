package com.sena.cipa.matricula;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    List<Matricula> findByAprendizId(Long aprendizId);

    List<Matricula> findByFichaId(Long fichaId);
}
