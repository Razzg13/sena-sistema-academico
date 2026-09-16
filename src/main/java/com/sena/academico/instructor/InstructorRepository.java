package com.sena.academico.instructor;

import com.sena.academico.common.EstadoRegistro;
import com.sena.academico.instructor.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    boolean existsByUsuarioId(Long usuarioId);

    List<Instructor> findByEstado(EstadoRegistro estado);
}
