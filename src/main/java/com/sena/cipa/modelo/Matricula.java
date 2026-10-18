package com.sena.cipa.modelo;

// Una matricula une un aprendiz con una ficha: es la tabla bisagra del sistema.
public class Matricula {

    private int id;
    private int aprendizId;
    private int fichaId;
    private String fechaMatricula;   // se guarda como texto "aaaa-mm-dd"
    private String estado;
    private String documentoAprendiz;   // solo para mostrar en la tabla
    private String nombreAprendiz;
    private String numeroFicha;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAprendizId() {
        return aprendizId;
    }

    public void setAprendizId(int aprendizId) {
        this.aprendizId = aprendizId;
    }

    public int getFichaId() {
        return fichaId;
    }

    public void setFichaId(int fichaId) {
        this.fichaId = fichaId;
    }

    public String getFechaMatricula() {
        return fechaMatricula;
    }

    public void setFechaMatricula(String fechaMatricula) {
        this.fechaMatricula = fechaMatricula;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public String getNumeroFicha() {
        return numeroFicha;
    }

    public void setNumeroFicha(String numeroFicha) {
        this.numeroFicha = numeroFicha;
    }

    // asi se ve una matricula dentro de una lista desplegable
    @Override
    public String toString() {
        return nombreAprendiz + " (" + numeroFicha + ")";
    }
}
