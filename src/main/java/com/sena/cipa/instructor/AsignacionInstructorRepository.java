package com.sena.cipa.instructor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AsignacionInstructorRepository extends JpaRepository<AsignacionInstructor, Long> {

    List<AsignacionInstructor> findByInstructorId(Long instructorId);
}
