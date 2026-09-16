package com.sena.academico.programa;

import com.sena.academico.programa.Programa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProgramaRepository extends JpaRepository<Programa, Long>, JpaSpecificationExecutor<Programa> {

    boolean existsByCodigo(String codigo);
}
