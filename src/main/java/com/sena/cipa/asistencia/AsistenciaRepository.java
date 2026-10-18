package com.sena.cipa.asistencia;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    List<Asistencia> findBySesionId(Long sesionId);

    Optional<Asistencia> findBySesionIdAndMatriculaId(Long sesionId, Long matriculaId);
}
