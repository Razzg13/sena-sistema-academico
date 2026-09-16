package com.sena.academico.programa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "competencias")
public class Competencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String codigo;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programa_id", nullable = false)
    private Programa programa;

    @OneToMany(mappedBy = "competencia", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("codigo asc")
    private List<ResultadoAprendizaje> resultadosAprendizaje = new ArrayList<>();

    protected Competencia() {
    }

    public Competencia(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public Programa getPrograma() {
        return programa;
    }

    void asignarPrograma(Programa programa) {
        this.programa = programa;
    }

    public List<ResultadoAprendizaje> getResultadosAprendizaje() {
        return resultadosAprendizaje;
    }

    public void agregarResultadoAprendizaje(ResultadoAprendizaje resultado) {
        resultadosAprendizaje.add(resultado);
        resultado.asignarCompetencia(this);
    }
}
