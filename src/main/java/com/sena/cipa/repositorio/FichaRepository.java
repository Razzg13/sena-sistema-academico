package com.sena.cipa.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sena.cipa.modelo.EstadoFicha;
import com.sena.cipa.modelo.Ficha;

public interface FichaRepository extends JpaRepository<Ficha, Long> {

    List<Ficha> findByEstado(EstadoFicha estado);

    long countByEstado(EstadoFicha estado);
}
