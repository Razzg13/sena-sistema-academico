package com.sena.cipa.vista;

import com.sena.cipa.modelo.Usuario;

import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class UsuarioVista extends VentanaCrud {

    public JTextField txtDocumento = new JTextField();
    public JTextField txtNombre = new JTextField();
    public JTextField txtCorreo = new JTextField();
    public JTextField txtTelefono = new JTextField();
    public JPasswordField txtClave = new JPasswordField();
    public JPasswordField txtConfirmar = new JPasswordField();
    public JComboBox<String> cmbRol = new JComboBox<>(new String[]{
        "ADMINISTRADOR", "COORDINACION_ACADEMICA", "INSTRUCTOR", "APRENDIZ", "CONSULTA_AUDITOR"});
    public JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"});

    public UsuarioVista() {
        super("Registro de usuarios", new String[]{"Id", "Documento", "Nombre completo", "Correo", "Rol", "Estado"},
                "Buscar por documento", "Inactivar");
        agregarCampo("Documento", txtDocumento);
        agregarCampo("Nombre completo", txtNombre);
        agregarCampo("Correo", txtCorreo);
        agregarCampo("Telefono (opcional)", txtTelefono);
        agregarCampo("Clave (al modificar, vacia = no cambiarla)", txtClave);
        agregarCampo("Confirmar clave", txtConfirmar);
        agregarCampo("Rol", cmbRol);
        agregarCampo("Estado", cmbEstado);
        terminar();
    }

    public void mostrar(Usuario u) {
        setIdSeleccionado(u.getId());
        txtDocumento.setText(u.getDocumento());
        txtNombre.setText(u.getNombreCompleto());
        txtCorreo.setText(u.getCorreo());
        txtTelefono.setText(u.getTelefono() == null ? "" : u.getTelefono());
        txtClave.setText("");
        txtConfirmar.setText("");
        cmbRol.setSelectedItem(u.getRol());
        cmbEstado.setSelectedItem(u.getEstado());
    }

    public void limpiar() {
        limpiarSeleccion();
        txtDocumento.setText("");
        txtNombre.setText("");
        txtCorreo.setText("");
        txtTelefono.setText("");
        txtClave.setText("");
        txtConfirmar.setText("");
        cmbRol.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
    }

    public void cargarTabla(List<Usuario> lista) {
        vaciarTabla();
        for (Usuario u : lista) {
            agregarFila(u.getId(), u.getDocumento(), u.getNombreCompleto(), u.getCorreo(), u.getRol(), u.getEstado());
        }
    }
}
