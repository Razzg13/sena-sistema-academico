package com.sena.cipa.aprendiz;

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
import com.sena.cipa.matricula.Matricula;
import com.sena.cipa.matricula.MatriculaRepository;
import com.sena.cipa.seguridad.RolNombre;
import com.sena.cipa.seguridad.UsuarioRepository;

@Controller
@RequestMapping("/aprendices")
public class AprendizController {

    private final AprendizRepository aprendizRepository;
    private final UsuarioRepository usuarioRepository;
    private final MatriculaRepository matriculaRepository;
    private final FichaRepository fichaRepository;

    public AprendizController(AprendizRepository aprendizRepository, UsuarioRepository usuarioRepository,
                               MatriculaRepository matriculaRepository, FichaRepository fichaRepository) {
        this.aprendizRepository = aprendizRepository;
        this.usuarioRepository = usuarioRepository;
        this.matriculaRepository = matriculaRepository;
        this.fichaRepository = fichaRepository;
    }

    @GetMapping
    public String listado(Model model) {
        model.addAttribute("aprendices", aprendizRepository.findAll());
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
        model.addAttribute("usuarios", usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() == RolNombre.APRENDIZ)
                .filter(u -> !aprendizRepository.existsByUsuarioId(u.getId()))
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
        aprendizRepository.save(aprendiz);
        redirectAttributes.addFlashAttribute("mensaje", "Aprendiz guardado correctamente.");
        return "redirect:/aprendices";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("aprendiz", aprendizRepository.findById(id).orElseThrow());
        model.addAttribute("matriculas", matriculaRepository.findByAprendizId(id));
        model.addAttribute("fichas", fichaRepository.findAll());
        model.addAttribute("nuevaMatricula", new Matricula());
        return "aprendiz/detalle";
    }

    @PostMapping("/{id}/matriculas")
    public String matricular(@PathVariable Long id, @ModelAttribute Matricula nuevaMatricula,
                              RedirectAttributes redirectAttributes) {
        Aprendiz aprendiz = aprendizRepository.findById(id).orElseThrow();
        nuevaMatricula.setAprendiz(aprendiz);
        matriculaRepository.save(nuevaMatricula);
        redirectAttributes.addFlashAttribute("mensaje", "Matricula registrada.");
        return "redirect:/aprendices/" + id;
    }

    @PostMapping("/matriculas/{matriculaId}/eliminar")
    public String eliminarMatricula(@PathVariable Long matriculaId, RedirectAttributes redirectAttributes) {
        Matricula matricula = matriculaRepository.findById(matriculaId).orElseThrow();
        Long aprendizId = matricula.getAprendiz().getId();
        matriculaRepository.deleteById(matriculaId);
        redirectAttributes.addFlashAttribute("mensaje", "Matricula eliminada.");
        return "redirect:/aprendices/" + aprendizId;
    }
}
