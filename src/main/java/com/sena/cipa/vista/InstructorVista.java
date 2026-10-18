package com.sena.cipa.vista;

import com.sena.cipa.modelo.Instructor;
import com.sena.cipa.modelo.Usuario;

import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class InstructorVista extends VentanaCrud {

    public JComboBox<Usuario> cmbUsuario = new JComboBox<>();
    public JTextField txtEspecialidad = new JTextField();
    public JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"});

    public InstructorVista() {
        super("Instructores",
                new String[]{"Id", "Documento", "Nombre", "Especialidad", "Estado"},
                "Buscar por documento", "Inactivar");
        setSubtitulo("Datos propios del instructor");
        agregarCampo("Usuario (rol INSTRUCTOR)", cmbUsuario);
        agregarCampo("Especialidad", txtEspecialidad);
        agregarCampo("Estado", cmbEstado);
        terminar();
        anchoColumna(0, 40);
        anchoColumna(1, 100);
        anchoColumna(2, 220);
        anchoColumna(3, 180);
        anchoColumna(4, 90);
    }

    public void mostrar(Instructor i) {
        setIdSeleccionado(i.getId());
        elegir(cmbUsuario, Usuario::getId, i.getUsuarioId());
        txtEspecialidad.setText(i.getEspecialidad());
        cmbEstado.setSelectedItem(i.getEstado());
    }

    public void limpiar() {
        limpiarSeleccion();
        if (cmbUsuario.getItemCount() > 0) {
            cmbUsuario.setSelectedIndex(0);
        }
        txtEspecialidad.setText("");
        cmbEstado.setSelectedIndex(0);
    }

    public void cargarTabla(List<Instructor> lista) {
        vaciarTabla();
        for (Instructor i : lista) {
            agregarFila(i.getId(), i.getDocumento(), i.getNombreCompleto(), i.getEspecialidad(), i.getEstado());
        }
    }
}
