package com.sena.cipa.servicio;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.sena.cipa.dao.AsignacionInstructorDao;
import com.sena.cipa.dao.InstructorDao;
import com.sena.cipa.modelo.Usuario;
import com.sena.cipa.modelo.enums.RolNombre;
import com.sena.cipa.seguridad.UsuarioPrincipal;

@Service
public class AccesoFichaService {

    private final InstructorDao instructorDao;
    private final AsignacionInstructorDao asignacionDao;

    public AccesoFichaService(InstructorDao instructorDao,
                               AsignacionInstructorDao asignacionDao) {
        this.instructorDao = instructorDao;
        this.asignacionDao = asignacionDao;
    }

    public boolean esInstructor(UsuarioPrincipal principal) {
        return principal.getUsuario().getRol() == RolNombre.INSTRUCTOR;
    }

    public Set<Long> fichasAsignadas(Usuario usuario) {
        return instructorDao.findByUsuarioId(usuario.getId())
                .map(instructor -> asignacionDao.findByInstructorId(instructor.getId()).stream()
                        .map(asignacion -> asignacion.getFicha().getId())
                        .collect(Collectors.toSet()))
                .orElseGet(Set::of);
    }

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
