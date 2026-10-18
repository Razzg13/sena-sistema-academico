package com.sena.cipa.evaluacion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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

import com.sena.cipa.common.Fechas;
import com.sena.cipa.common.NegocioException;
import com.sena.cipa.entrega.Entrega;
import com.sena.cipa.entrega.EntregaRepository;
import com.sena.cipa.ficha.EstadoFicha;
import com.sena.cipa.ficha.Ficha;
import com.sena.cipa.ficha.FichaRepository;
import com.sena.cipa.matricula.EstadoMatricula;
import com.sena.cipa.matricula.Matricula;
import com.sena.cipa.matricula.MatriculaRepository;
import com.sena.cipa.seguridad.AccesoFichaService;
import com.sena.cipa.seguridad.UsuarioPrincipal;

@Controller
@RequestMapping("/actividades")
public class ActividadEvaluativaController {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final BigDecimal NOTA_MAXIMA = new BigDecimal("5");

    private final ActividadEvaluativaRepository actividadRepository;
    private final FichaRepository fichaRepository;
    private final MatriculaRepository matriculaRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final EntregaRepository entregaRepository;
    private final AccesoFichaService acceso;

    public ActividadEvaluativaController(ActividadEvaluativaRepository actividadRepository, FichaRepository fichaRepository,
                                          MatriculaRepository matriculaRepository, EvaluacionRepository evaluacionRepository,
                                          EntregaRepository entregaRepository, AccesoFichaService acceso) {
        this.actividadRepository = actividadRepository;
        this.fichaRepository = fichaRepository;
        this.matriculaRepository = matriculaRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.entregaRepository = entregaRepository;
        this.acceso = acceso;
    }

    public record FilaActividad(ActividadEvaluativa actividad, int matriculados, int entregas, int calificadas) {
    }

    @GetMapping
    public String listado(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        List<ActividadEvaluativa> actividades = acceso.fichasRestringidas(principal)
                .map(ids -> ids.isEmpty() ? List.<ActividadEvaluativa>of() : actividadRepository.findByFichaIdIn(ids))
                .orElseGet(actividadRepository::findAll);

        Map<Long, Integer> matriculadosPorFicha = new HashMap<>();
        List<FilaActividad> filas = new ArrayList<>();
        for (ActividadEvaluativa actividad : actividades) {
            Long fichaId = actividad.getFicha().getId();
            int matriculados = matriculadosPorFicha.computeIfAbsent(fichaId,
                    id -> matriculaRepository.findByFichaIdAndEstado(id, EstadoMatricula.ACTIVA).size());
            int entregas = entregaRepository.findByActividadId(actividad.getId()).size();
            int calificadas = (int) evaluacionRepository.findByActividadId(actividad.getId()).stream()
                    .filter(e -> e.getCalificacion() != null).count();
            filas.add(new FilaActividad(actividad, matriculados, entregas, calificadas));
        }
        filas.sort(Comparator.comparing((FilaActividad f) -> f.actividad().getFechaLimite()).reversed());

        model.addAttribute("filas", filas);
        model.addAttribute("soloMisFichas", acceso.esInstructor(principal));
        return "evaluacion/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(@RequestParam(required = false) Long ficha,
                         @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        ActividadEvaluativa actividad = new ActividadEvaluativa();
        if (ficha != null && acceso.puedeOperarFicha(principal, ficha)) {
            fichaRepository.findById(ficha).ifPresent(actividad::setFicha);
        }
        model.addAttribute("actividad", actividad);
        model.addAttribute("fichas", fichasEnEjecucionPermitidas(principal));
        return "evaluacion/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute ActividadEvaluativa actividad,
                           @AuthenticationPrincipal UsuarioPrincipal principal, RedirectAttributes redirectAttributes) {
        Ficha ficha = actividad.getFicha();
        if (ficha == null) {
            throw new NegocioException("Selecciona la ficha a la que se asigna la actividad.", "/actividades/nuevo");
        }
        acceso.exigirFicha(principal, ficha.getId());
        if (ficha.getEstado() != EstadoFicha.EN_EJECUCION) {
            throw new NegocioException("Solo se pueden asignar actividades a fichas en ejecucion.", "/actividades/nuevo");
        }
        if (actividad.getNombre() == null || actividad.getNombre().isBlank()) {
            throw new NegocioException("La actividad necesita un nombre.", "/actividades/nuevo");
        }
        actividadRepository.save(actividad);
        redirectAttributes.addFlashAttribute("mensaje", "Actividad asignada a la ficha " + ficha.getNumero()
                + ". Los aprendices ya pueden verla y entregarla.");
        return "redirect:/actividades";
    }

