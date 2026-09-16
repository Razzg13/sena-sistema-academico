package com.sena.academico.matricula;

import com.sena.academico.aprendiz.Aprendiz;
import com.sena.academico.common.AuditableEntity;
import com.sena.academico.ficha.Ficha;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Vinculacion de un aprendiz a una ficha (RF-06). Debe evitarse mas de una
 * matricula ACTIVA para el mismo par aprendiz-ficha.
 */
@Entity
@Table(name = "matriculas")
public class Matricula extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprendiz_id", nullable = false)
    private Aprendiz aprendiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ficha_id", nullable = false)
    private Ficha ficha;

    @Column(name = "fecha_matricula", nullable = false)
    private LocalDate fechaMatricula;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoMatricula estado = EstadoMatricula.ACTIVA;

    protected Matricula() {
    }

    public Matricula(Aprendiz aprendiz, Ficha ficha, LocalDate fechaMatricula) {
        this.aprendiz = aprendiz;
        this.ficha = ficha;
        this.fechaMatricula = fechaMatricula;
    }

    public Aprendiz getAprendiz() {
        return aprendiz;
    }

    public Ficha getFicha() {
        return ficha;
    }

    public LocalDate getFechaMatricula() {
        return fechaMatricula;
    }

    public EstadoMatricula getEstado() {
        return estado;
    }

    public void cancelar() {
        this.estado = EstadoMatricula.CANCELADA;
    }
}
