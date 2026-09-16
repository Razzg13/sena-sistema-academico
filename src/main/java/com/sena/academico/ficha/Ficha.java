package com.sena.academico.ficha;

import com.sena.academico.common.AuditableEntity;
import com.sena.academico.programa.Programa;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fichas", uniqueConstraints = @UniqueConstraint(columnNames = "numero"))
public class Ficha extends AuditableEntity {

    @NotBlank
    @Column(nullable = false, length = 20)
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programa_id", nullable = false)
    private Programa programa;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Jornada jornada;

    @NotNull
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @NotNull
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String ambiente;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoFicha estado = EstadoFicha.EN_EJECUCION;

    @OneToMany(mappedBy = "ficha", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("fechaInicio asc")
    private List<AsignacionInstructor> asignaciones = new ArrayList<>();

    protected Ficha() {
    }

    public Ficha(String numero, Programa programa, Jornada jornada, LocalDate fechaInicio, LocalDate fechaFin,
                 String ambiente) {
        this.numero = numero;
        this.programa = programa;
        this.jornada = jornada;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.ambiente = ambiente;
    }

    public String getNumero() {
        return numero;
    }

    public Programa getPrograma() {
        return programa;
    }

    public Jornada getJornada() {
        return jornada;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public EstadoFicha getEstado() {
        return estado;
    }

    public void cambiarEstado(EstadoFicha nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public List<AsignacionInstructor> getAsignaciones() {
        return asignaciones;
    }

    public void agregarAsignacion(AsignacionInstructor asignacion) {
        asignaciones.add(asignacion);
        asignacion.asignarFicha(this);
    }
}
