package com.sena.cipa.controlador;

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

import com.sena.cipa.dao.AsignacionInstructorDao;
import com.sena.cipa.dao.FichaDao;
import com.sena.cipa.dao.InstructorDao;
import com.sena.cipa.dao.UsuarioDao;
import com.sena.cipa.modelo.AsignacionInstructor;
import com.sena.cipa.modelo.Instructor;
import com.sena.cipa.modelo.enums.RolNombre;

@Controller
@RequestMapping("/instructores")
public class InstructorController {

    private final InstructorDao instructorDao;
    private final UsuarioDao usuarioDao;
    private final AsignacionInstructorDao asignacionDao;
    private final FichaDao fichaDao;

    public InstructorController(InstructorDao instructorDao, UsuarioDao usuarioDao,
                                 AsignacionInstructorDao asignacionDao, FichaDao fichaDao) {
        this.instructorDao = instructorDao;
        this.usuarioDao = usuarioDao;
        this.asignacionDao = asignacionDao;
        this.fichaDao = fichaDao;
    }

    @GetMapping
    public String listado(Model model) {
        model.addAttribute("instructores", instructorDao.findAll());
        return "instructor/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("instructor", new Instructor());
        agregarUsuariosDisponibles(model);
        return "instructor/formulario";
    }

    private void agregarUsuariosDisponibles(Model model) {
        model.addAttribute("usuarios", usuarioDao.findAll().stream()
                .filter(u -> u.getRol() == RolNombre.INSTRUCTOR)
                .filter(u -> !instructorDao.existsByUsuarioId(u.getId()))
                .toList());
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Instructor instructor, BindingResult resultado, Model model,
                           RedirectAttributes redirectAttributes) {
        if (resultado.hasErrors()) {
            agregarUsuariosDisponibles(model);
            return "instructor/formulario";
        }
        instructorDao.save(instructor);
        redirectAttributes.addFlashAttribute("mensaje", "Instructor guardado correctamente.");
        return "redirect:/instructores";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("instructor", instructorDao.findById(id).orElseThrow());
        model.addAttribute("asignaciones", asignacionDao.findByInstructorId(id));
        model.addAttribute("fichas", fichaDao.findAll());
        model.addAttribute("nuevaAsignacion", new AsignacionInstructor());
        return "instructor/detalle";
    }

    @PostMapping("/{id}/asignaciones")
    public String asignar(@PathVariable Long id, @ModelAttribute AsignacionInstructor nuevaAsignacion,
                           RedirectAttributes redirectAttributes) {
        Instructor instructor = instructorDao.findById(id).orElseThrow();
        nuevaAsignacion.setInstructor(instructor);
        asignacionDao.save(nuevaAsignacion);
        redirectAttributes.addFlashAttribute("mensaje", "Asignacion registrada.");
        return "redirect:/instructores/" + id;
    }

    @PostMapping("/asignaciones/{asignacionId}/eliminar")
    public String eliminarAsignacion(@PathVariable Long asignacionId, RedirectAttributes redirectAttributes) {
        AsignacionInstructor asignacion = asignacionDao.findById(asignacionId).orElseThrow();
        Long instructorId = asignacion.getInstructor().getId();
        asignacionDao.deleteById(asignacionId);
        redirectAttributes.addFlashAttribute("mensaje", "Asignacion eliminada.");
        return "redirect:/instructores/" + instructorId;
    }
}
