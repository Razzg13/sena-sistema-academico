package com.sena.academico.evaluacion;

import com.sena.academico.common.AuditableEntity;
import com.sena.academico.ficha.Ficha;
import com.sena.academico.programa.ResultadoAprendizaje;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Actividad evaluativa de una ficha, asociada a un resultado de aprendizaje (RF-10).
 */
@Entity
@Table(name = "actividades_evaluativas")
public class ActividadEvaluativa extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ficha_id", nullable = false)
    private Ficha ficha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resultado_aprendizaje_id", nullable = false)
    private ResultadoAprendizaje resultadoAprendizaje;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @NotNull
    @Column(name = "fecha_limite", nullable = false)
    private LocalDate fechaLimite;

    protected ActividadEvaluativa() {
    }

    public ActividadEvaluativa(Ficha ficha, ResultadoAprendizaje resultadoAprendizaje, String nombre,
                                String descripcion, LocalDate fechaLimite) {
        this.ficha = ficha;
        this.resultadoAprendizaje = resultadoAprendizaje;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
    }

    public Ficha getFicha() {
        return ficha;
    }

    public ResultadoAprendizaje getResultadoAprendizaje() {
        return resultadoAprendizaje;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }
}
