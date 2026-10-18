package com.sena.cipa.modelo;

// Una actividad evaluativa es un taller, una prueba o un proyecto de una ficha.
public class ActividadEvaluativa {

    private int id;
    private int fichaId;
    private String nombre;
    private String descripcion;   // puede quedar vacia (se guarda como null)
    private String fechaLimite;   // se guarda como texto "aaaa-mm-dd"
    private String numeroFicha;   // solo para mostrar en la tabla y en las listas

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFichaId() {
        return fichaId;
    }

    public void setFichaId(int fichaId) {
        this.fichaId = fichaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(String fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public String getNumeroFicha() {
        return numeroFicha;
    }

    public void setNumeroFicha(String numeroFicha) {
        this.numeroFicha = numeroFicha;
    }

    // asi se ve una actividad dentro de una lista desplegable
    @Override
    public String toString() {
        return nombre + " (" + numeroFicha + ")";
    }
}
