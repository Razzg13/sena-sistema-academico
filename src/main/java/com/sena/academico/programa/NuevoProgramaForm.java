package com.sena.academico.programa;

import com.sena.academico.programa.NivelFormacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class NuevoProgramaForm {

    @NotBlank
    private String codigo;

    @NotBlank
    private String nombre;

    @NotNull
    private NivelFormacion nivel;

    @NotBlank
    private String version;

    @NotNull
    @Positive
    private Integer duracionHoras;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public NivelFormacion getNivel() {
        return nivel;
    }

    public void setNivel(NivelFormacion nivel) {
        this.nivel = nivel;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getDuracionHoras() {
        return duracionHoras;
    }

    public void setDuracionHoras(Integer duracionHoras) {
        this.duracionHoras = duracionHoras;
    }
}
