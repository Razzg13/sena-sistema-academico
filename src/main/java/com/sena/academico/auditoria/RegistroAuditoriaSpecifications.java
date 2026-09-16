package com.sena.academico.auditoria;

import com.sena.academico.auditoria.Accion;
import com.sena.academico.auditoria.RegistroAuditoria;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class RegistroAuditoriaSpecifications {

    private RegistroAuditoriaSpecifications() {
    }

    public static Specification<RegistroAuditoria> usuarioContiene(String usuario) {
        if (usuario == null || usuario.isBlank()) {
            return null;
        }
        String patron = "%" + usuario.toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("usuario")), patron);
    }

    public static Specification<RegistroAuditoria> entidadContiene(String entidad) {
        if (entidad == null || entidad.isBlank()) {
            return null;
        }
        String patron = "%" + entidad.toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("entidad")), patron);
    }

    public static Specification<RegistroAuditoria> accionEs(Accion accion) {
        if (accion == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("accion"), accion);
    }

    public static Specification<RegistroAuditoria> fechaDesde(LocalDateTime desde) {
        if (desde == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("fecha"), desde);
    }

    public static Specification<RegistroAuditoria> fechaHasta(LocalDateTime hasta) {
        if (hasta == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("fecha"), hasta);
    }
}
