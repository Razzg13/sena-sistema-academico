package com.sena.cipa.asistencia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SesionFormacionRepository extends JpaRepository<SesionFormacion, Long> {

    List<SesionFormacion> findByFichaId(Long fichaId);
}
