package com.sena.cipa.evaluacion;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sena.cipa.ficha.FichaRepository;
import com.sena.cipa.matricula.Matricula;
import com.sena.cipa.matricula.MatriculaRepository;

@Controller
@RequestMapping("/actividades")
public class ActividadEvaluativaController {

    private final ActividadEvaluativaRepository actividadRepository;
    private final FichaRepository fichaRepository;
    private final MatriculaRepository matriculaRepository;
    private final EvaluacionRepository evaluacionRepository;

    public ActividadEvaluativaController(ActividadEvaluativaRepository actividadRepository, FichaRepository fichaRepository,
                                          MatriculaRepository matriculaRepository, EvaluacionRepository evaluacionRepository) {
        this.actividadRepository = actividadRepository;
        this.fichaRepository = fichaRepository;
        this.matriculaRepository = matriculaRepository;
        this.evaluacionRepository = evaluacionRepository;
    }

    @GetMapping
    public String listado(Model model) {
        model.addAttribute("actividades", actividadRepository.findAll());
        return "evaluacion/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("actividad", new ActividadEvaluativa());
        model.addAttribute("fichas", fichaRepository.findAll());
        return "evaluacion/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute ActividadEvaluativa actividad, RedirectAttributes redirectAttributes) {
        actividadRepository.save(actividad);
        redirectAttributes.addFlashAttribute("mensaje", "Actividad evaluativa registrada.");
        return "redirect:/actividades";
    }

    @GetMapping("/{id}/calificar")
    public String calificar(@PathVariable Long id, Model model) {
        ActividadEvaluativa actividad = actividadRepository.findById(id).orElseThrow();
        List<Matricula> matriculas = matriculaRepository.findByFichaId(actividad.getFicha().getId());

        CalificarForm form = new CalificarForm();
        for (Matricula matricula : matriculas) {
            CalificarForm.Item item = new CalificarForm.Item();
            item.setMatriculaId(matricula.getId());
            item.setNombreAprendiz(matricula.getAprendiz().getUsuario().getNombreCompleto());
            evaluacionRepository.findByActividadIdAndMatriculaId(id, matricula.getId()).ifPresent(existente -> {
                item.setCalificacion(existente.getCalificacion());
                item.setJuicio(existente.getJuicio());
                item.setRetroalimentacion(existente.getRetroalimentacion());
            });
            form.getItems().add(item);
        }

        model.addAttribute("actividad", actividad);
        model.addAttribute("form", form);
        model.addAttribute("juicios", JuicioEvaluacion.values());
        return "evaluacion/calificar";
    }

    @PostMapping("/{id}/calificar")
    public String guardarCalificacion(@PathVariable Long id, @ModelAttribute CalificarForm form,
                                       RedirectAttributes redirectAttributes) {
        ActividadEvaluativa actividad = actividadRepository.findById(id).orElseThrow();
        for (CalificarForm.Item item : form.getItems()) {
            Evaluacion evaluacion = evaluacionRepository.findByActividadIdAndMatriculaId(id, item.getMatriculaId())
                    .orElseGet(Evaluacion::new);
            evaluacion.setActividad(actividad);
            evaluacion.setMatricula(matriculaRepository.findById(item.getMatriculaId()).orElseThrow());
            evaluacion.setCalificacion(item.getCalificacion());
            evaluacion.setJuicio(item.getJuicio());
            evaluacion.setRetroalimentacion(item.getRetroalimentacion());
            evaluacion.setFechaEvaluacion(LocalDate.now());
            evaluacionRepository.save(evaluacion);
        }
        redirectAttributes.addFlashAttribute("mensaje", "Calificaciones guardadas.");
        return "redirect:/actividades";
    }
}
