package com.sena.cipa.controlador;

import com.sena.cipa.modelo.Instructor;
import com.sena.cipa.modelo.InstructorDAO;
import com.sena.cipa.modelo.Usuario;
import com.sena.cipa.modelo.UsuarioDAOImpl;
import com.sena.cipa.vista.InstructorVista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// CONTROLADOR: recibe los clics de la vista, valida, llama al DAO (modelo) y actualiza la vista.
public class InstructorControlador implements ActionListener {

    private InstructorVista vista;
    private InstructorDAO dao;

    public InstructorControlador(InstructorVista vista, InstructorDAO dao) {
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

        // la lista de usuarios se llena solo con los que tienen rol INSTRUCTOR
        vista.llenarCombo(vista.cmbUsuario, new UsuarioDAOImpl().listarPorRol("INSTRUCTOR"));
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

    private Instructor leerFormulario() {
        String especialidad = vista.txtEspecialidad.getText().trim();

        if (vista.cmbUsuario.getItemCount() == 0) {
            vista.mostrarMensaje("Primero registra un usuario con rol INSTRUCTOR");
            return null;
        }
        if (especialidad.isEmpty()) {
            vista.mostrarMensaje("Completa la especialidad del instructor");
            return null;
        }

        Instructor i = new Instructor();
        i.setUsuarioId(((Usuario) vista.cmbUsuario.getSelectedItem()).getId());
        i.setEspecialidad(especialidad);
        i.setEstado((String) vista.cmbEstado.getSelectedItem());
        return i;
    }

    private void registrar() {
        Instructor i = leerFormulario();
        if (i == null) {
            return;
        }
        if (dao.registrar(i)) {
            vista.estado("Instructor registrado");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo registrar. Revisa que el usuario no este ya registrado como instructor");
        }
    }

    // busca por el documento que se escribe en el cuadro de dialogo
    private void buscar() {
        String documento = vista.pedirTexto("Documento del instructor:");
        if (documento.isEmpty()) {
            return;
        }
        Instructor i = dao.buscarPorDocumento(documento);
        if (i == null) {
            vista.mostrarMensaje("No existe un instructor con el documento " + documento);
        } else {
            vista.mostrar(i);
            vista.estado("Instructor " + documento + " encontrado");
        }
    }

    private void modificar() {
        if (vista.getIdSeleccionado() == 0) {
            vista.mostrarMensaje("Primero selecciona un instructor de la tabla o buscalo por documento");
            return;
        }
        Instructor i = leerFormulario();
        if (i == null) {
            return;
        }
        i.setId(vista.getIdSeleccionado());
        if (dao.actualizar(i)) {
            vista.estado("Instructor modificado");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo modificar. Revisa que el usuario no este ya registrado como instructor");
        }
    }

    private void inactivar() {
        int id = vista.getIdSeleccionado();
        if (id == 0) {
            vista.mostrarMensaje("Primero selecciona un instructor de la tabla o buscalo por documento");
            return;
        }
        if (!vista.confirmar("Seguro que quieres inactivar este instructor?")) {
            return;
        }
        if (dao.eliminar(id)) {
            vista.estado("Instructor inactivado");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo inactivar el instructor");
        }
    }

    private void seleccionarFila() {
        int id = vista.idDeFilaSeleccionada();
        if (id == 0) {
            return;
        }
        Instructor i = dao.leerPorId(id);
        if (i != null) {
            vista.mostrar(i);
        }
    }
}
