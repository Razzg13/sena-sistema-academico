package com.sena.cipa.aprendiz;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sena.cipa.common.Fechas;
import com.sena.cipa.common.NegocioException;
import com.sena.cipa.entrega.ArchivoEntrega;
import com.sena.cipa.entrega.ArchivoEntregaRepository;
import com.sena.cipa.entrega.Entrega;
import com.sena.cipa.entrega.EntregaRepository;
import com.sena.cipa.evaluacion.ActividadEvaluativa;
import com.sena.cipa.evaluacion.ActividadEvaluativaRepository;
import com.sena.cipa.evaluacion.Evaluacion;
import com.sena.cipa.evaluacion.EvaluacionRepository;
import com.sena.cipa.matricula.EstadoMatricula;
import com.sena.cipa.matricula.Matricula;
import com.sena.cipa.matricula.MatriculaRepository;
import com.sena.cipa.seguridad.UsuarioPrincipal;

@Controller
@RequestMapping("/mis-actividades")
public class ActividadesAprendizController {

    private static final Set<String> EXTENSIONES = Set.of("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "txt", "zip", "png", "jpg", "jpeg");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final AprendizRepository aprendizRepository;
    private final MatriculaRepository matriculaRepository;
    private final ActividadEvaluativaRepository actividadRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final EntregaRepository entregaRepository;
    private final ArchivoEntregaRepository archivoRepository;

    public ActividadesAprendizController(AprendizRepository aprendizRepository, MatriculaRepository matriculaRepository,
                                          ActividadEvaluativaRepository actividadRepository,
                                          EvaluacionRepository evaluacionRepository, EntregaRepository entregaRepository,
                                          ArchivoEntregaRepository archivoRepository) {
        this.aprendizRepository = aprendizRepository;
        this.matriculaRepository = matriculaRepository;
        this.actividadRepository = actividadRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.entregaRepository = entregaRepository;
        this.archivoRepository = archivoRepository;
    }

    public record FilaActividad(ActividadEvaluativa actividad, Matricula matricula, Entrega entrega,
                                 Evaluacion evaluacion, EstadoActividad estado, String fechaEntrega) {
    }

    @GetMapping
    public String listado(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        Optional<Aprendiz> aprendiz = aprendizRepository.findByUsuarioId(principal.getUsuario().getId());
        if (aprendiz.isEmpty()) {
            model.addAttribute("sinPerfil", true);
            return "aprendiz/mis-actividades";
        }
        List<FilaActividad> filas = filasDe(aprendiz.get());
        model.addAttribute("filas", filas);
        model.addAttribute("pendientes", filas.stream().filter(f -> f.estado() == EstadoActividad.PENDIENTE).count());
        return "aprendiz/mis-actividades";
    }

    @GetMapping("/{actividadId}")
    public String detalle(@PathVariable Long actividadId, @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        FilaActividad fila = filaPropia(actividadId, principal);
        model.addAttribute("fila", fila);
        model.addAttribute("puedeEntregar", fila.estado() != EstadoActividad.CALIFICADA);
        return "aprendiz/actividad-detalle";
    }

    @PostMapping("/{actividadId}/entregar")
    @Transactional
    public String entregar(@PathVariable Long actividadId, @RequestParam(required = false) String comentario,
                            @RequestParam(required = false) MultipartFile archivo,
                            @AuthenticationPrincipal UsuarioPrincipal principal, RedirectAttributes redirectAttributes) {
        FilaActividad fila = filaPropia(actividadId, principal);
        String destino = "/mis-actividades/" + actividadId;

        if (fila.estado() == EstadoActividad.CALIFICADA) {
            throw new NegocioException("Esta actividad ya fue calificada y no admite nuevas entregas.", destino);
        }
        String texto = comentario == null ? "" : comentario.trim();
        boolean hayArchivo = archivo != null && !archivo.isEmpty();
        if (texto.isEmpty() && !hayArchivo && fila.entrega() == null) {
            throw new NegocioException("Escribe un comentario o adjunta un archivo para entregar la actividad.", destino);
        }
        if (texto.length() > 1000) {
            throw new NegocioException("El comentario no puede superar los 1000 caracteres.", destino);
        }

        String nombreArchivo = null;
        if (hayArchivo) {
            nombreArchivo = nombreSeguro(archivo.getOriginalFilename());
            String extension = nombreArchivo.contains(".")
                    ? nombreArchivo.substring(nombreArchivo.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT) : "";
            if (!EXTENSIONES.contains(extension)) {
                throw new NegocioException("Tipo de archivo no permitido. Usa: " + String.join(", ", new java.util.TreeSet<>(EXTENSIONES)) + ".", destino);
            }
        }

        Entrega entrega = fila.entrega() != null ? fila.entrega() : new Entrega();
        entrega.setActividad(fila.actividad());
        entrega.setMatricula(fila.matricula());
        entrega.setComentario(texto.isEmpty() ? null : texto);
        entrega.setFechaEntrega(Fechas.ahora());

        byte[] contenido = null;
        if (hayArchivo) {
            try {
                contenido = archivo.getBytes();
            } catch (IOException e) {
                throw new NegocioException("No se pudo leer el archivo. Intenta de nuevo.", destino);
            }
            entrega.setNombreArchivo(nombreArchivo);
            String tipo = archivo.getContentType();
            entrega.setTipoContenido(tipo == null ? "application/octet-stream" : tipo.substring(0, Math.min(tipo.length(), 150)));
            entrega.setTamanoBytes((long) contenido.length);
        }
        entrega = entregaRepository.save(entrega);

        if (contenido != null) {
            ArchivoEntrega almacenado = archivoRepository.findById(entrega.getId()).orElse(null);
            if (almacenado == null) {
                almacenado = new ArchivoEntrega(entrega, contenido);
            } else {
                almacenado.setContenido(contenido);
            }
            archivoRepository.save(almacenado);
        }

        redirectAttributes.addFlashAttribute("mensaje", fila.entrega() == null
                ? "Actividad entregada. Tu instructor la vera para calificarla."
                : "Entrega actualizada.");
        return "redirect:" + destino;
    }

    private FilaActividad filaPropia(Long actividadId, UsuarioPrincipal principal) {
        Aprendiz aprendiz = aprendizRepository.findByUsuarioId(principal.getUsuario().getId())
                .orElseThrow(() -> new AccessDeniedException("La cuenta no tiene perfil de aprendiz"));
        ActividadEvaluativa actividad = actividadRepository.findById(actividadId).orElseThrow();
        Matricula matricula = matriculaRepository.findByAprendizId(aprendiz.getId()).stream()
                .filter(m -> m.getEstado() == EstadoMatricula.ACTIVA)
                .filter(m -> m.getFicha().getId().equals(actividad.getFicha().getId()))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("La actividad no pertenece a una ficha activa del aprendiz"));
        Entrega entrega = entregaRepository.findByActividadIdAndMatriculaId(actividadId, matricula.getId()).orElse(null);
        Evaluacion evaluacion = evaluacionRepository.findByActividadIdAndMatriculaId(actividadId, matricula.getId()).orElse(null);
        return fila(actividad, matricula, entrega, evaluacion, Fechas.hoy());
    }

