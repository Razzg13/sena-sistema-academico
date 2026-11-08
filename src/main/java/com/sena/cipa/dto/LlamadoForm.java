package com.sena.cipa.dto;

import java.util.ArrayList;
import java.util.List;

import com.sena.cipa.modelo.EstadoAsistencia;

public class LlamadoForm {

    public static class Item {
        private Long matriculaId;
        private String nombreAprendiz;
        private EstadoAsistencia estado = EstadoAsistencia.PRESENTE;
        private String observacion;

        public Long getMatriculaId() {
            return matriculaId;
        }

        public void setMatriculaId(Long matriculaId) {
            this.matriculaId = matriculaId;
        }

        public String getNombreAprendiz() {
            return nombreAprendiz;
        }

        public void setNombreAprendiz(String nombreAprendiz) {
            this.nombreAprendiz = nombreAprendiz;
        }

        public EstadoAsistencia getEstado() {
            return estado;
        }

        public void setEstado(EstadoAsistencia estado) {
            this.estado = estado;
        }

        public String getObservacion() {
            return observacion;
        }

        public void setObservacion(String observacion) {
            this.observacion = observacion;
        }
    }

    private List<Item> items = new ArrayList<>();

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
