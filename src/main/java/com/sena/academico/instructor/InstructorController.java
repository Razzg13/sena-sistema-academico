package com.sena.academico.instructor;

import com.sena.academico.instructor.InstructorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class InstructorController {

    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping("/coordinacion/instructores")
    public String listar(Model model) {
        model.addAttribute("instructores", instructorService.listarActivos());
        return "instructor/listado";
    }

    @PostMapping("/coordinacion/instructores")
    public String crear(@RequestParam String correo, @RequestParam String especialidad,
                         RedirectAttributes redirectAttributes) {
        instructorService.crear(correo, especialidad);
        redirectAttributes.addFlashAttribute("mensaje", "Perfil de instructor creado para " + correo + ".");
        return "redirect:/coordinacion/instructores";
    }
}
