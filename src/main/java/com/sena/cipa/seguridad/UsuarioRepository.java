package com.sena.cipa.seguridad;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sena.cipa.common.EstadoGeneral;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    long countByEstado(EstadoGeneral estado);

    long countByRol(RolNombre rol);
}
