package com.sena.academico.asistencia;

import com.sena.academico.common.AuditableEntity;
import com.sena.academico.matricula.Matricula;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Registro de asistencia de un aprendiz matriculado a una sesion de formacion (RF-09).
 * Debe existir a lo sumo un registro por par sesion-matricula.
 */
@Entity
@Table(name = "asistencias", uniqueConstraints = @UniqueConstraint(columnNames = {"sesion_id", "matricula_id"}))
public class Asistencia extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id", nullable = false)
    private SesionFormacion sesion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoAsistencia estado;

    @Column(length = 300)
    private String observacion;

    @Column(length = 300)
    private String justificacion;

    protected Asistencia() {
    }

    public Asistencia(SesionFormacion sesion, Matricula matricula, EstadoAsistencia estado,
                       String observacion, String justificacion) {
        this.sesion = sesion;
        this.matricula = matricula;
        this.estado = estado;
        this.observacion = observacion;
        this.justificacion = justificacion;
    }

    public SesionFormacion getSesion() {
        return sesion;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public EstadoAsistencia getEstado() {
        return estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public String getJustificacion() {
        return justificacion;
    }

    public void actualizar(EstadoAsistencia estado, String observacion, String justificacion) {
        this.estado = estado;
        this.observacion = observacion;
        this.justificacion = justificacion;
    }
}
