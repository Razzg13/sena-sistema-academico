package com.sena.academico.ficha;

import com.sena.academico.auditoria.Accion;
import com.sena.academico.auditoria.AuditoriaService;
import com.sena.academico.common.NegocioException;
import com.sena.academico.ficha.AsignacionInstructor;
import com.sena.academico.ficha.EstadoFicha;
import com.sena.academico.ficha.Ficha;
import com.sena.academico.ficha.AsignacionInstructorRepository;
import com.sena.academico.ficha.FichaRepository;
import com.sena.academico.instructor.Instructor;
import com.sena.academico.instructor.InstructorService;
import com.sena.academico.programa.Programa;
import com.sena.academico.programa.ProgramaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class FichaService {

    private final FichaRepository fichaRepository;
    private final ProgramaService programaService;
    private final InstructorService instructorService;
    private final AuditoriaService auditoriaService;
    private final AsignacionInstructorRepository asignacionInstructorRepository;

    public FichaService(FichaRepository fichaRepository, ProgramaService programaService,
                         InstructorService instructorService, AuditoriaService auditoriaService,
                         AsignacionInstructorRepository asignacionInstructorRepository) {
        this.fichaRepository = fichaRepository;
        this.programaService = programaService;
        this.instructorService = instructorService;
        this.auditoriaService = auditoriaService;
        this.asignacionInstructorRepository = asignacionInstructorRepository;
    }

    @Transactional
    public Ficha crear(NuevaFichaForm form) {
        if (fichaRepository.existsByNumero(form.getNumero())) {
            throw new NegocioException("Ya existe una ficha con el número " + form.getNumero() + ".");
        }
        if (!form.getFechaFin().isAfter(form.getFechaInicio())) {
            throw new NegocioException("La fecha de fin debe ser posterior a la fecha de inicio.");
        }
        Programa programa = programaService.obtener(form.getProgramaId());

        Ficha ficha = new Ficha(form.getNumero(), programa, form.getJornada(),
                form.getFechaInicio(), form.getFechaFin(), form.getAmbiente());
        ficha = fichaRepository.save(ficha);
        auditoriaService.registrar(Accion.CREACION, "Ficha", ficha.getId(),
                "Ficha creada: " + ficha.getNumero() + " (programa " + programa.getCodigo() + ")");
        return ficha;
    }

    public Ficha obtener(Long id) {
        return fichaRepository.findById(id)
                .orElseThrow(() -> new NegocioException("La ficha solicitada no existe."));
    }

    public Page<Ficha> buscar(String texto, Pageable pageable) {
        Specification<Ficha> spec = (texto == null || texto.isBlank())
                ? (root, query, cb) -> cb.conjunction()
                : (root, query, cb) -> cb.like(cb.lower(root.get("numero")), "%" + texto.toLowerCase() + "%");
        return fichaRepository.findAll(spec, pageable);
    }

    @Transactional
    public void cambiarEstado(Long fichaId, EstadoFicha nuevoEstado) {
        Ficha ficha = obtener(fichaId);
        ficha.cambiarEstado(nuevoEstado);
        auditoriaService.registrar(Accion.MODIFICACION, "Ficha", ficha.getId(),
                "Estado cambiado a " + nuevoEstado);
    }

    @Transactional
    public AsignacionInstructor asignarInstructor(Long fichaId, Long instructorId, LocalDate desde, LocalDate hasta) {
        if (hasta.isBefore(desde)) {
            throw new NegocioException("La fecha de fin de la asignación no puede ser anterior a la de inicio.");
        }
        Ficha ficha = obtener(fichaId);
        Instructor instructor = instructorService.obtener(instructorId);

        AsignacionInstructor asignacion = new AsignacionInstructor(instructor, desde, hasta);
        ficha.agregarAsignacion(asignacion);
        fichaRepository.save(ficha);
        auditoriaService.registrar(Accion.MODIFICACION, "Ficha", ficha.getId(),
                "Instructor asignado: " + instructor.getUsuario().getNombreCompleto() + " (" + desde + " a " + hasta + ")");
        return asignacion;
    }

    /**
     * Fichas donde el usuario (via su perfil de instructor) tiene alguna asignacion,
     * usadas para el acceso de autogestion del instructor bajo /coordinacion/fichas.
     */
    public List<Ficha> listarPorInstructor(Long usuarioId) {
        return asignacionInstructorRepository.findByInstructor_Usuario_Id(usuarioId).stream()
                .map(AsignacionInstructor::getFicha)
                .distinct()
                .sorted(Comparator.comparing(Ficha::getNumero))
                .toList();
    }

    public boolean estaAsignado(Long fichaId, Long usuarioId) {
        return asignacionInstructorRepository.existsByFicha_IdAndInstructor_Usuario_Id(fichaId, usuarioId);
    }
}
