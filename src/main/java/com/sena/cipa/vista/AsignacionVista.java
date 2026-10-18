package com.sena.cipa.vista;

import com.sena.cipa.modelo.Asignacion;
import com.sena.cipa.modelo.Ficha;
import com.sena.cipa.modelo.Instructor;

import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class AsignacionVista extends VentanaCrud {

    public JComboBox<Ficha> cmbFicha = new JComboBox<>();
    public JComboBox<Instructor> cmbInstructor = new JComboBox<>();
    public JTextField txtFechaInicio = new JTextField();
    public JTextField txtFechaFin = new JTextField();

    public AsignacionVista() {
        super("Asignaciones de instructor",
                new String[]{"Id", "Ficha", "Instructor", "Desde", "Hasta"},
                "Buscar por ficha", "Eliminar");
        setSubtitulo("Que instructor atiende cada ficha");
        agregarCampo("Ficha", cmbFicha);
        agregarCampo("Instructor", cmbInstructor);
        agregarCampo("Fecha de inicio (aaaa-mm-dd)", txtFechaInicio);
        agregarCampo("Fecha de fin (aaaa-mm-dd)", txtFechaFin);
        terminar();
        anchoColumna(0, 40);
        anchoColumna(1, 100);
        anchoColumna(2, 240);
        anchoColumna(3, 100);
        anchoColumna(4, 100);
    }

    public void mostrar(Asignacion a) {
        setIdSeleccionado(a.getId());
        elegir(cmbFicha, Ficha::getId, a.getFichaId());
        elegir(cmbInstructor, Instructor::getId, a.getInstructorId());
        txtFechaInicio.setText(a.getFechaInicio());
        txtFechaFin.setText(a.getFechaFin());
    }

    public void limpiar() {
        limpiarSeleccion();
        if (cmbFicha.getItemCount() > 0) {
            cmbFicha.setSelectedIndex(0);
        }
        if (cmbInstructor.getItemCount() > 0) {
            cmbInstructor.setSelectedIndex(0);
        }
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
    }

    public void cargarTabla(List<Asignacion> lista) {
        vaciarTabla();
        for (Asignacion a : lista) {
            agregarFila(a.getId(), a.getNumeroFicha(), a.getNombreInstructor(), a.getFechaInicio(), a.getFechaFin());
        }
    }
}
