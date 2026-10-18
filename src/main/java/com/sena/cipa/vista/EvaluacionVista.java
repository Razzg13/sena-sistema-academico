package com.sena.cipa.vista;

import com.sena.cipa.modelo.ActividadEvaluativa;
import com.sena.cipa.modelo.Evaluacion;
import com.sena.cipa.modelo.Matricula;

import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class EvaluacionVista extends VentanaCrud {

    public JComboBox<ActividadEvaluativa> cmbActividad = new JComboBox<>();
    public JComboBox<Matricula> cmbMatricula = new JComboBox<>();
    public JTextField txtCalificacion = new JTextField();
    public JComboBox<String> cmbJuicio = new JComboBox<>(new String[]{
        "APROBADO", "NO_APROBADO", "POR_MEJORAR"});
    public JTextField txtRetroalimentacion = new JTextField();
    public JTextField txtFecha = new JTextField();

    public EvaluacionVista() {
        super("Evaluaciones",
                new String[]{"Id", "Actividad", "Aprendiz", "Ficha", "Nota", "Juicio", "Fecha"},
                "Buscar por documento", "Eliminar");
        setSubtitulo("Notas y juicios por actividad");
        agregarCampo("Actividad", cmbActividad);
        agregarCampo("Matricula (aprendiz y ficha)", cmbMatricula);
        agregarCampo("Calificacion de 0.00 a 5.00 (opcional)", txtCalificacion);
        agregarCampo("Juicio", cmbJuicio);
        agregarCampo("Retroalimentacion (opcional)", txtRetroalimentacion);
        agregarCampo("Fecha de evaluacion (aaaa-mm-dd)", txtFecha);
        terminar();
        anchoColumna(0, 40);
        anchoColumna(1, 200);
        anchoColumna(2, 160);
        anchoColumna(4, 60);
    }

    public void mostrar(Evaluacion e) {
        setIdSeleccionado(e.getId());
        elegir(cmbActividad, ActividadEvaluativa::getId, e.getActividadId());
        elegir(cmbMatricula, Matricula::getId, e.getMatriculaId());
        txtCalificacion.setText(e.getCalificacion() == null ? "" : e.getCalificacion());
        cmbJuicio.setSelectedItem(e.getJuicio());
        txtRetroalimentacion.setText(e.getRetroalimentacion() == null ? "" : e.getRetroalimentacion());
        txtFecha.setText(e.getFechaEvaluacion());
    }

    public void limpiar() {
        limpiarSeleccion();
        if (cmbActividad.getItemCount() > 0) {
            cmbActividad.setSelectedIndex(0);
        }
        if (cmbMatricula.getItemCount() > 0) {
            cmbMatricula.setSelectedIndex(0);
        }
        txtCalificacion.setText("");
        cmbJuicio.setSelectedIndex(0);
        txtRetroalimentacion.setText("");
        txtFecha.setText("");
    }

    public void cargarTabla(List<Evaluacion> lista) {
        vaciarTabla();
        for (Evaluacion e : lista) {
            agregarFila(e.getId(), e.getNombreActividad(), e.getNombreAprendiz(), e.getNumeroFicha(),
                    e.getCalificacion() == null ? "sin nota" : e.getCalificacion(), e.getJuicio(),
                    e.getFechaEvaluacion());
        }
    }
}
