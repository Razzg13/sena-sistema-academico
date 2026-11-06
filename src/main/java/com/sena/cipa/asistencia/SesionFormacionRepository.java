package com.sena.cipa.asistencia;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SesionFormacionRepository extends JpaRepository<SesionFormacion, Long> {

    List<SesionFormacion> findByFichaId(Long fichaId);

    List<SesionFormacion> findByFichaIdInOrderByFechaDesc(Collection<Long> fichaIds);

    List<SesionFormacion> findByFichaIdInAndFechaGreaterThanEqualOrderByFechaAscHoraInicioAsc(
            Collection<Long> fichaIds, LocalDate desde);
}
