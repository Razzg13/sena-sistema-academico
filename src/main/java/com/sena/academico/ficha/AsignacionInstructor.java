package com.sena.academico.ficha;

import com.sena.academico.instructor.Instructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Vinculacion de un instructor a una ficha con un rango de fechas (RF-07).
 */
@Entity
@Table(name = "asignaciones_instructor")
public class AsignacionInstructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ficha_id", nullable = false)
    private Ficha ficha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    protected AsignacionInstructor() {
    }

    public AsignacionInstructor(Instructor instructor, LocalDate fechaInicio, LocalDate fechaFin) {
        this.instructor = instructor;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public Long getId() {
        return id;
    }

    public Ficha getFicha() {
        return ficha;
    }

    void asignarFicha(Ficha ficha) {
        this.ficha = ficha;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }
}
