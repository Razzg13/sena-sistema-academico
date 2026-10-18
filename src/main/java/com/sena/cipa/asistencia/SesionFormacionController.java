package com.sena.cipa.asistencia;

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
@RequestMapping("/sesiones")
public class SesionFormacionController {

    private final SesionFormacionRepository sesionRepository;
    private final FichaRepository fichaRepository;
    private final MatriculaRepository matriculaRepository;
    private final AsistenciaRepository asistenciaRepository;

    public SesionFormacionController(SesionFormacionRepository sesionRepository, FichaRepository fichaRepository,
                                      MatriculaRepository matriculaRepository, AsistenciaRepository asistenciaRepository) {
        this.sesionRepository = sesionRepository;
        this.fichaRepository = fichaRepository;
        this.matriculaRepository = matriculaRepository;
        this.asistenciaRepository = asistenciaRepository;
    }

    @GetMapping
    public String listado(Model model) {
        model.addAttribute("sesiones", sesionRepository.findAll());
        return "asistencia/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("sesion", new SesionFormacion());
        model.addAttribute("fichas", fichaRepository.findAll());
        return "asistencia/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute SesionFormacion sesion, RedirectAttributes redirectAttributes) {
        sesionRepository.save(sesion);
        redirectAttributes.addFlashAttribute("mensaje", "Sesion registrada.");
        return "redirect:/sesiones";
    }

    @GetMapping("/{id}/llamado")
    public String llamado(@PathVariable Long id, Model model) {
        SesionFormacion sesion = sesionRepository.findById(id).orElseThrow();
        List<Matricula> matriculas = matriculaRepository.findByFichaId(sesion.getFicha().getId());

        LlamadoForm form = new LlamadoForm();
        for (Matricula matricula : matriculas) {
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
                                  RedirectAttributes redirectAttributes) {
        SesionFormacion sesion = sesionRepository.findById(id).orElseThrow();
        for (LlamadoForm.Item item : form.getItems()) {
            Asistencia asistencia = asistenciaRepository.findBySesionIdAndMatriculaId(id, item.getMatriculaId())
                    .orElseGet(Asistencia::new);
            asistencia.setSesion(sesion);
            asistencia.setMatricula(matriculaRepository.findById(item.getMatriculaId()).orElseThrow());
            asistencia.setEstado(item.getEstado());
            asistencia.setObservacion(item.getObservacion());
            asistenciaRepository.save(asistencia);
        }
        redirectAttributes.addFlashAttribute("mensaje", "Asistencia guardada.");
        return "redirect:/sesiones";
    }
}
