package com.sena.cipa.vista;

import com.sena.cipa.modelo.Programa;

import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class ProgramaVista extends VentanaCrud {

    public JTextField txtCodigo = new JTextField();
    public JTextField txtNombre = new JTextField();
    public JComboBox<String> cmbNivel = new JComboBox<>(new String[]{
        "AUXILIAR", "COMPLEMENTARIA", "ESPECIALIZACION_TECNOLOGICA", "OPERARIO", "TECNICO", "TECNOLOGO"});
    public JTextField txtVersion = new JTextField();
    public JTextField txtDuracion = new JTextField();
    public JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"});

    public ProgramaVista() {
        super("Programas de formacion", new String[]{"Id", "Codigo", "Nombre", "Nivel", "Version", "Horas", "Estado"},
                "Buscar por codigo", "Inactivar");
        agregarCampo("Codigo", txtCodigo);
        agregarCampo("Nombre", txtNombre);
        agregarCampo("Nivel", cmbNivel);
        agregarCampo("Version", txtVersion);
        agregarCampo("Duracion (horas)", txtDuracion);
        agregarCampo("Estado", cmbEstado);
        terminar();
    }

    public void mostrar(Programa p) {
        setIdSeleccionado(p.getId());
        txtCodigo.setText(p.getCodigo());
        txtNombre.setText(p.getNombre());
        cmbNivel.setSelectedItem(p.getNivel());
        txtVersion.setText(p.getVersion());
        txtDuracion.setText(String.valueOf(p.getDuracionHoras()));
        cmbEstado.setSelectedItem(p.getEstado());
    }

    public void limpiar() {
        limpiarSeleccion();
        txtCodigo.setText("");
        txtNombre.setText("");
        cmbNivel.setSelectedIndex(0);
        txtVersion.setText("");
        txtDuracion.setText("");
        cmbEstado.setSelectedIndex(0);
    }

    public void cargarTabla(List<Programa> lista) {
        vaciarTabla();
        for (Programa p : lista) {
            agregarFila(p.getId(), p.getCodigo(), p.getNombre(), p.getNivel(), p.getVersion(),
                    p.getDuracionHoras(), p.getEstado());
        }
    }
}
