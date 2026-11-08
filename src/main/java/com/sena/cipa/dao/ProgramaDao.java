package com.sena.cipa.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sena.cipa.modelo.Programa;
import com.sena.cipa.modelo.enums.EstadoGeneral;

public interface ProgramaDao extends JpaRepository<Programa, Long> {

    long countByEstado(EstadoGeneral estado);
}
