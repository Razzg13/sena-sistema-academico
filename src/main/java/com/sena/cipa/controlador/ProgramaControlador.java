package com.sena.cipa.controlador;

import com.sena.cipa.modelo.Programa;
import com.sena.cipa.modelo.ProgramaDAO;
import com.sena.cipa.vista.ProgramaVista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// CONTROLADOR: recibe los clics de la vista, valida, llama al DAO (modelo) y actualiza la vista.
public class ProgramaControlador implements ActionListener {

    private ProgramaVista vista;
    private ProgramaDAO dao;

    public ProgramaControlador(ProgramaVista vista, ProgramaDAO dao) {
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

    private Programa leerFormulario() {
        String codigo = vista.txtCodigo.getText().trim();
        String nombre = vista.txtNombre.getText().trim();
        String version = vista.txtVersion.getText().trim();
        String horas = vista.txtDuracion.getText().trim();
        if (codigo.isEmpty() || nombre.isEmpty() || version.isEmpty() || horas.isEmpty()) {
            vista.mostrarMensaje("Completa el codigo, el nombre, la version y la duracion");
            return null;
        }
        int duracion;
        try {
            duracion = Integer.parseInt(horas);
        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("La duracion debe ser un numero entero");
            return null;
        }
        Programa p = new Programa();
        p.setCodigo(codigo);
        p.setNombre(nombre);
        p.setNivel((String) vista.cmbNivel.getSelectedItem());
        p.setVersion(version);
        p.setDuracionHoras(duracion);
        p.setEstado((String) vista.cmbEstado.getSelectedItem());
        return p;
    }

    private void registrar() {
        Programa p = leerFormulario();
        if (p == null) {
            return;
        }
        if (dao.registrar(p)) {
            vista.estado("Programa registrado");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo registrar. Revisa que el codigo no este repetido");
        }
    }

    private void buscar() {
        String codigo = vista.txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            vista.mostrarMensaje("Escribe el codigo que quieres buscar");
            return;
        }
        Programa p = dao.buscarPorCodigo(codigo);
        if (p == null) {
            vista.mostrarMensaje("No existe un programa con el codigo " + codigo);
        } else {
            vista.mostrar(p);
        }
    }

    private void modificar() {
        if (vista.getIdSeleccionado() == 0) {
            vista.mostrarMensaje("Primero selecciona un programa de la tabla o buscalo por codigo");
            return;
        }
        Programa p = leerFormulario();
        if (p == null) {
            return;
        }
        p.setId(vista.getIdSeleccionado());
        if (dao.actualizar(p)) {
            vista.estado("Programa modificado");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo modificar. Revisa que el codigo no este repetido");
        }
    }

    private void inactivar() {
        int id = vista.getIdSeleccionado();
        if (id == 0) {
            vista.mostrarMensaje("Primero selecciona un programa de la tabla o buscalo por codigo");
            return;
        }
        if (!vista.confirmar("Seguro que quieres inactivar este programa?")) {
            return;
        }
        if (dao.eliminar(id)) {
            vista.estado("Programa inactivado");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo inactivar el programa");
        }
    }

    private void seleccionarFila() {
        int id = vista.idDeFilaSeleccionada();
        if (id == 0) {
            return;
        }
        Programa p = dao.leerPorId(id);
        if (p != null) {
            vista.mostrar(p);
        }
    }
}
