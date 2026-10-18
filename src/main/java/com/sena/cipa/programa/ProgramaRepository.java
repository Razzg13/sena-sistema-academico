package com.sena.cipa.programa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sena.cipa.common.EstadoGeneral;

public interface ProgramaRepository extends JpaRepository<Programa, Long> {

    long countByEstado(EstadoGeneral estado);
}
