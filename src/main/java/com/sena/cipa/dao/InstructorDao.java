package com.sena.cipa.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sena.cipa.modelo.Instructor;
import com.sena.cipa.modelo.enums.EstadoGeneral;

public interface InstructorDao extends JpaRepository<Instructor, Long> {

    boolean existsByUsuarioId(Long usuarioId);

    Optional<Instructor> findByUsuarioId(Long usuarioId);

    long countByEstado(EstadoGeneral estado);
}
