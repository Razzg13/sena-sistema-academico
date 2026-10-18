package com.sena.cipa.modelo;

// Un instructor es un usuario con rol INSTRUCTOR mas los datos propios de su cargo.
public class Instructor {

    private int id;
    private int usuarioId;
    private String especialidad;
    private String estado;
    private String documento;        // vienen de la tabla usuarios (solo para mostrar)
    private String nombreCompleto;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    // asi se ve un instructor dentro de una lista desplegable
    @Override
    public String toString() {
        return documento + " - " + nombreCompleto;
    }
}
