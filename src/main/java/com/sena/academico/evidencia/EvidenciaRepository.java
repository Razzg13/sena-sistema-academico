package com.sena.academico.evidencia;

import com.sena.academico.evidencia.Evidencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvidenciaRepository extends JpaRepository<Evidencia, Long> {

    List<Evidencia> findByActividadId(Long actividadId);

    Optional<Evidencia> findByActividadIdAndMatriculaId(Long actividadId, Long matriculaId);

    List<Evidencia> findByMatriculaId(Long matriculaId);
}
