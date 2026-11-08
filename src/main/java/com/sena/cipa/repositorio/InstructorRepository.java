package com.sena.cipa.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sena.cipa.modelo.EstadoGeneral;
import com.sena.cipa.modelo.Instructor;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    boolean existsByUsuarioId(Long usuarioId);

    Optional<Instructor> findByUsuarioId(Long usuarioId);

    long countByEstado(EstadoGeneral estado);
}
