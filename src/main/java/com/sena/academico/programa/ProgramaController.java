package com.sena.academico.programa;

import com.sena.academico.programa.NivelFormacion;
import com.sena.academico.programa.Programa;
import com.sena.academico.programa.NuevoProgramaForm;
import com.sena.academico.programa.ProgramaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProgramaController {

    private static final int TAMANO_PAGINA = 15;

    private final ProgramaService programaService;

    public ProgramaController(ProgramaService programaService) {
        this.programaService = programaService;
    }

    @GetMapping("/coordinacion/programas")
    public String listar(@RequestParam(required = false) String texto,
                          @RequestParam(defaultValue = "0") int pagina,
                          Model model) {
        Page<Programa> programas = programaService.buscar(texto,
                PageRequest.of(pagina, TAMANO_PAGINA, Sort.by("codigo")));
        model.addAttribute("programas", programas);
        model.addAttribute("texto", texto);
        return "programa/listado";
    }

    @GetMapping("/coordinacion/programas/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("form", new NuevoProgramaForm());
        model.addAttribute("niveles", NivelFormacion.values());
        return "programa/nuevo";
    }

    @PostMapping("/coordinacion/programas")
    public String crear(@Valid @ModelAttribute("form") NuevoProgramaForm form, BindingResult resultado,
                         Model model, RedirectAttributes redirectAttributes) {
        if (resultado.hasErrors()) {
            model.addAttribute("niveles", NivelFormacion.values());
            return "programa/nuevo";
        }
        Programa programa = programaService.crear(form);
        redirectAttributes.addFlashAttribute("mensaje", "Programa " + programa.getCodigo() + " creado correctamente.");
        return "redirect:/coordinacion/programas/" + programa.getId();
    }

    @GetMapping("/coordinacion/programas/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("programa", programaService.obtener(id));
        return "programa/detalle";
    }

    @PostMapping("/coordinacion/programas/{id}/competencias")
    public String agregarCompetencia(@PathVariable Long id,
                                      @RequestParam String codigo,
                                      @RequestParam String nombre,
                                      RedirectAttributes redirectAttributes) {
        programaService.agregarCompetencia(id, codigo, nombre);
        redirectAttributes.addFlashAttribute("mensaje", "Competencia agregada.");
        return "redirect:/coordinacion/programas/" + id;
    }

    @PostMapping("/coordinacion/competencias/{competenciaId}/resultados")
    public String agregarResultado(@PathVariable Long competenciaId,
                                    @RequestParam String programaId,
                                    @RequestParam String codigo,
                                    @RequestParam String descripcion,
                                    RedirectAttributes redirectAttributes) {
        programaService.agregarResultadoAprendizaje(competenciaId, codigo, descripcion);
        redirectAttributes.addFlashAttribute("mensaje", "Resultado de aprendizaje agregado.");
        return "redirect:/coordinacion/programas/" + programaId;
    }

    @PostMapping("/coordinacion/programas/{id}/desactivar")
    public String desactivar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        programaService.desactivar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Programa inactivado.");
        return "redirect:/coordinacion/programas/" + id;
    }

    @PostMapping("/coordinacion/programas/{id}/activar")
    public String activar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        programaService.activar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Programa reactivado.");
        return "redirect:/coordinacion/programas/" + id;
    }
}
