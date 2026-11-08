package com.sena.cipa.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sena.cipa.modelo.Ficha;
import com.sena.cipa.modelo.enums.EstadoFicha;

public interface FichaDao extends JpaRepository<Ficha, Long> {

    List<Ficha> findByEstado(EstadoFicha estado);

    long countByEstado(EstadoFicha estado);
}
