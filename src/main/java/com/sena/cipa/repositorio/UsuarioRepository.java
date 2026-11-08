package com.sena.cipa.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sena.cipa.modelo.EstadoGeneral;
import com.sena.cipa.modelo.RolNombre;
import com.sena.cipa.modelo.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    long countByEstado(EstadoGeneral estado);

    long countByRol(RolNombre rol);
}
