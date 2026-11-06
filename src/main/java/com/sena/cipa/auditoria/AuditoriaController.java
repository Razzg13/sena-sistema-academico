package com.sena.cipa.auditoria;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sena.cipa.aprendiz.AprendizRepository;
import com.sena.cipa.ficha.Ficha;
import com.sena.cipa.ficha.FichaRepository;
import com.sena.cipa.instructor.AsignacionInstructorRepository;
import com.sena.cipa.instructor.InstructorRepository;
import com.sena.cipa.matricula.MatriculaRepository;
import com.sena.cipa.programa.ProgramaRepository;

@Controller
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final ProgramaRepository programaRepository;
    private final FichaRepository fichaRepository;
    private final AprendizRepository aprendizRepository;
    private final InstructorRepository instructorRepository;
    private final MatriculaRepository matriculaRepository;
    private final AsignacionInstructorRepository asignacionRepository;

    public AuditoriaController(ProgramaRepository programaRepository, FichaRepository fichaRepository,
                                AprendizRepository aprendizRepository, InstructorRepository instructorRepository,
                                MatriculaRepository matriculaRepository, AsignacionInstructorRepository asignacionRepository) {
        this.programaRepository = programaRepository;
        this.fichaRepository = fichaRepository;
        this.aprendizRepository = aprendizRepository;
        this.instructorRepository = instructorRepository;
        this.matriculaRepository = matriculaRepository;
        this.asignacionRepository = asignacionRepository;
    }

    public record ResumenFicha(Ficha ficha, int matriculados, String instructores) {
    }

    @GetMapping
    public String panel(Model model) {
        model.addAttribute("totalProgramas", programaRepository.count());
        model.addAttribute("totalFichas", fichaRepository.count());
        model.addAttribute("totalAprendices", aprendizRepository.count());
        model.addAttribute("totalInstructores", instructorRepository.count());

        List<ResumenFicha> resumenes = fichaRepository.findAll().stream()
                .map(ficha -> {
                    int matriculados = matriculaRepository.findByFichaId(ficha.getId()).size();
                    String instructores = asignacionRepository.findByFichaId(ficha.getId()).stream()
                            .map(a -> a.getInstructor().getUsuario().getNombreCompleto())
                            .distinct()
                            .collect(Collectors.joining(", "));
                    return new ResumenFicha(ficha, matriculados,
                            instructores.isBlank() ? "Sin instructor asignado" : instructores);
                })
                .toList();
        model.addAttribute("fichas", resumenes);

        return "auditoria/panel";
    }
}
