package com.sena.cipa.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sena.cipa.modelo.Aprendiz;
import com.sena.cipa.modelo.EstadoFormativo;

public interface AprendizRepository extends JpaRepository<Aprendiz, Long> {

    boolean existsByUsuarioId(Long usuarioId);

    Optional<Aprendiz> findByUsuarioId(Long usuarioId);

    long countByEstadoFormativo(EstadoFormativo estadoFormativo);
}
