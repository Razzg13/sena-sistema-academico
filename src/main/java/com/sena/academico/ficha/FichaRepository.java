package com.sena.academico.ficha;

import com.sena.academico.ficha.Ficha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FichaRepository extends JpaRepository<Ficha, Long>, JpaSpecificationExecutor<Ficha> {

    boolean existsByNumero(String numero);
}
