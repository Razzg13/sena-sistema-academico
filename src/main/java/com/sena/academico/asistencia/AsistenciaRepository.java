package com.sena.academico.asistencia;

import com.sena.academico.asistencia.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    Optional<Asistencia> findBySesionIdAndMatriculaId(Long sesionId, Long matriculaId);

    List<Asistencia> findBySesionId(Long sesionId);

    List<Asistencia> findByMatriculaId(Long matriculaId);
}
