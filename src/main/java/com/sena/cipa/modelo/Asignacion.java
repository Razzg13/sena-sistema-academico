package com.sena.cipa.modelo;

// Una asignacion dice que instructor atiende una ficha y en que fechas.
public class Asignacion {

    private int id;
    private int fichaId;
    private int instructorId;
    private String fechaInicio;   // se guardan como texto "aaaa-mm-dd"
    private String fechaFin;
    private String numeroFicha;      // solo para mostrar en la tabla
    private String nombreInstructor;

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

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
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

    public String getNumeroFicha() {
        return numeroFicha;
    }

    public void setNumeroFicha(String numeroFicha) {
        this.numeroFicha = numeroFicha;
    }

    public String getNombreInstructor() {
        return nombreInstructor;
    }

    public void setNombreInstructor(String nombreInstructor) {
        this.nombreInstructor = nombreInstructor;
    }

    // asi se ve una asignacion dentro de una lista desplegable
    @Override
    public String toString() {
        return numeroFicha + " - " + nombreInstructor;
    }
}
