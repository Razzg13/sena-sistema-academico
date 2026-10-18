package com.sena.cipa.controlador;

import com.sena.cipa.modelo.Usuario;
import com.sena.cipa.modelo.UsuarioDAO;
import com.sena.cipa.vista.UsuarioVista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// CONTROLADOR: recibe los clics de la vista, valida, llama al DAO (modelo) y actualiza la vista.
public class UsuarioControlador implements ActionListener {

    private UsuarioVista vista;
    private UsuarioDAO dao;

    public UsuarioControlador(UsuarioVista vista, UsuarioDAO dao) {
        this.vista = vista;
        this.dao = dao;

        vista.btnRegistrar.addActionListener(this);
        vista.btnBuscar.addActionListener(this);
        vista.btnModificar.addActionListener(this);
        vista.btnEliminar.addActionListener(this);
        vista.btnLimpiar.addActionListener(this);
        vista.tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarFila();
            }
        });

        vista.cargarTabla(dao.listarTodos());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        if (origen == vista.btnRegistrar) {
            registrar();
        } else if (origen == vista.btnBuscar) {
            buscar();
        } else if (origen == vista.btnModificar) {
            modificar();
        } else if (origen == vista.btnEliminar) {
            inactivar();
        } else if (origen == vista.btnLimpiar) {
            vista.limpiar();
        }
    }

    // nuevo = true cuando se va a registrar (ahi la clave es obligatoria)
    private Usuario leerFormulario(boolean nuevo) {
        String documento = vista.txtDocumento.getText().trim();
        String nombre = vista.txtNombre.getText().trim();
        String correo = vista.txtCorreo.getText().trim();
        String telefono = vista.txtTelefono.getText().trim();
        String clave = new String(vista.txtClave.getPassword());
        String confirmar = new String(vista.txtConfirmar.getPassword());

        if (documento.isEmpty() || nombre.isEmpty() || correo.isEmpty()) {
            vista.mostrarMensaje("Completa el documento, el nombre y el correo");
            return null;
        }
        if (!correo.contains("@") || !correo.contains(".")) {
            vista.mostrarMensaje("El correo no es valido");
            return null;
        }
        if (nuevo && clave.isEmpty()) {
            vista.mostrarMensaje("Escribe una clave para el usuario");
            return null;
        }
        if (!clave.isEmpty() && clave.length() < 6) {
            vista.mostrarMensaje("La clave debe tener minimo 6 caracteres");
            return null;
        }
        if (!clave.equals(confirmar)) {
            vista.mostrarMensaje("Las claves no coinciden");
            return null;
        }
        Usuario u = new Usuario();
        u.setDocumento(documento);
        u.setNombreCompleto(nombre);
        u.setCorreo(correo);
        u.setTelefono(telefono.isEmpty() ? null : telefono);
        u.setClave(clave);
        u.setRol((String) vista.cmbRol.getSelectedItem());
        u.setEstado((String) vista.cmbEstado.getSelectedItem());
        return u;
    }

    private void registrar() {
        Usuario u = leerFormulario(true);
        if (u == null) {
            return;
        }
        if (dao.registrar(u)) {
            vista.estado("Usuario registrado");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo registrar. Revisa que el documento y el correo no esten repetidos");
        }
    }

    private void buscar() {
        String documento = vista.txtDocumento.getText().trim();
        if (documento.isEmpty()) {
            vista.mostrarMensaje("Escribe el documento que quieres buscar");
            return;
        }
        Usuario u = dao.buscarPorDocumento(documento);
        if (u == null) {
            vista.mostrarMensaje("No existe un usuario con el documento " + documento);
        } else {
            vista.mostrar(u);
        }
    }

    private void modificar() {
        if (vista.getIdSeleccionado() == 0) {
            vista.mostrarMensaje("Primero selecciona un usuario de la tabla o buscalo por documento");
            return;
        }
        Usuario u = leerFormulario(false);
        if (u == null) {
            return;
        }
        u.setId(vista.getIdSeleccionado());
        if (dao.actualizar(u)) {
            vista.estado("Usuario modificado");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo modificar. Revisa que el documento y el correo no esten repetidos");
        }
    }

    private void inactivar() {
        int id = vista.getIdSeleccionado();
        if (id == 0) {
            vista.mostrarMensaje("Primero selecciona un usuario de la tabla o buscalo por documento");
            return;
        }
        if (!vista.confirmar("Seguro que quieres inactivar este usuario?")) {
            return;
        }
        if (dao.eliminar(id)) {
            vista.estado("Usuario inactivado");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo inactivar el usuario");
        }
    }

    private void seleccionarFila() {
        int id = vista.idDeFilaSeleccionada();
        if (id == 0) {
            return;
        }
        Usuario u = dao.leerPorId(id);
        if (u != null) {
            vista.mostrar(u);
        }
    }
}
