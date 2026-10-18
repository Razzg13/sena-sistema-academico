package com.sena.cipa.aprendiz;

import java.time.LocalDate;

import com.sena.cipa.entrega.Entrega;
import com.sena.cipa.evaluacion.ActividadEvaluativa;
import com.sena.cipa.evaluacion.Evaluacion;

public enum EstadoActividad {
    PENDIENTE("Pendiente"),
    ENTREGADA("Entregada"),
    CALIFICADA("Calificada"),
    VENCIDA("Vencida");

    private final String etiqueta;

    EstadoActividad(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getClaseCss() {
        return "estado-" + name().toLowerCase();
    }

    public static EstadoActividad de(ActividadEvaluativa actividad, Entrega entrega, Evaluacion evaluacion, LocalDate hoy) {
        if (evaluacion != null && evaluacion.getCalificacion() != null) {
            return CALIFICADA;
        }
        if (entrega != null) {
            return ENTREGADA;
        }
        return actividad.getFechaLimite().isBefore(hoy) ? VENCIDA : PENDIENTE;
    }
}
