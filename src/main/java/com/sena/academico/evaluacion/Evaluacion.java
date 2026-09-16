package com.sena.academico.evaluacion;

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

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Calificacion, juicio y retroalimentacion de un aprendiz (via su matricula) en una
 * actividad evaluativa (RF-11). A lo sumo un registro por par actividad-matricula.
 */
@Entity
@Table(name = "evaluaciones", uniqueConstraints = @UniqueConstraint(columnNames = {"actividad_id", "matricula_id"}))
public class Evaluacion extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actividad_id", nullable = false)
    private ActividadEvaluativa actividad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @Column(precision = 4, scale = 2)
    private BigDecimal calificacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "juicio_evaluacion", nullable = false, length = 20)
    private JuicioEvaluacion juicioEvaluacion;

    @Column(length = 500)
    private String retroalimentacion;

    @Column(name = "fecha_evaluacion", nullable = false)
    private LocalDate fechaEvaluacion;

    protected Evaluacion() {
    }

    public Evaluacion(ActividadEvaluativa actividad, Matricula matricula, BigDecimal calificacion,
                       JuicioEvaluacion juicioEvaluacion, String retroalimentacion, LocalDate fechaEvaluacion) {
        this.actividad = actividad;
        this.matricula = matricula;
        this.calificacion = calificacion;
        this.juicioEvaluacion = juicioEvaluacion;
        this.retroalimentacion = retroalimentacion;
        this.fechaEvaluacion = fechaEvaluacion;
    }

    public ActividadEvaluativa getActividad() {
        return actividad;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public BigDecimal getCalificacion() {
        return calificacion;
    }

    public JuicioEvaluacion getJuicioEvaluacion() {
        return juicioEvaluacion;
    }

    public String getRetroalimentacion() {
        return retroalimentacion;
    }

    public LocalDate getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public void actualizar(BigDecimal calificacion, JuicioEvaluacion juicioEvaluacion, String retroalimentacion,
                            LocalDate fechaEvaluacion) {
        this.calificacion = calificacion;
        this.juicioEvaluacion = juicioEvaluacion;
        this.retroalimentacion = retroalimentacion;
        this.fechaEvaluacion = fechaEvaluacion;
    }
}
