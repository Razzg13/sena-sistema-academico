package com.sena.academico.programa;

import com.sena.academico.common.AuditableEntity;
import com.sena.academico.common.EstadoRegistro;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "programas", uniqueConstraints = @UniqueConstraint(columnNames = "codigo"))
public class Programa extends AuditableEntity {

    @NotBlank
    @Column(nullable = false, length = 20)
    private String codigo;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String nombre;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private NivelFormacion nivel;

    @NotBlank
    @Column(nullable = false, length = 10)
    private String version;

    @Positive
    @Column(name = "duracion_horas", nullable = false)
    private Integer duracionHoras;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoRegistro estado = EstadoRegistro.ACTIVO;

    @OneToMany(mappedBy = "programa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("codigo asc")
    private List<Competencia> competencias = new ArrayList<>();

    protected Programa() {
    }

    public Programa(String codigo, String nombre, NivelFormacion nivel, String version, Integer duracionHoras) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.nivel = nivel;
        this.version = version;
        this.duracionHoras = duracionHoras;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public NivelFormacion getNivel() {
        return nivel;
    }

    public String getVersion() {
        return version;
    }

    public Integer getDuracionHoras() {
        return duracionHoras;
    }

    public EstadoRegistro getEstado() {
        return estado;
    }

    public void desactivar() {
        this.estado = EstadoRegistro.INACTIVO;
    }

    public void activar() {
        this.estado = EstadoRegistro.ACTIVO;
    }

    public List<Competencia> getCompetencias() {
        return competencias;
    }

    public void agregarCompetencia(Competencia competencia) {
        competencias.add(competencia);
        competencia.asignarPrograma(this);
    }
}
