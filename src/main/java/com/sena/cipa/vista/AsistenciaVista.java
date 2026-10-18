package com.sena.cipa.vista;

import com.sena.cipa.modelo.Asistencia;
import com.sena.cipa.modelo.Matricula;
import com.sena.cipa.modelo.SesionFormacion;

import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class AsistenciaVista extends VentanaCrud {

    public JComboBox<SesionFormacion> cmbSesion = new JComboBox<>();
    public JComboBox<Matricula> cmbMatricula = new JComboBox<>();
    public JComboBox<String> cmbEstado = new JComboBox<>(new String[]{
        "PRESENTE", "AUSENTE", "TARDANZA", "EXCUSADO"});
    public JTextField txtObservacion = new JTextField();

    public AsistenciaVista() {
        super("Asistencias",
                new String[]{"Id", "Sesion", "Aprendiz", "Ficha", "Estado", "Observacion"},
                "Buscar por documento", "Eliminar");
        setSubtitulo("Control de asistencia por sesion");
        agregarCampo("Sesion", cmbSesion);
        agregarCampo("Matricula (aprendiz y ficha)", cmbMatricula);
        agregarCampo("Estado", cmbEstado);
        agregarCampo("Observacion (opcional)", txtObservacion);
        terminar();
        anchoColumna(0, 40);
        anchoColumna(1, 240);
        anchoColumna(2, 160);
        anchoColumna(3, 80);
    }

    public void mostrar(Asistencia a) {
        setIdSeleccionado(a.getId());
        elegir(cmbSesion, SesionFormacion::getId, a.getSesionId());
        elegir(cmbMatricula, Matricula::getId, a.getMatriculaId());
        cmbEstado.setSelectedItem(a.getEstado());
        txtObservacion.setText(a.getObservacion() == null ? "" : a.getObservacion());
    }

    public void limpiar() {
        limpiarSeleccion();
        if (cmbSesion.getItemCount() > 0) {
            cmbSesion.setSelectedIndex(0);
        }
        if (cmbMatricula.getItemCount() > 0) {
            cmbMatricula.setSelectedIndex(0);
        }
        cmbEstado.setSelectedIndex(0);
        txtObservacion.setText("");
    }

    public void cargarTabla(List<Asistencia> lista) {
        vaciarTabla();
        for (Asistencia a : lista) {
            agregarFila(a.getId(), a.getFechaSesion() + " - " + a.getTemaSesion(), a.getNombreAprendiz(),
                    a.getNumeroFicha(), a.getEstado(), a.getObservacion() == null ? "" : a.getObservacion());
        }
    }
}
