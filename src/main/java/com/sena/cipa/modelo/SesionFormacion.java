package com.sena.cipa.modelo;

// Una sesion de formacion es cada clase que se le dicta a una ficha.
public class SesionFormacion {

    private int id;
    private int fichaId;
    private String fecha;         // se guarda como texto "aaaa-mm-dd"
    private String horaInicio;    // se guarda como texto "HH:mm:00"
    private String horaFin;
    private String tema;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getNumeroFicha() {
        return numeroFicha;
    }

    public void setNumeroFicha(String numeroFicha) {
        this.numeroFicha = numeroFicha;
    }

    // asi se ve una sesion dentro de una lista desplegable
    @Override
    public String toString() {
        return numeroFicha + " - " + fecha + " " + tema;
    }
}
