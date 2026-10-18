package com.sena.cipa.evaluacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CalificarForm {

    public static class Item {
        private Long matriculaId;
        private String nombreAprendiz;
        private BigDecimal calificacion;
        private JuicioEvaluacion juicio = JuicioEvaluacion.POR_MEJORAR;
        private String retroalimentacion;

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

        public BigDecimal getCalificacion() {
            return calificacion;
        }

        public void setCalificacion(BigDecimal calificacion) {
            this.calificacion = calificacion;
        }

        public JuicioEvaluacion getJuicio() {
            return juicio;
        }

        public void setJuicio(JuicioEvaluacion juicio) {
            this.juicio = juicio;
        }

        public String getRetroalimentacion() {
            return retroalimentacion;
        }

        public void setRetroalimentacion(String retroalimentacion) {
            this.retroalimentacion = retroalimentacion;
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
