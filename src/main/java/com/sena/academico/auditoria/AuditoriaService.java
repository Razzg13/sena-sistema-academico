package com.sena.academico.auditoria;

import com.sena.academico.auditoria.Accion;
import com.sena.academico.auditoria.RegistroAuditoria;
import com.sena.academico.auditoria.RegistroAuditoriaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.sena.academico.auditoria.RegistroAuditoriaSpecifications.accionEs;
import static com.sena.academico.auditoria.RegistroAuditoriaSpecifications.entidadContiene;
import static com.sena.academico.auditoria.RegistroAuditoriaSpecifications.fechaDesde;
import static com.sena.academico.auditoria.RegistroAuditoriaSpecifications.fechaHasta;
import static com.sena.academico.auditoria.RegistroAuditoriaSpecifications.usuarioContiene;

/**
 * Registra la bitacora de operaciones criticas (RF-15). Los demas modulos llaman a
 * {@link #registrar} despues de crear, modificar, inactivar o evaluar una entidad.
 */
@Service
public class AuditoriaService {

    private final RegistroAuditoriaRepository repository;

    public AuditoriaService(RegistroAuditoriaRepository repository) {
        this.repository = repository;
    }

    public RegistroAuditoria registrar(Accion accion, String entidad, Long entidadId, String detalle) {
        RegistroAuditoria registro = new RegistroAuditoria(
                LocalDateTime.now(),
                usuarioActual(),
                accion,
                entidad,
                entidadId,
                ipActual(),
                detalle
        );
        return repository.save(registro);
    }

    public Page<RegistroAuditoria> buscar(FiltroAuditoria filtro, Pageable pageable) {
        List<Specification<RegistroAuditoria>> specs = Stream.of(
                        usuarioContiene(filtro.usuario()),
                        entidadContiene(filtro.entidad()),
                        accionEs(filtro.accion()),
                        fechaDesde(filtro.desde()),
                        fechaHasta(filtro.hasta()))
                .filter(Objects::nonNull)
                .toList();

        Specification<RegistroAuditoria> spec = specs.isEmpty()
                ? (root, query, cb) -> cb.conjunction()
                : Specification.allOf(specs);
        return repository.findAll(spec, pageable);
    }

    private String usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "sistema";
        }
        return auth.getName();
    }

    private String ipActual() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) {
                return "n/a";
            }
            HttpServletRequest request = attrs.getRequest();
            String forwarded = request.getHeader("X-Forwarded-For");
            return (forwarded != null && !forwarded.isBlank()) ? forwarded : request.getRemoteAddr();
        } catch (IllegalStateException e) {
            return "n/a";
        }
    }
}
