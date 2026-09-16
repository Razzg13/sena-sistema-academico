package com.sena.academico.auditoria;

import com.sena.academico.auditoria.Accion;
import com.sena.academico.auditoria.RegistroAuditoria;
import com.sena.academico.auditoria.AuditoriaService;
import com.sena.academico.auditoria.FiltroAuditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Controller
public class AuditoriaController {

    private static final int TAMANO_PAGINA = 20;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping("/auditoria")
    public String listar(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String entidad,
            @RequestParam(required = false) Accion accion,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {

        LocalDateTime desdeFecha = desde != null ? desde.atStartOfDay() : null;
        LocalDateTime hastaFecha = hasta != null ? hasta.atTime(LocalTime.MAX) : null;

        FiltroAuditoria filtro = new FiltroAuditoria(usuario, entidad, accion, desdeFecha, hastaFecha);
        Page<RegistroAuditoria> resultado = auditoriaService.buscar(
                filtro, PageRequest.of(pagina, TAMANO_PAGINA, Sort.by(Sort.Direction.DESC, "fecha")));

        model.addAttribute("registros", resultado);
        model.addAttribute("acciones", Accion.values());
        model.addAttribute("formatoFecha", FORMATO_FECHA);
        model.addAttribute("usuario", usuario);
        model.addAttribute("entidad", entidad);
        model.addAttribute("accion", accion);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        return "auditoria/listado";
    }
}
