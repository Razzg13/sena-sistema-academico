package com.sena.academico.matricula;

import com.sena.academico.aprendiz.Aprendiz;
import com.sena.academico.aprendiz.AprendizService;
import com.sena.academico.auditoria.Accion;
import com.sena.academico.auditoria.AuditoriaService;
import com.sena.academico.common.NegocioException;
import com.sena.academico.ficha.Ficha;
import com.sena.academico.ficha.FichaService;
import com.sena.academico.matricula.EstadoMatricula;
import com.sena.academico.matricula.Matricula;
import com.sena.academico.matricula.MatriculaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AprendizService aprendizService;
    private final FichaService fichaService;
    private final AuditoriaService auditoriaService;

    public MatriculaService(MatriculaRepository matriculaRepository, AprendizService aprendizService,
                             FichaService fichaService, AuditoriaService auditoriaService) {
        this.matriculaRepository = matriculaRepository;
        this.aprendizService = aprendizService;
        this.fichaService = fichaService;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public Matricula matricular(Long aprendizId, Long fichaId) {
        if (matriculaRepository.existsByAprendizIdAndFichaIdAndEstado(aprendizId, fichaId, EstadoMatricula.ACTIVA)) {
            throw new NegocioException("Este aprendiz ya está matriculado (activo) en esta ficha.");
        }
        Aprendiz aprendiz = aprendizService.obtener(aprendizId);
        Ficha ficha = fichaService.obtener(fichaId);

        Matricula matricula = new Matricula(aprendiz, ficha, LocalDate.now());
        matricula = matriculaRepository.save(matricula);
        auditoriaService.registrar(Accion.CREACION, "Matricula", matricula.getId(),
                "Aprendiz " + aprendiz.getUsuario().getNombreCompleto() + " matriculado en ficha " + ficha.getNumero());
        return matricula;
    }

    @Transactional
    public void cancelar(Long matriculaId) {
        Matricula matricula = obtener(matriculaId);
        matricula.cancelar();
        auditoriaService.registrar(Accion.INACTIVACION, "Matricula", matricula.getId(),
                "Matrícula cancelada: " + matricula.getAprendiz().getUsuario().getNombreCompleto()
                        + " en ficha " + matricula.getFicha().getNumero());
    }

    public Matricula obtener(Long id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new NegocioException("La matrícula solicitada no existe."));
    }

    public List<Matricula> listarPorFicha(Long fichaId) {
        return matriculaRepository.findByFichaIdOrderByFechaMatriculaDesc(fichaId);
    }

    public List<Matricula> listarTodas() {
        return matriculaRepository.findAllByOrderByFechaMatriculaDesc();
    }

    /**
     * Matriculas del aprendiz autenticado (autogestion), a traves de su Usuario.
     */
    public List<Matricula> listarPorAprendizUsuario(Long usuarioId) {
        return matriculaRepository.findByAprendiz_Usuario_IdOrderByFechaMatriculaDesc(usuarioId);
    }

    /**
     * La matricula del aprendiz autenticado en una ficha concreta, para verificar que
     * solo consulte/actue sobre sus propios datos (no los de otro aprendiz).
     */
    public Matricula obtenerPropia(Long fichaId, Long usuarioId) {
        return matriculaRepository.findByFicha_IdAndAprendiz_Usuario_Id(fichaId, usuarioId)
                .orElseThrow(() -> new NegocioException("No estás matriculado en esta ficha."));
    }
}
