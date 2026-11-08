package com.sena.cipa.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sena.cipa.modelo.Usuario;
import com.sena.cipa.modelo.enums.EstadoGeneral;
import com.sena.cipa.modelo.enums.RolNombre;

public interface UsuarioDao extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    boolean existsByCorreo(String correo);

    long countByEstado(EstadoGeneral estado);

    long countByRol(RolNombre rol);
}
