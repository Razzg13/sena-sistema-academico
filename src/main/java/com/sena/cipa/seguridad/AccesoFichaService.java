package com.sena.cipa.seguridad;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.sena.cipa.instructor.AsignacionInstructorRepository;
import com.sena.cipa.instructor.InstructorRepository;

/**
 * Un INSTRUCTOR solo opera sobre las fichas que tiene asignadas; ADMINISTRADOR y
 * COORDINACION_ACADEMICA operan sobre todas.
 */
@Service
public class AccesoFichaService {

    private final InstructorRepository instructorRepository;
    private final AsignacionInstructorRepository asignacionRepository;

    public AccesoFichaService(InstructorRepository instructorRepository,
                               AsignacionInstructorRepository asignacionRepository) {
        this.instructorRepository = instructorRepository;
        this.asignacionRepository = asignacionRepository;
    }

    public boolean esInstructor(UsuarioPrincipal principal) {
        return principal.getUsuario().getRol() == RolNombre.INSTRUCTOR;
    }

    public Set<Long> fichasAsignadas(Usuario usuario) {
        return instructorRepository.findByUsuarioId(usuario.getId())
                .map(instructor -> asignacionRepository.findByInstructorId(instructor.getId()).stream()
                        .map(asignacion -> asignacion.getFicha().getId())
                        .collect(Collectors.toSet()))
                .orElseGet(Set::of);
    }

    /** Vacio = sin restriccion de ficha; con valor = solo esas fichas. */
    public Optional<Set<Long>> fichasRestringidas(UsuarioPrincipal principal) {
        if (!esInstructor(principal)) {
            return Optional.empty();
        }
        return Optional.of(fichasAsignadas(principal.getUsuario()));
    }

    public boolean puedeOperarFicha(UsuarioPrincipal principal, Long fichaId) {
        return fichasRestringidas(principal).map(ids -> ids.contains(fichaId)).orElse(true);
    }

    public void exigirFicha(UsuarioPrincipal principal, Long fichaId) {
        if (!puedeOperarFicha(principal, fichaId)) {
            throw new AccessDeniedException("La ficha no esta asignada a este instructor");
        }
    }
}
