package com.sena.academico.aprendiz;

import com.sena.academico.aprendiz.EstadoFormativo;
import com.sena.academico.aprendiz.AprendizService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AprendizController {

    private final AprendizService aprendizService;

    public AprendizController(AprendizService aprendizService) {
        this.aprendizService = aprendizService;
    }

    @GetMapping("/coordinacion/aprendices")
    public String listar(Model model) {
        model.addAttribute("aprendices", aprendizService.listarActivos());
        model.addAttribute("estadosFormativos", EstadoFormativo.values());
        return "aprendiz/listado";
    }

    @PostMapping("/coordinacion/aprendices")
    public String crear(@RequestParam String correo, RedirectAttributes redirectAttributes) {
        aprendizService.crear(correo);
        redirectAttributes.addFlashAttribute("mensaje", "Perfil de aprendiz creado para " + correo + ".");
        return "redirect:/coordinacion/aprendices";
    }

    @PostMapping("/coordinacion/aprendices/{id}/estado-formativo")
    public String cambiarEstadoFormativo(@PathVariable Long id, @RequestParam EstadoFormativo estado,
                                          RedirectAttributes redirectAttributes) {
        aprendizService.cambiarEstadoFormativo(id, estado);
        redirectAttributes.addFlashAttribute("mensaje", "Estado formativo actualizado.");
        return "redirect:/coordinacion/aprendices";
    }
}
