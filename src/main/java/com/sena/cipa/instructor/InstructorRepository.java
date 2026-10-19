package com.sena.cipa.instructor;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sena.cipa.common.EstadoGeneral;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    boolean existsByUsuarioId(Long usuarioId);

    Optional<Instructor> findByUsuarioId(Long usuarioId);

    long countByEstado(EstadoGeneral estado);
}
