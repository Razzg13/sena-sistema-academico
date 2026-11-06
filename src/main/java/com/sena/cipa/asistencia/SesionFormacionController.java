package com.sena.cipa.asistencia;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sena.cipa.common.NegocioException;
import com.sena.cipa.ficha.EstadoFicha;
import com.sena.cipa.ficha.Ficha;
import com.sena.cipa.ficha.FichaRepository;
import com.sena.cipa.matricula.EstadoMatricula;
import com.sena.cipa.matricula.Matricula;
import com.sena.cipa.matricula.MatriculaRepository;
import com.sena.cipa.seguridad.AccesoFichaService;
import com.sena.cipa.seguridad.UsuarioPrincipal;

@Controller
@RequestMapping("/sesiones")
public class SesionFormacionController {

    private final SesionFormacionRepository sesionRepository;
    private final FichaRepository fichaRepository;
    private final MatriculaRepository matriculaRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final AccesoFichaService acceso;

    public SesionFormacionController(SesionFormacionRepository sesionRepository, FichaRepository fichaRepository,
                                      MatriculaRepository matriculaRepository, AsistenciaRepository asistenciaRepository,
                                      AccesoFichaService acceso) {
        this.sesionRepository = sesionRepository;
        this.fichaRepository = fichaRepository;
        this.matriculaRepository = matriculaRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.acceso = acceso;
    }

    @GetMapping
    public String listado(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        List<SesionFormacion> sesiones = acceso.fichasRestringidas(principal)
                .map(ids -> ids.isEmpty() ? List.<SesionFormacion>of()
                        : sesionRepository.findByFichaIdInOrderByFechaDesc(ids))
                .orElseGet(() -> sesionRepository.findAll(Sort.by(Sort.Direction.DESC, "fecha")));
        model.addAttribute("sesiones", sesiones);
        model.addAttribute("soloMisFichas", acceso.esInstructor(principal));
        return "asistencia/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(@RequestParam(required = false) Long ficha,
                         @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        SesionFormacion sesion = new SesionFormacion();
        if (ficha != null && acceso.puedeOperarFicha(principal, ficha)) {
            fichaRepository.findById(ficha).ifPresent(sesion::setFicha);
        }
        model.addAttribute("sesion", sesion);
        model.addAttribute("fichas", fichasEnEjecucionPermitidas(principal));
        return "asistencia/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute SesionFormacion sesion,
                           @AuthenticationPrincipal UsuarioPrincipal principal, RedirectAttributes redirectAttributes) {
        Ficha ficha = sesion.getFicha();
        if (ficha == null) {
            throw new NegocioException("Selecciona una ficha para la sesion.", "/sesiones/nuevo");
        }
        acceso.exigirFicha(principal, ficha.getId());
        if (ficha.getEstado() != EstadoFicha.EN_EJECUCION) {
            throw new NegocioException("Solo se pueden registrar sesiones en fichas en ejecucion.", "/sesiones/nuevo");
        }
        if (sesion.getHoraInicio() != null && sesion.getHoraFin() != null
                && !sesion.getHoraFin().isAfter(sesion.getHoraInicio())) {
            throw new NegocioException("La hora de fin debe ser posterior a la hora de inicio.", "/sesiones/nuevo");
        }
        sesionRepository.save(sesion);
        redirectAttributes.addFlashAttribute("mensaje", "Sesion registrada.");
        return "redirect:/sesiones";
    }

    @GetMapping("/{id}/llamado")
    public String llamado(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        SesionFormacion sesion = sesionRepository.findById(id).orElseThrow();
        acceso.exigirFicha(principal, sesion.getFicha().getId());

        LlamadoForm form = new LlamadoForm();
        for (Matricula matricula : matriculasActivas(sesion)) {
            LlamadoForm.Item item = new LlamadoForm.Item();
            item.setMatriculaId(matricula.getId());
            item.setNombreAprendiz(matricula.getAprendiz().getUsuario().getNombreCompleto());
            asistenciaRepository.findBySesionIdAndMatriculaId(id, matricula.getId()).ifPresent(existente -> {
                item.setEstado(existente.getEstado());
                item.setObservacion(existente.getObservacion());
            });
            form.getItems().add(item);
        }

        model.addAttribute("sesion", sesion);
        model.addAttribute("form", form);
        model.addAttribute("estados", EstadoAsistencia.values());
        return "asistencia/llamado";
    }

    @PostMapping("/{id}/llamado")
    public String guardarLlamado(@PathVariable Long id, @ModelAttribute LlamadoForm form,
                                  @AuthenticationPrincipal UsuarioPrincipal principal,
                                  RedirectAttributes redirectAttributes) {
        SesionFormacion sesion = sesionRepository.findById(id).orElseThrow();
        acceso.exigirFicha(principal, sesion.getFicha().getId());

        Map<Long, Matricula> permitidas = matriculasActivas(sesion).stream()
                .collect(Collectors.toMap(Matricula::getId, Function.identity()));
        String destino = "/sesiones/" + id + "/llamado";

        List<Asistencia> porGuardar = new ArrayList<>();
        for (LlamadoForm.Item item : form.getItems()) {
            Matricula matricula = permitidas.get(item.getMatriculaId());
            if (matricula == null) {
                throw new NegocioException("Hay un aprendiz que no pertenece a esta ficha o no tiene matricula activa.", destino);
            }
            if (item.getEstado() == null) {
                throw new NegocioException("Falta el estado de asistencia de un aprendiz.", destino);
            }
            if (item.getObservacion() != null && item.getObservacion().length() > 300) {
                throw new NegocioException("La observacion no puede superar los 300 caracteres.", destino);
            }
            Asistencia asistencia = asistenciaRepository.findBySesionIdAndMatriculaId(id, matricula.getId())
                    .orElseGet(Asistencia::new);
            asistencia.setSesion(sesion);
            asistencia.setMatricula(matricula);
            asistencia.setEstado(item.getEstado());
            asistencia.setObservacion(item.getObservacion());
            porGuardar.add(asistencia);
        }
        asistenciaRepository.saveAll(porGuardar);
        redirectAttributes.addFlashAttribute("mensaje", "Asistencia guardada.");
        return "redirect:/sesiones";
    }

    private List<Matricula> matriculasActivas(SesionFormacion sesion) {
        List<Matricula> matriculas = new ArrayList<>(
                matriculaRepository.findByFichaIdAndEstado(sesion.getFicha().getId(), EstadoMatricula.ACTIVA));
        matriculas.sort(Comparator.comparing(m -> m.getAprendiz().getUsuario().getNombreCompleto()));
        return matriculas;
    }

    private List<Ficha> fichasEnEjecucionPermitidas(UsuarioPrincipal principal) {
        List<Ficha> fichas = acceso.fichasRestringidas(principal)
                .map(ids -> fichaRepository.findAllById(ids))
                .orElseGet(fichaRepository::findAll);
        return fichas.stream().filter(f -> f.getEstado() == EstadoFicha.EN_EJECUCION).toList();
    }
}
