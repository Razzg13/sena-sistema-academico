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

import com.sena.cipa.dao.FichaDao;
import com.sena.cipa.dao.ProgramaDao;
import com.sena.cipa.modelo.Ficha;
import com.sena.cipa.modelo.enums.EstadoFicha;
import com.sena.cipa.modelo.enums.Jornada;

@Controller
@RequestMapping("/fichas")
public class FichaController {

    private final FichaDao fichaDao;
    private final ProgramaDao programaDao;

    public FichaController(FichaDao fichaDao, ProgramaDao programaDao) {
        this.fichaDao = fichaDao;
        this.programaDao = programaDao;
    }

    @GetMapping
    public String listado(Model model) {
        model.addAttribute("fichas", fichaDao.findAll());
        return "ficha/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("ficha", new Ficha());
        agregarListas(model);
        return "ficha/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("ficha", fichaDao.findById(id).orElseThrow());
        agregarListas(model);
        return "ficha/formulario";
    }

    private void agregarListas(Model model) {
        model.addAttribute("programas", programaDao.findAll());
        model.addAttribute("jornadas", Jornada.values());
        model.addAttribute("estados", EstadoFicha.values());
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Ficha ficha, BindingResult resultado, Model model,
                           RedirectAttributes redirectAttributes) {
        if (resultado.hasErrors()) {
            agregarListas(model);
            return "ficha/formulario";
        }
        if (ficha.getEstado() == null) {
            ficha.setEstado(EstadoFicha.EN_EJECUCION);
        }
        fichaDao.save(ficha);
        redirectAttributes.addFlashAttribute("mensaje", "Ficha guardada correctamente.");
        return "redirect:/fichas";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        fichaDao.deleteById(id);
        redirectAttributes.addFlashAttribute("mensaje", "Ficha eliminada.");
        return "redirect:/fichas";
    }
}
