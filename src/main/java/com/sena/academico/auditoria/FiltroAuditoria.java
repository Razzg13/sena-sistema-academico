package com.sena.academico.auditoria;

import com.sena.academico.auditoria.Accion;

import java.time.LocalDateTime;

public record FiltroAuditoria(String usuario, String entidad, Accion accion,
                               LocalDateTime desde, LocalDateTime hasta) {
}
