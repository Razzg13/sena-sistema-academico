package com.sena.cipa.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sena.cipa.modelo.AsignacionInstructor;

public interface AsignacionInstructorRepository extends JpaRepository<AsignacionInstructor, Long> {

    List<AsignacionInstructor> findByInstructorId(Long instructorId);

    List<AsignacionInstructor> findByFichaId(Long fichaId);
}
