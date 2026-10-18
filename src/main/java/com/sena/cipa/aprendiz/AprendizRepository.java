package com.sena.cipa.aprendiz;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AprendizRepository extends JpaRepository<Aprendiz, Long> {

    boolean existsByUsuarioId(Long usuarioId);
}
