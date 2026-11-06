package com.sena.cipa.ficha;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FichaRepository extends JpaRepository<Ficha, Long> {

    List<Ficha> findByEstado(EstadoFicha estado);

    long countByEstado(EstadoFicha estado);
}
