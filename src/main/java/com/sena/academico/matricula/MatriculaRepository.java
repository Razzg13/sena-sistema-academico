package com.sena.academico.matricula;

import com.sena.academico.matricula.EstadoMatricula;
import com.sena.academico.matricula.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    boolean existsByAprendizIdAndFichaIdAndEstado(Long aprendizId, Long fichaId, EstadoMatricula estado);

    List<Matricula> findByFichaIdOrderByFechaMatriculaDesc(Long fichaId);

    List<Matricula> findAllByOrderByFechaMatriculaDesc();

    List<Matricula> findByAprendiz_Usuario_IdOrderByFechaMatriculaDesc(Long usuarioId);

    Optional<Matricula> findByFicha_IdAndAprendiz_Usuario_Id(Long fichaId, Long usuarioId);
}
