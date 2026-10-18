package com.sena.cipa.vista;

import com.sena.cipa.modelo.ActividadEvaluativa;
import com.sena.cipa.modelo.Ficha;

import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class ActividadEvaluativaVista extends VentanaCrud {

    public JComboBox<Ficha> cmbFicha = new JComboBox<>();
    public JTextField txtNombre = new JTextField();
    public JTextField txtDescripcion = new JTextField();
    public JTextField txtFechaLimite = new JTextField();

    public ActividadEvaluativaVista() {
        super("Actividades evaluativas",
                new String[]{"Id", "Ficha", "Nombre", "Descripcion", "Fecha limite"},
                "Buscar por nombre", "Eliminar");
        setSubtitulo("Talleres, pruebas y proyectos");
        agregarCampo("Ficha", cmbFicha);
        agregarCampo("Nombre", txtNombre);
        agregarCampo("Descripcion", txtDescripcion);
        agregarCampo("Fecha limite (aaaa-mm-dd)", txtFechaLimite);
        terminar();
        anchoColumna(0, 40);
        anchoColumna(1, 80);
        anchoColumna(2, 200);
        anchoColumna(3, 240);
    }

    public void mostrar(ActividadEvaluativa a) {
        setIdSeleccionado(a.getId());
        elegir(cmbFicha, Ficha::getId, a.getFichaId());
        txtNombre.setText(a.getNombre());
        txtDescripcion.setText(a.getDescripcion() == null ? "" : a.getDescripcion());
        txtFechaLimite.setText(a.getFechaLimite());
    }

    public void limpiar() {
        limpiarSeleccion();
        if (cmbFicha.getItemCount() > 0) {
            cmbFicha.setSelectedIndex(0);
        }
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtFechaLimite.setText("");
    }

    public void cargarTabla(List<ActividadEvaluativa> lista) {
        vaciarTabla();
        for (ActividadEvaluativa a : lista) {
            agregarFila(a.getId(), a.getNumeroFicha(), a.getNombre(), a.getDescripcion(),
                    a.getFechaLimite());
        }
    }
}
