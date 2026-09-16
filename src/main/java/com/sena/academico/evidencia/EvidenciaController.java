package com.sena.academico.evidencia;

import com.sena.academico.archivo.ArchivoAlmacenamientoService;
import com.sena.academico.evidencia.EstadoEvidencia;
import com.sena.academico.evidencia.Evidencia;
import com.sena.academico.evidencia.EvidenciaService;
import com.sena.academico.seguridad.UsuarioPrincipal;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EvidenciaController {

    private final EvidenciaService evidenciaService;
    private final ArchivoAlmacenamientoService archivoAlmacenamientoService;

    public EvidenciaController(EvidenciaService evidenciaService,
                                ArchivoAlmacenamientoService archivoAlmacenamientoService) {
        this.evidenciaService = evidenciaService;
        this.archivoAlmacenamientoService = archivoAlmacenamientoService;
    }

    @PostMapping("/coordinacion/actividades/{actividadId}/evidencias")
    public String entregar(@PathVariable Long actividadId,
                            @RequestParam Long matriculaId,
                            @RequestParam("archivo") MultipartFile archivo,
                            RedirectAttributes redirectAttributes) {
        evidenciaService.entregar(actividadId, matriculaId, archivo);
        redirectAttributes.addFlashAttribute("mensaje", "Evidencia recibida.");
        return "redirect:/coordinacion/actividades/" + actividadId + "/calificar";
    }

    @PostMapping("/coordinacion/evidencias/{id}/estado")
    public String cambiarEstado(@PathVariable Long id,
                                 @RequestParam EstadoEvidencia estado,
                                 @RequestParam(required = false) String observacion,
                                 RedirectAttributes redirectAttributes) {
        Long actividadId = evidenciaService.obtener(id).getActividad().getId();
        evidenciaService.cambiarEstado(id, estado, observacion);
        redirectAttributes.addFlashAttribute("mensaje", "Estado de la evidencia actualizado.");
        return "redirect:/coordinacion/actividades/" + actividadId + "/calificar";
    }

    @GetMapping("/coordinacion/evidencias/{id}/descargar")
    public ResponseEntity<Resource> descargar(@PathVariable Long id,
                                               @AuthenticationPrincipal UsuarioPrincipal principal) {
        Evidencia evidencia = evidenciaService.obtener(id);
        if (!evidenciaService.puedeDescargar(evidencia, principal)) {
            throw new AccessDeniedException("No estás autorizado para descargar este archivo.");
        }
        Resource recurso = archivoAlmacenamientoService.cargarComoRecurso(evidencia.getArchivo());
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(evidencia.getArchivo().getTipoContenido()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + evidencia.getArchivo().getNombreOriginal() + "\"")
                .body(recurso);
    }
}
