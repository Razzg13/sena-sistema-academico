package com.sena.cipa.modelo;

// Registro de asistencia: si un aprendiz matriculado asistio a una sesion de formacion.
public class Asistencia {

    private int id;
    private int sesionId;
    private int matriculaId;
    private String estado;
    private String observacion;
    private String fechaSesion;      // datos que se traen con la consulta, solo para mostrar
    private String temaSesion;
    private String numeroFicha;
    private String documentoAprendiz;
    private String nombreAprendiz;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSesionId() {
        return sesionId;
    }

    public void setSesionId(int sesionId) {
        this.sesionId = sesionId;
    }

    public int getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(int matriculaId) {
        this.matriculaId = matriculaId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getFechaSesion() {
        return fechaSesion;
    }

    public void setFechaSesion(String fechaSesion) {
        this.fechaSesion = fechaSesion;
    }

    public String getTemaSesion() {
        return temaSesion;
    }

    public void setTemaSesion(String temaSesion) {
        this.temaSesion = temaSesion;
    }

    public String getNumeroFicha() {
        return numeroFicha;
    }

    public void setNumeroFicha(String numeroFicha) {
        this.numeroFicha = numeroFicha;
    }

    public String getDocumentoAprendiz() {
        return documentoAprendiz;
    }

    public void setDocumentoAprendiz(String documentoAprendiz) {
        this.documentoAprendiz = documentoAprendiz;
    }

    public String getNombreAprendiz() {
        return nombreAprendiz;
    }

    public void setNombreAprendiz(String nombreAprendiz) {
        this.nombreAprendiz = nombreAprendiz;
    }

    @Override
    public String toString() {
        return fechaSesion + " " + temaSesion + " - " + nombreAprendiz;
    }
}
