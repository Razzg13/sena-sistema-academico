package com.sena.cipa.modelo;

// Una ficha es el grupo de aprendices que cursa un programa de formacion.
public class Ficha {

    private int id;
    private String numero;
    private int programaId;
    private String jornada;
    private String fechaInicio;   // se guarda como texto "aaaa-mm-dd"
    private String fechaFin;
    private String ambiente;
    private String estado;
    private String nombrePrograma;   // solo para mostrar en la tabla y en las listas

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int getProgramaId() {
        return programaId;
    }

    public void setProgramaId(int programaId) {
        this.programaId = programaId;
    }

    public String getJornada() {
        return jornada;
    }

    public void setJornada(String jornada) {
        this.jornada = jornada;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombrePrograma() {
        return nombrePrograma;
    }

    public void setNombrePrograma(String nombrePrograma) {
        this.nombrePrograma = nombrePrograma;
    }

    // asi se ve una ficha dentro de una lista desplegable
    @Override
    public String toString() {
        return numero + " - " + (nombrePrograma == null ? "sin programa" : nombrePrograma);
    }
}