    private List<FilaActividad> filasDe(Aprendiz aprendiz) {
        List<Matricula> activas = matriculaRepository.findByAprendizId(aprendiz.getId()).stream()
                .filter(m -> m.getEstado() == EstadoMatricula.ACTIVA).toList();
        if (activas.isEmpty()) {
            return List.of();
        }
        List<Long> matriculaIds = activas.stream().map(Matricula::getId).toList();
        Map<String, Entrega> entregas = entregaRepository.findByMatriculaIdIn(matriculaIds).stream()
                .collect(Collectors.toMap(e -> e.getActividad().getId() + ":" + e.getMatricula().getId(), Function.identity()));
        Map<String, Evaluacion> evaluaciones = evaluacionRepository.findByMatriculaIdIn(matriculaIds).stream()
                .collect(Collectors.toMap(e -> e.getActividad().getId() + ":" + e.getMatricula().getId(), Function.identity()));

        LocalDate hoy = Fechas.hoy();
        List<FilaActividad> filas = new ArrayList<>();
        for (Matricula matricula : activas) {
            for (ActividadEvaluativa actividad : actividadRepository.findByFichaId(matricula.getFicha().getId())) {
                String clave = actividad.getId() + ":" + matricula.getId();
                filas.add(fila(actividad, matricula, entregas.get(clave), evaluaciones.get(clave), hoy));
            }
        }
        filas.sort(Comparator.comparing((FilaActividad f) -> prioridad(f.estado()))
                .thenComparing(f -> f.actividad().getFechaLimite()));
        return filas;
    }

    private FilaActividad fila(ActividadEvaluativa actividad, Matricula matricula, Entrega entrega,
                                Evaluacion evaluacion, LocalDate hoy) {
        EstadoActividad estado = EstadoActividad.de(actividad, entrega, evaluacion, hoy);
        String fechaEntrega = entrega == null ? null : FORMATO_FECHA.format(entrega.getFechaEntrega());
        return new FilaActividad(actividad, matricula, entrega, evaluacion, estado, fechaEntrega);
    }

    private int prioridad(EstadoActividad estado) {
        return switch (estado) {
            case PENDIENTE -> 0;
            case VENCIDA -> 1;
            case ENTREGADA -> 2;
            case CALIFICADA -> 3;
        };
    }

    private String nombreSeguro(String original) {
        String nombre = original == null ? "archivo" : original;
        nombre = nombre.substring(Math.max(nombre.lastIndexOf('/'), nombre.lastIndexOf('\\')) + 1);
        nombre = nombre.replaceAll("[\\p{Cntrl}\"]", "").trim();
        if (nombre.isEmpty()) {
            nombre = "archivo";
        }
        return nombre.length() > 200 ? nombre.substring(nombre.length() - 200) : nombre;
    }
}
