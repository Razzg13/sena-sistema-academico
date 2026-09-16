package com.sena.academico.auditoria;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Bitacora de operaciones criticas: altas, cambios, inactivaciones y evaluaciones (RF-15).
 * Es un registro inmutable, no extiende AuditableEntity porque no se actualiza ni se borra.
 */
@Entity
@Table(name = "registros_auditoria")
public class RegistroAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, length = 150)
    private String usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Accion accion;

    @Column(nullable = false, length = 100)
    private String entidad;

    @Column(name = "entidad_id")
    private Long entidadId;

    @Column(name = "ip_local", length = 45)
    private String ipLocal;

    @Column(length = 500)
    private String detalle;

    protected RegistroAuditoria() {
    }

    public RegistroAuditoria(LocalDateTime fecha, String usuario, Accion accion, String entidad,
                              Long entidadId, String ipLocal, String detalle) {
        this.fecha = fecha;
        this.usuario = usuario;
        this.accion = accion;
        this.entidad = entidad;
        this.entidadId = entidadId;
        this.ipLocal = ipLocal;
        this.detalle = detalle;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public Accion getAccion() {
        return accion;
    }

    public String getEntidad() {
        return entidad;
    }

    public Long getEntidadId() {
        return entidadId;
    }

    public String getIpLocal() {
        return ipLocal;
    }

    public String getDetalle() {
        return detalle;
    }
}
