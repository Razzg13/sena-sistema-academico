package com.sena.cipa.vista;

import com.sena.cipa.modelo.Aprendiz;
import com.sena.cipa.modelo.Ficha;
import com.sena.cipa.modelo.Matricula;

import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class MatriculaVista extends VentanaCrud {

    public JComboBox<Aprendiz> cmbAprendiz = new JComboBox<>();
    public JComboBox<Ficha> cmbFicha = new JComboBox<>();
    public JTextField txtFechaMatricula = new JTextField();
    public JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"ACTIVA", "CANCELADA"});

    public MatriculaVista() {
        super("Matriculas",
                new String[]{"Id", "Aprendiz", "Documento", "Ficha", "Fecha", "Estado"},
                "Buscar por documento", "Cancelar matricula");
        setSubtitulo("Aprendiz inscrito en una ficha");
        agregarCampo("Aprendiz", cmbAprendiz);
        agregarCampo("Ficha", cmbFicha);
        agregarCampo("Fecha de matricula (aaaa-mm-dd)", txtFechaMatricula);
        agregarCampo("Estado", cmbEstado);
        terminar();
        anchoColumna(0, 40);
        anchoColumna(1, 220);
        anchoColumna(2, 100);
        anchoColumna(3, 90);
        anchoColumna(5, 90);
    }

    public void mostrar(Matricula m) {
        setIdSeleccionado(m.getId());
        elegir(cmbAprendiz, Aprendiz::getId, m.getAprendizId());
        elegir(cmbFicha, Ficha::getId, m.getFichaId());
        txtFechaMatricula.setText(m.getFechaMatricula());
        cmbEstado.setSelectedItem(m.getEstado());
    }

    public void limpiar() {
        limpiarSeleccion();
        if (cmbAprendiz.getItemCount() > 0) {
            cmbAprendiz.setSelectedIndex(0);
        }
        if (cmbFicha.getItemCount() > 0) {
            cmbFicha.setSelectedIndex(0);
        }
        txtFechaMatricula.setText("");
        cmbEstado.setSelectedIndex(0);
    }

    public void cargarTabla(List<Matricula> lista) {
        vaciarTabla();
        for (Matricula m : lista) {
            agregarFila(m.getId(), m.getNombreAprendiz(), m.getDocumentoAprendiz(), m.getNumeroFicha(),
                    m.getFechaMatricula(), m.getEstado());
        }
    }
}
