package com.sena.cipa.controlador;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sena.cipa.dao.AprendizDao;
import com.sena.cipa.dao.AsignacionInstructorDao;
import com.sena.cipa.dao.FichaDao;
import com.sena.cipa.dao.InstructorDao;
import com.sena.cipa.dao.MatriculaDao;
import com.sena.cipa.dao.ProgramaDao;
import com.sena.cipa.modelo.Ficha;

@Controller
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final ProgramaDao programaDao;
    private final FichaDao fichaDao;
    private final AprendizDao aprendizDao;
    private final InstructorDao instructorDao;
    private final MatriculaDao matriculaDao;
    private final AsignacionInstructorDao asignacionDao;

    public AuditoriaController(ProgramaDao programaDao, FichaDao fichaDao,
                                AprendizDao aprendizDao, InstructorDao instructorDao,
                                MatriculaDao matriculaDao, AsignacionInstructorDao asignacionDao) {
        this.programaDao = programaDao;
        this.fichaDao = fichaDao;
        this.aprendizDao = aprendizDao;
        this.instructorDao = instructorDao;
        this.matriculaDao = matriculaDao;
        this.asignacionDao = asignacionDao;
    }

    public record ResumenFicha(Ficha ficha, int matriculados, String instructores) {
    }

    @GetMapping
    public String panel(Model model) {
        model.addAttribute("totalProgramas", programaDao.count());
        model.addAttribute("totalFichas", fichaDao.count());
        model.addAttribute("totalAprendices", aprendizDao.count());
        model.addAttribute("totalInstructores", instructorDao.count());

        List<ResumenFicha> resumenes = fichaDao.findAll().stream()
                .map(ficha -> {
                    int matriculados = matriculaDao.findByFichaId(ficha.getId()).size();
                    String instructores = asignacionDao.findByFichaId(ficha.getId()).stream()
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
