package com.sena.academico.ficha;

import com.sena.academico.ficha.EstadoFicha;
import com.sena.academico.ficha.Ficha;
import com.sena.academico.ficha.Jornada;
import com.sena.academico.ficha.FichaService;
import com.sena.academico.ficha.NuevaFichaForm;
import com.sena.academico.aprendiz.AprendizService;
import com.sena.academico.asistencia.AsistenciaService;
import com.sena.academico.evaluacion.EvaluacionService;
import com.sena.academico.common.NegocioException;
import com.sena.academico.instructor.InstructorService;
import com.sena.academico.matricula.MatriculaService;
import com.sena.academico.programa.ProgramaService;
import com.sena.academico.seguridad.RolNombre;
import com.sena.academico.seguridad.UsuarioPrincipal;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class FichaController {

    private static final int TAMANO_PAGINA = 15;

    private final FichaService fichaService;
    private final ProgramaService programaService;
    private final InstructorService instructorService;
    private final AprendizService aprendizService;
    private final MatriculaService matriculaService;
    private final AsistenciaService asistenciaService;
    private final EvaluacionService evaluacionService;

    public FichaController(FichaService fichaService, ProgramaService programaService,
                            InstructorService instructorService, AprendizService aprendizService,
                            MatriculaService matriculaService, AsistenciaService asistenciaService,
                            EvaluacionService evaluacionService) {
        this.fichaService = fichaService;
        this.programaService = programaService;
        this.instructorService = instructorService;
        this.aprendizService = aprendizService;
        this.matriculaService = matriculaService;
        this.asistenciaService = asistenciaService;
        this.evaluacionService = evaluacionService;
    }

    @GetMapping("/coordinacion/fichas")
    public String listar(@RequestParam(required = false) String texto,
                          @RequestParam(defaultValue = "0") int pagina,
                          @AuthenticationPrincipal UsuarioPrincipal principal,
                          Model model) {
        Page<Ficha> fichas;
        if (esSoloInstructor(principal)) {
            // Un instructor solo ve las fichas donde tiene alguna asignacion (sin busqueda/paginacion:
            // en la practica un instructor tiene pocas fichas a la vez).
            fichas = new PageImpl<>(fichaService.listarPorInstructor(principal.getUsuario().getId()));
        } else {
            fichas = fichaService.buscar(texto, PageRequest.of(pagina, TAMANO_PAGINA, Sort.by("numero")));
        }
        model.addAttribute("fichas", fichas);
        model.addAttribute("texto", texto);
        return "ficha/listado";
    }

    @GetMapping("/coordinacion/fichas/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("form", new NuevaFichaForm());
        model.addAttribute("programas", programaService.buscar(null, Pageable.unpaged()).getContent());
        model.addAttribute("jornadas", Jornada.values());
        return "ficha/nuevo";
    }

    @PostMapping("/coordinacion/fichas")
    public String crear(@Valid @ModelAttribute("form") NuevaFichaForm form, BindingResult resultado,
                         Model model, RedirectAttributes redirectAttributes) {
        if (resultado.hasErrors()) {
            model.addAttribute("programas", programaService.buscar(null, Pageable.unpaged()).getContent());
            model.addAttribute("jornadas", Jornada.values());
            return "ficha/nuevo";
        }
        Ficha ficha = fichaService.crear(form);
        redirectAttributes.addFlashAttribute("mensaje", "Ficha " + ficha.getNumero() + " creada correctamente.");
        return "redirect:/coordinacion/fichas/" + ficha.getId();
    }

    @GetMapping("/coordinacion/fichas/{id}")
    public String detalle(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        if (esSoloInstructor(principal) && !fichaService.estaAsignado(id, principal.getUsuario().getId())) {
            throw new NegocioException("No estás asignado a esta ficha.");
        }
        model.addAttribute("ficha", fichaService.obtener(id));
        model.addAttribute("instructores", instructorService.listarActivos());
        model.addAttribute("estados", EstadoFicha.values());
        model.addAttribute("aprendices", aprendizService.listarActivos());
        model.addAttribute("matriculas", matriculaService.listarPorFicha(id));
        model.addAttribute("sesiones", asistenciaService.listarSesionesPorFicha(id));
        model.addAttribute("actividades", evaluacionService.listarActividadesPorFicha(id));
        return "ficha/detalle";
    }

    @PostMapping("/coordinacion/fichas/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, @RequestParam EstadoFicha estado,
                                 RedirectAttributes redirectAttributes) {
        fichaService.cambiarEstado(id, estado);
        redirectAttributes.addFlashAttribute("mensaje", "Estado de la ficha actualizado a " + estado + ".");
        return "redirect:/coordinacion/fichas/" + id;
    }

    @PostMapping("/coordinacion/fichas/{id}/instructores")
    public String asignarInstructor(@PathVariable Long id,
                                     @RequestParam Long instructorId,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                                     RedirectAttributes redirectAttributes) {
        fichaService.asignarInstructor(id, instructorId, desde, hasta);
        redirectAttributes.addFlashAttribute("mensaje", "Instructor asignado a la ficha.");
        return "redirect:/coordinacion/fichas/" + id;
    }

    /**
     * true si el usuario autenticado SOLO tiene el rol INSTRUCTOR (ni ADMINISTRADOR ni
     * COORDINACION_ACADEMICA), para aplicar el filtrado de autogestion.
     */
    private boolean esSoloInstructor(UsuarioPrincipal principal) {
        return principal.getRoles().contains(RolNombre.INSTRUCTOR)
                && !principal.getRoles().contains(RolNombre.ADMINISTRADOR)
                && !principal.getRoles().contains(RolNombre.COORDINACION_ACADEMICA);
    }
}
