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

import com.sena.cipa.dao.AprendizDao;
import com.sena.cipa.dao.FichaDao;
import com.sena.cipa.dao.MatriculaDao;
import com.sena.cipa.dao.UsuarioDao;
import com.sena.cipa.modelo.Aprendiz;
import com.sena.cipa.modelo.Matricula;
import com.sena.cipa.modelo.enums.EstadoFormativo;
import com.sena.cipa.modelo.enums.RolNombre;

@Controller
@RequestMapping("/aprendices")
public class AprendizController {

    private final AprendizDao aprendizDao;
    private final UsuarioDao usuarioDao;
    private final MatriculaDao matriculaDao;
    private final FichaDao fichaDao;

    public AprendizController(AprendizDao aprendizDao, UsuarioDao usuarioDao,
                               MatriculaDao matriculaDao, FichaDao fichaDao) {
        this.aprendizDao = aprendizDao;
        this.usuarioDao = usuarioDao;
        this.matriculaDao = matriculaDao;
        this.fichaDao = fichaDao;
    }

    @GetMapping
    public String listado(Model model) {
        model.addAttribute("aprendices", aprendizDao.findAll());
        return "aprendiz/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("aprendiz", new Aprendiz());
        agregarUsuariosDisponibles(model);
        model.addAttribute("estadosFormativos", EstadoFormativo.values());
        return "aprendiz/formulario";
    }

    private void agregarUsuariosDisponibles(Model model) {
        model.addAttribute("usuarios", usuarioDao.findAll().stream()
                .filter(u -> u.getRol() == RolNombre.APRENDIZ)
                .filter(u -> !aprendizDao.existsByUsuarioId(u.getId()))
                .toList());
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Aprendiz aprendiz, BindingResult resultado, Model model,
                           RedirectAttributes redirectAttributes) {
        if (resultado.hasErrors()) {
            agregarUsuariosDisponibles(model);
            model.addAttribute("estadosFormativos", EstadoFormativo.values());
            return "aprendiz/formulario";
        }
        aprendizDao.save(aprendiz);
        redirectAttributes.addFlashAttribute("mensaje", "Aprendiz guardado correctamente.");
        return "redirect:/aprendices";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("aprendiz", aprendizDao.findById(id).orElseThrow());
        model.addAttribute("matriculas", matriculaDao.findByAprendizId(id));
        model.addAttribute("fichas", fichaDao.findAll());
        model.addAttribute("nuevaMatricula", new Matricula());
        return "aprendiz/detalle";
    }

    @PostMapping("/{id}/matriculas")
    public String matricular(@PathVariable Long id, @ModelAttribute Matricula nuevaMatricula,
                              RedirectAttributes redirectAttributes) {
        Aprendiz aprendiz = aprendizDao.findById(id).orElseThrow();
        nuevaMatricula.setAprendiz(aprendiz);
        matriculaDao.save(nuevaMatricula);
        redirectAttributes.addFlashAttribute("mensaje", "Matricula registrada.");
        return "redirect:/aprendices/" + id;
    }

    @PostMapping("/matriculas/{matriculaId}/eliminar")
    public String eliminarMatricula(@PathVariable Long matriculaId, RedirectAttributes redirectAttributes) {
        Matricula matricula = matriculaDao.findById(matriculaId).orElseThrow();
        Long aprendizId = matricula.getAprendiz().getId();
        matriculaDao.deleteById(matriculaId);
        redirectAttributes.addFlashAttribute("mensaje", "Matricula eliminada.");
        return "redirect:/aprendices/" + aprendizId;
    }
}
