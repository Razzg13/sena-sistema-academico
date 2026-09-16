package com.sena.academico.auditoria;

import com.sena.academico.auditoria.RegistroAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RegistroAuditoriaRepository
        extends JpaRepository<RegistroAuditoria, Long>, JpaSpecificationExecutor<RegistroAuditoria> {

    long countByEntidadAndEntidadId(String entidad, Long entidadId);
}
