package com.sena.cipa.entrega;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

import com.sena.cipa.seguridad.AccesoFichaService;
import com.sena.cipa.seguridad.RolNombre;
import com.sena.cipa.seguridad.UsuarioPrincipal;

@Controller
@RequestMapping("/entregas")
public class EntregaController {

    private final EntregaRepository entregaRepository;
    private final ArchivoEntregaRepository archivoRepository;
    private final AccesoFichaService acceso;

    public EntregaController(EntregaRepository entregaRepository, ArchivoEntregaRepository archivoRepository,
                              AccesoFichaService acceso) {
        this.entregaRepository = entregaRepository;
        this.archivoRepository = archivoRepository;
        this.acceso = acceso;
    }

    @GetMapping("/{id}/archivo")
    public ResponseEntity<byte[]> descargar(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal principal) {
        Entrega entrega = entregaRepository.findById(id).orElseThrow();
        RolNombre rol = principal.getUsuario().getRol();
        switch (rol) {
            case INSTRUCTOR -> acceso.exigirFicha(principal, entrega.getActividad().getFicha().getId());
            case APRENDIZ -> {
                Long duenio = entrega.getMatricula().getAprendiz().getUsuario().getId();
                if (!duenio.equals(principal.getUsuario().getId())) {
                    throw new AccessDeniedException("La entrega pertenece a otro aprendiz");
                }
            }
            default -> {
            }
        }
        if (!entrega.isTieneArchivo()) {
            throw new NoSuchElementException("La entrega no tiene archivo");
        }
        ArchivoEntrega archivo = archivoRepository.findById(id).orElseThrow();

        MediaType tipo;
        try {
            tipo = MediaType.parseMediaType(entrega.getTipoContenido());
        } catch (RuntimeException e) {
            tipo = MediaType.APPLICATION_OCTET_STREAM;
        }
        return ResponseEntity.ok()
                .contentType(tipo)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(entrega.getNombreArchivo(), StandardCharsets.UTF_8).build().toString())
                .header("X-Content-Type-Options", "nosniff")
                .contentLength(archivo.getContenido().length)
                .body(archivo.getContenido());
    }
}
