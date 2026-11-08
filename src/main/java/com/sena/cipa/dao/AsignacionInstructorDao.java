package com.sena.cipa.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sena.cipa.modelo.AsignacionInstructor;

public interface AsignacionInstructorDao extends JpaRepository<AsignacionInstructor, Long> {

    List<AsignacionInstructor> findByInstructorId(Long instructorId);

    List<AsignacionInstructor> findByFichaId(Long fichaId);
}
