package com.sena.academico.asistencia;

import com.sena.academico.asistencia.SesionFormacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SesionFormacionRepository extends JpaRepository<SesionFormacion, Long> {

    List<SesionFormacion> findByFichaIdOrderByFechaDescHoraInicioDesc(Long fichaId);
}
