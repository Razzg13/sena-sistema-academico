package com.sena.academico.programa;

import com.sena.academico.auditoria.Accion;
import com.sena.academico.auditoria.AuditoriaService;
import com.sena.academico.common.NegocioException;
import com.sena.academico.programa.Competencia;
import com.sena.academico.programa.Programa;
import com.sena.academico.programa.ResultadoAprendizaje;
import com.sena.academico.programa.CompetenciaRepository;
import com.sena.academico.programa.ProgramaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProgramaService {

    private final ProgramaRepository programaRepository;
    private final CompetenciaRepository competenciaRepository;
    private final AuditoriaService auditoriaService;

    public ProgramaService(ProgramaRepository programaRepository, CompetenciaRepository competenciaRepository,
                            AuditoriaService auditoriaService) {
        this.programaRepository = programaRepository;
        this.competenciaRepository = competenciaRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public Programa crear(NuevoProgramaForm form) {
        if (programaRepository.existsByCodigo(form.getCodigo())) {
            throw new NegocioException("Ya existe un programa con el código " + form.getCodigo() + ".");
        }
        Programa programa = new Programa(form.getCodigo(), form.getNombre(), form.getNivel(),
                form.getVersion(), form.getDuracionHoras());
        programa = programaRepository.save(programa);
        auditoriaService.registrar(Accion.CREACION, "Programa", programa.getId(),
                "Programa creado: " + programa.getCodigo() + " - " + programa.getNombre());
        return programa;
    }

    public Programa obtener(Long id) {
        return programaRepository.findById(id)
                .orElseThrow(() -> new NegocioException("El programa solicitado no existe."));
    }

    public Page<Programa> buscar(String texto, Pageable pageable) {
        Specification<Programa> spec = (texto == null || texto.isBlank())
                ? (root, query, cb) -> cb.conjunction()
                : (root, query, cb) -> cb.or(
                        cb.like(cb.lower(root.get("nombre")), "%" + texto.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("codigo")), "%" + texto.toLowerCase() + "%"));
        return programaRepository.findAll(spec, pageable);
    }

    @Transactional
    public void desactivar(Long id) {
        Programa programa = obtener(id);
        programa.desactivar();
        auditoriaService.registrar(Accion.INACTIVACION, "Programa", programa.getId(),
                "Programa inactivado: " + programa.getCodigo());
    }

    @Transactional
    public void activar(Long id) {
        Programa programa = obtener(id);
        programa.activar();
        auditoriaService.registrar(Accion.MODIFICACION, "Programa", programa.getId(),
                "Programa reactivado: " + programa.getCodigo());
    }

    @Transactional
    public Competencia agregarCompetencia(Long programaId, String codigo, String nombre) {
        Programa programa = obtener(programaId);
        Competencia competencia = new Competencia(codigo, nombre);
        programa.agregarCompetencia(competencia);
        programaRepository.save(programa);
        auditoriaService.registrar(Accion.MODIFICACION, "Programa", programa.getId(),
                "Competencia agregada: " + codigo + " - " + nombre);
        return competencia;
    }

    @Transactional
    public ResultadoAprendizaje agregarResultadoAprendizaje(Long competenciaId, String codigo, String descripcion) {
        Competencia competencia = competenciaRepository.findById(competenciaId)
                .orElseThrow(() -> new NegocioException("La competencia solicitada no existe."));
        ResultadoAprendizaje resultado = new ResultadoAprendizaje(codigo, descripcion);
        competencia.agregarResultadoAprendizaje(resultado);
        competenciaRepository.save(competencia);
        auditoriaService.registrar(Accion.MODIFICACION, "Programa", competencia.getPrograma().getId(),
                "Resultado de aprendizaje agregado a " + competencia.getCodigo() + ": " + codigo);
        return resultado;
    }
}
