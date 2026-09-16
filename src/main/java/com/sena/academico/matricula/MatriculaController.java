package com.sena.academico.matricula;

import com.sena.academico.matricula.MatriculaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @GetMapping("/coordinacion/matriculas")
    public String listar(Model model) {
        model.addAttribute("matriculas", matriculaService.listarTodas());
        return "matricula/listado";
    }

    @PostMapping("/coordinacion/fichas/{fichaId}/matriculas")
    public String matricular(@PathVariable Long fichaId, @RequestParam Long aprendizId,
                              RedirectAttributes redirectAttributes) {
        matriculaService.matricular(aprendizId, fichaId);
        redirectAttributes.addFlashAttribute("mensaje", "Aprendiz matriculado en la ficha.");
        return "redirect:/coordinacion/fichas/" + fichaId;
    }

    @PostMapping("/coordinacion/matriculas/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Long fichaId = matriculaService.obtener(id).getFicha().getId();
        matriculaService.cancelar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Matrícula cancelada.");
        return "redirect:/coordinacion/fichas/" + fichaId;
    }
}
