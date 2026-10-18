package com.sena.cipa.ficha;

import java.time.LocalDate;

import com.sena.cipa.programa.Programa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "fichas")
public class Ficha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String numero;

    @ManyToOne
    @JoinColumn(name = "programa_id", nullable = false)
    private Programa programa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Jornada jornada;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false, length = 100)
    private String ambiente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoFicha estado = EstadoFicha.EN_EJECUCION;

    public Long getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public Jornada getJornada() {
        return jornada;
    }

    public void setJornada(Jornada jornada) {
        this.jornada = jornada;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    public EstadoFicha getEstado() {
        return estado;
    }

    public void setEstado(EstadoFicha estado) {
        this.estado = estado;
    }
}
