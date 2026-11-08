package com.sena.cipa.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sena.cipa.modelo.EstadoGeneral;
import com.sena.cipa.modelo.Programa;

public interface ProgramaRepository extends JpaRepository<Programa, Long> {

    long countByEstado(EstadoGeneral estado);
}
