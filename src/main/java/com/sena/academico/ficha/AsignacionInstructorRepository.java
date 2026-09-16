package com.sena.academico.ficha;

import com.sena.academico.ficha.AsignacionInstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsignacionInstructorRepository extends JpaRepository<AsignacionInstructor, Long> {

    List<AsignacionInstructor> findByInstructor_Usuario_Id(Long usuarioId);

    boolean existsByFicha_IdAndInstructor_Usuario_Id(Long fichaId, Long usuarioId);
}