    @GetMapping("/{id}/calificar")
    public String calificar(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        ActividadEvaluativa actividad = actividadRepository.findById(id).orElseThrow();
        acceso.exigirFicha(principal, actividad.getFicha().getId());

        Map<Long, Entrega> entregas = entregaRepository.findByActividadId(id).stream()
                .collect(Collectors.toMap(e -> e.getMatricula().getId(), Function.identity()));

        CalificarForm form = new CalificarForm();
        for (Matricula matricula : matriculasActivas(actividad)) {
            CalificarForm.Item item = new CalificarForm.Item();
            item.setMatriculaId(matricula.getId());
            item.setNombreAprendiz(matricula.getAprendiz().getUsuario().getNombreCompleto());
            evaluacionRepository.findByActividadIdAndMatriculaId(id, matricula.getId()).ifPresent(existente -> {
                item.setCalificacion(existente.getCalificacion());
                item.setJuicio(existente.getJuicio());
                item.setRetroalimentacion(existente.getRetroalimentacion());
            });
            Entrega entrega = entregas.get(matricula.getId());
            if (entrega != null) {
                item.setEntregaId(entrega.getId());
                item.setEntregaFecha(FORMATO_FECHA.format(entrega.getFechaEntrega()));
                item.setEntregaComentario(entrega.getComentario());
                item.setEntregaArchivo(entrega.getNombreArchivo());
                item.setEntregaTardia(entrega.isTardia());
            }
            form.getItems().add(item);
        }

        model.addAttribute("actividad", actividad);
        model.addAttribute("form", form);
        model.addAttribute("juicios", JuicioEvaluacion.values());
        return "evaluacion/calificar";
    }

    @PostMapping("/{id}/calificar")
    public String guardarCalificacion(@PathVariable Long id, @ModelAttribute CalificarForm form,
                                       @AuthenticationPrincipal UsuarioPrincipal principal,
                                       RedirectAttributes redirectAttributes) {
        ActividadEvaluativa actividad = actividadRepository.findById(id).orElseThrow();
        acceso.exigirFicha(principal, actividad.getFicha().getId());

        Map<Long, Matricula> permitidas = matriculasActivas(actividad).stream()
                .collect(Collectors.toMap(Matricula::getId, Function.identity()));
        String destino = "/actividades/" + id + "/calificar";

        List<Evaluacion> porGuardar = new ArrayList<>();
        for (CalificarForm.Item item : form.getItems()) {
            Matricula matricula = permitidas.get(item.getMatriculaId());
            if (matricula == null) {
                throw new NegocioException("Hay un aprendiz que no pertenece a esta ficha o no tiene matricula activa.", destino);
            }
            BigDecimal nota = item.getCalificacion();
            String retroalimentacion = item.getRetroalimentacion() == null ? "" : item.getRetroalimentacion().trim();
            Optional<Evaluacion> existente = evaluacionRepository.findByActividadIdAndMatriculaId(id, matricula.getId());

            if (nota == null && retroalimentacion.isEmpty() && existente.isEmpty()) {
                continue;
            }
            if (nota != null && (nota.signum() < 0 || nota.compareTo(NOTA_MAXIMA) > 0)) {
                throw new NegocioException("La calificacion de " + matricula.getAprendiz().getUsuario().getNombreCompleto()
                        + " debe estar entre 0.00 y 5.00.", destino);
            }
            if (retroalimentacion.length() > 500) {
                throw new NegocioException("La retroalimentacion no puede superar los 500 caracteres.", destino);
            }
            if (item.getJuicio() == null) {
                throw new NegocioException("Falta el juicio evaluativo de un aprendiz.", destino);
            }

            Evaluacion evaluacion = existente.orElseGet(Evaluacion::new);
            evaluacion.setActividad(actividad);
            evaluacion.setMatricula(matricula);
            evaluacion.setCalificacion(nota == null ? null : nota.setScale(2, RoundingMode.HALF_UP));
            evaluacion.setJuicio(item.getJuicio());
            evaluacion.setRetroalimentacion(retroalimentacion.isEmpty() ? null : retroalimentacion);
            evaluacion.setFechaEvaluacion(Fechas.hoy());
            porGuardar.add(evaluacion);
        }

        evaluacionRepository.saveAll(porGuardar);
        redirectAttributes.addFlashAttribute("mensaje", porGuardar.isEmpty()
                ? "No se registraron calificaciones: ingresa una nota o una retroalimentacion."
                : "Calificaciones guardadas (" + porGuardar.size() + ").");
        return "redirect:/actividades";
    }

    private List<Matricula> matriculasActivas(ActividadEvaluativa actividad) {
        List<Matricula> matriculas = new ArrayList<>(
                matriculaRepository.findByFichaIdAndEstado(actividad.getFicha().getId(), EstadoMatricula.ACTIVA));
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
