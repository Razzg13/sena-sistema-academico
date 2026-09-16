package com.sena.academico.aprendiz;

import com.sena.academico.aprendiz.Aprendiz;
import com.sena.academico.common.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AprendizRepository extends JpaRepository<Aprendiz, Long> {

    boolean existsByUsuarioId(Long usuarioId);

    List<Aprendiz> findByEstado(EstadoRegistro estado);
}
