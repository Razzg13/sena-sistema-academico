package com.sena.academico.evidencia;

import com.sena.academico.archivo.Archivo;
import com.sena.academico.common.AuditableEntity;
import com.sena.academico.evaluacion.ActividadEvaluativa;
import com.sena.academico.matricula.Matricula;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

/**
 * Entrega de un aprendiz para una actividad evaluativa (RF-12): vincula la
 * actividad, la matricula del aprendiz y el archivo entregado.
 */
@Entity
@Table(name = "evidencias", uniqueConstraints = @UniqueConstraint(columnNames = {"actividad_id", "matricula_id"}))
public class Evidencia extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actividad_id", nullable = false)
    private ActividadEvaluativa actividad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archivo_id", nullable = false)
    private Archivo archivo;

    @Column(name = "fecha_entrega", nullable = false)
    private LocalDate fechaEntrega;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEvidencia estado = EstadoEvidencia.ENTREGADA;

    @Column(length = 300)
    private String observacion;

    protected Evidencia() {
    }

    public Evidencia(ActividadEvaluativa actividad, Matricula matricula, Archivo archivo, LocalDate fechaEntrega) {
        this.actividad = actividad;
        this.matricula = matricula;
        this.archivo = archivo;
        this.fechaEntrega = fechaEntrega;
    }

    public ActividadEvaluativa getActividad() {
        return actividad;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public Archivo getArchivo() {
        return archivo;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public EstadoEvidencia getEstado() {
        return estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void cambiarEstado(EstadoEvidencia nuevoEstado, String observacion) {
        this.estado = nuevoEstado;
        this.observacion = observacion;
    }
}
