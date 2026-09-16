package com.sena.academico.asistencia;

import com.sena.academico.common.AuditableEntity;
import com.sena.academico.ficha.Ficha;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Sesion de formacion programada para una ficha (RF-08). El llamado a lista
 * (RF-09) se registra por separado en {@link Asistencia}, una por matricula y sesion.
 */
@Entity
@Table(name = "sesiones_formacion")
public class SesionFormacion extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ficha_id", nullable = false)
    private Ficha ficha;

    @NotNull
    @Column(nullable = false)
    private LocalDate fecha;

    @NotNull
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @NotNull
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String tema;

    protected SesionFormacion() {
    }

    public SesionFormacion(Ficha ficha, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, String tema) {
        this.ficha = ficha;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.tema = tema;
    }

    public Ficha getFicha() {
        return ficha;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public String getTema() {
        return tema;
    }
}
