package com.sena.cipa.instructor;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sena.cipa.ficha.FichaRepository;
import com.sena.cipa.seguridad.RolNombre;
import com.sena.cipa.seguridad.UsuarioRepository;

@Controller
@RequestMapping("/instructores")
public class InstructorController {

    private final InstructorRepository instructorRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsignacionInstructorRepository asignacionRepository;
    private final FichaRepository fichaRepository;

    public InstructorController(InstructorRepository instructorRepository, UsuarioRepository usuarioRepository,
                                 AsignacionInstructorRepository asignacionRepository, FichaRepository fichaRepository) {
        this.instructorRepository = instructorRepository;
        this.usuarioRepository = usuarioRepository;
        this.asignacionRepository = asignacionRepository;
        this.fichaRepository = fichaRepository;
    }

    @GetMapping
    public String listado(Model model) {
        model.addAttribute("instructores", instructorRepository.findAll());
        return "instructor/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("instructor", new Instructor());
        agregarUsuariosDisponibles(model);
        return "instructor/formulario";
    }

    private void agregarUsuariosDisponibles(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() == RolNombre.INSTRUCTOR)
                .filter(u -> !instructorRepository.existsByUsuarioId(u.getId()))
                .toList());
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Instructor instructor, BindingResult resultado, Model model,
                           RedirectAttributes redirectAttributes) {
        if (resultado.hasErrors()) {
            agregarUsuariosDisponibles(model);
            return "instructor/formulario";
        }
        instructorRepository.save(instructor);
        redirectAttributes.addFlashAttribute("mensaje", "Instructor guardado correctamente.");
        return "redirect:/instructores";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("instructor", instructorRepository.findById(id).orElseThrow());
        model.addAttribute("asignaciones", asignacionRepository.findByInstructorId(id));
        model.addAttribute("fichas", fichaRepository.findAll());
        model.addAttribute("nuevaAsignacion", new AsignacionInstructor());
        return "instructor/detalle";
    }

    @PostMapping("/{id}/asignaciones")
    public String asignar(@PathVariable Long id, @ModelAttribute AsignacionInstructor nuevaAsignacion,
                           RedirectAttributes redirectAttributes) {
        Instructor instructor = instructorRepository.findById(id).orElseThrow();
        nuevaAsignacion.setInstructor(instructor);
        asignacionRepository.save(nuevaAsignacion);
        redirectAttributes.addFlashAttribute("mensaje", "Asignacion registrada.");
        return "redirect:/instructores/" + id;
    }

    @PostMapping("/asignaciones/{asignacionId}/eliminar")
    public String eliminarAsignacion(@PathVariable Long asignacionId, RedirectAttributes redirectAttributes) {
        AsignacionInstructor asignacion = asignacionRepository.findById(asignacionId).orElseThrow();
        Long instructorId = asignacion.getInstructor().getId();
        asignacionRepository.deleteById(asignacionId);
        redirectAttributes.addFlashAttribute("mensaje", "Asignacion eliminada.");
        return "redirect:/instructores/" + instructorId;
    }
}
