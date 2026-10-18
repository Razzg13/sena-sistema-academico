package com.sena.cipa.programa;

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

import com.sena.cipa.common.EstadoGeneral;

@Controller
@RequestMapping("/programas")
public class ProgramaController {

    private final ProgramaRepository programaRepository;

    public ProgramaController(ProgramaRepository programaRepository) {
        this.programaRepository = programaRepository;
    }

    @GetMapping
    public String listado(Model model) {
        model.addAttribute("programas", programaRepository.findAll());
        return "programa/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("programa", new Programa());
        model.addAttribute("niveles", NivelFormacion.values());
        return "programa/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("programa", programaRepository.findById(id).orElseThrow());
        model.addAttribute("niveles", NivelFormacion.values());
        return "programa/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Programa programa, BindingResult resultado, Model model,
                           RedirectAttributes redirectAttributes) {
        if (resultado.hasErrors()) {
            model.addAttribute("niveles", NivelFormacion.values());
            return "programa/formulario";
        }
        if (programa.getEstado() == null) {
            programa.setEstado(EstadoGeneral.ACTIVO);
        }
        programaRepository.save(programa);
        redirectAttributes.addFlashAttribute("mensaje", "Programa guardado correctamente.");
        return "redirect:/programas";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        programaRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("mensaje", "Programa eliminado.");
        return "redirect:/programas";
    }
}
