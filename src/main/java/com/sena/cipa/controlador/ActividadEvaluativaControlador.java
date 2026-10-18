package com.sena.cipa.controlador;

import com.sena.cipa.modelo.ActividadEvaluativa;
import com.sena.cipa.modelo.ActividadEvaluativaDAO;
import com.sena.cipa.modelo.Ficha;
import com.sena.cipa.modelo.FichaDAOImpl;
import com.sena.cipa.vista.ActividadEvaluativaVista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

// CONTROLADOR: recibe los clics de la vista, valida, llama al DAO (modelo) y actualiza la vista.
public class ActividadEvaluativaControlador implements ActionListener {

    private ActividadEvaluativaVista vista;
    private ActividadEvaluativaDAO dao;

    public ActividadEvaluativaControlador(ActividadEvaluativaVista vista, ActividadEvaluativaDAO dao) {
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

        // la lista de fichas se llena con el DAO de fichas
        vista.llenarCombo(vista.cmbFicha, new FichaDAOImpl().listarTodos());
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
            eliminar();
        } else if (origen == vista.btnLimpiar) {
            vista.limpiar();
        }
    }

    private ActividadEvaluativa leerFormulario() {
        String nombre = vista.txtNombre.getText().trim();
        String descripcion = vista.txtDescripcion.getText().trim();
        String fecha = vista.txtFechaLimite.getText().trim();

        if (vista.cmbFicha.getItemCount() == 0) {
            vista.mostrarMensaje("Primero registra una ficha");
            return null;
        }
        if (nombre.isEmpty()) {
            vista.mostrarMensaje("Completa el nombre de la actividad");
            return null;
        }
        if (!fechaValida(fecha)) {
            vista.mostrarMensaje("La fecha limite debe existir y escribirse como aaaa-mm-dd");
            return null;
        }

        ActividadEvaluativa a = new ActividadEvaluativa();
        a.setFichaId(((Ficha) vista.cmbFicha.getSelectedItem()).getId());
        a.setNombre(nombre);
        // la descripcion puede quedar vacia: en la base se guarda como null
        a.setDescripcion(descripcion.isEmpty() ? null : descripcion);
        a.setFechaLimite(fecha);
        return a;
    }

    private boolean fechaValida(String texto) {
        try {
            LocalDate.parse(texto);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    private void registrar() {
        ActividadEvaluativa a = leerFormulario();
        if (a == null) {
            return;
        }
        if (dao.registrar(a)) {
            vista.estado("Actividad registrada");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo registrar la actividad. Revisa los datos de la ficha");
        }
    }

    private void buscar() {
        String nombre = vista.txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            vista.mostrarMensaje("Escribe el nombre de la actividad que quieres buscar");
            return;
        }
        ActividadEvaluativa a = dao.buscarPorNombre(nombre);
        if (a == null) {
            vista.mostrarMensaje("No existe una actividad con el nombre " + nombre);
        } else {
            vista.mostrar(a);
            vista.estado("Actividad encontrada: " + nombre);
        }
    }

    private void modificar() {
        if (vista.getIdSeleccionado() == 0) {
            vista.mostrarMensaje("Primero selecciona una actividad de la tabla o buscala por nombre");
            return;
        }
        ActividadEvaluativa a = leerFormulario();
        if (a == null) {
            return;
        }
        a.setId(vista.getIdSeleccionado());
        if (dao.actualizar(a)) {
            vista.estado("Actividad modificada");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo modificar la actividad. Revisa los datos de la ficha");
        }
    }

    private void eliminar() {
        int id = vista.getIdSeleccionado();
        if (id == 0) {
            vista.mostrarMensaje("Primero selecciona una actividad de la tabla o buscala por nombre");
            return;
        }
        if (!vista.confirmar("Seguro que quieres eliminar esta actividad?")) {
            return;
        }
        if (dao.eliminar(id)) {
            vista.estado("Actividad eliminada");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo eliminar: revisa si la actividad ya tiene evaluaciones registradas");
        }
    }

    private void seleccionarFila() {
        int id = vista.idDeFilaSeleccionada();
        if (id == 0) {
            return;
        }
        ActividadEvaluativa a = dao.leerPorId(id);
        if (a != null) {
            vista.mostrar(a);
        }
    }
}
