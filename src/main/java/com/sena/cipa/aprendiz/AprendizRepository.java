package com.sena.cipa.aprendiz;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AprendizRepository extends JpaRepository<Aprendiz, Long> {

    boolean existsByUsuarioId(Long usuarioId);

    Optional<Aprendiz> findByUsuarioId(Long usuarioId);

    long countByEstadoFormativo(EstadoFormativo estadoFormativo);
}
