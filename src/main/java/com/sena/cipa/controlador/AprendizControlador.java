package com.sena.cipa.controlador;

import com.sena.cipa.modelo.Aprendiz;
import com.sena.cipa.modelo.AprendizDAO;
import com.sena.cipa.modelo.Usuario;
import com.sena.cipa.modelo.UsuarioDAOImpl;
import com.sena.cipa.vista.AprendizVista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// CONTROLADOR: recibe los clics de la vista, valida, llama al DAO (modelo) y actualiza la vista.
public class AprendizControlador implements ActionListener {

    private AprendizVista vista;
    private AprendizDAO dao;

    public AprendizControlador(AprendizVista vista, AprendizDAO dao) {
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

        // la lista de usuarios se llena solo con los que tienen rol APRENDIZ
        vista.llenarCombo(vista.cmbUsuario, new UsuarioDAOImpl().listarPorRol("APRENDIZ"));
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

    private Aprendiz leerFormulario() {
        if (vista.cmbUsuario.getItemCount() == 0) {
            vista.mostrarMensaje("Primero registra un usuario con rol APRENDIZ");
            return null;
        }

        Aprendiz a = new Aprendiz();
        a.setUsuarioId(((Usuario) vista.cmbUsuario.getSelectedItem()).getId());
        a.setEstadoFormativo((String) vista.cmbEstadoFormativo.getSelectedItem());
        a.setEstado((String) vista.cmbEstado.getSelectedItem());
        return a;
    }

    private void registrar() {
        Aprendiz a = leerFormulario();
        if (a == null) {
            return;
        }
        if (dao.registrar(a)) {
            vista.estado("Aprendiz registrado");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo registrar. Revisa que el usuario no este ya registrado como aprendiz");
        }
    }

    // busca por el documento que se escribe en el cuadro de dialogo
    private void buscar() {
        String documento = vista.pedirTexto("Documento del aprendiz:");
        if (documento.isEmpty()) {
            return;
        }
        Aprendiz a = dao.buscarPorDocumento(documento);
        if (a == null) {
            vista.mostrarMensaje("No existe un aprendiz con el documento " + documento);
        } else {
            vista.mostrar(a);
            vista.estado("Aprendiz " + documento + " encontrado");
        }
    }

    private void modificar() {
        if (vista.getIdSeleccionado() == 0) {
            vista.mostrarMensaje("Primero selecciona un aprendiz de la tabla o buscalo por documento");
            return;
        }
        Aprendiz a = leerFormulario();
        if (a == null) {
            return;
        }
        a.setId(vista.getIdSeleccionado());
        if (dao.actualizar(a)) {
            vista.estado("Aprendiz modificado");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo modificar. Revisa que el usuario no este ya registrado como aprendiz");
        }
    }

    private void inactivar() {
        int id = vista.getIdSeleccionado();
        if (id == 0) {
            vista.mostrarMensaje("Primero selecciona un aprendiz de la tabla o buscalo por documento");
            return;
        }
        if (!vista.confirmar("Seguro que quieres inactivar este aprendiz?")) {
            return;
        }
        if (dao.eliminar(id)) {
            vista.estado("Aprendiz inactivado");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo inactivar el aprendiz");
        }
    }

    private void seleccionarFila() {
        int id = vista.idDeFilaSeleccionada();
        if (id == 0) {
            return;
        }
        Aprendiz a = dao.leerPorId(id);
        if (a != null) {
            vista.mostrar(a);
        }
    }
}
