package com.sena.cipa.controlador;

import com.sena.cipa.modelo.Asignacion;
import com.sena.cipa.modelo.AsignacionDAO;
import com.sena.cipa.modelo.Ficha;
import com.sena.cipa.modelo.FichaDAOImpl;
import com.sena.cipa.modelo.Instructor;
import com.sena.cipa.modelo.InstructorDAOImpl;
import com.sena.cipa.vista.AsignacionVista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

// CONTROLADOR: recibe los clics de la vista, valida, llama al DAO (modelo) y actualiza la vista.
public class AsignacionControlador implements ActionListener {

    private AsignacionVista vista;
    private AsignacionDAO dao;

    public AsignacionControlador(AsignacionVista vista, AsignacionDAO dao) {
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

        // cualquier ficha puede tener instructor, pero solo instructores activos
        vista.llenarCombo(vista.cmbFicha, new FichaDAOImpl().listarTodos());
        vista.llenarCombo(vista.cmbInstructor, new InstructorDAOImpl().listarActivos());
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

    private Asignacion leerFormulario() {
        String inicio = vista.txtFechaInicio.getText().trim();
        String fin = vista.txtFechaFin.getText().trim();

        if (vista.cmbFicha.getItemCount() == 0) {
            vista.mostrarMensaje("Primero registra una ficha");
            return null;
        }
        if (vista.cmbInstructor.getItemCount() == 0) {
            vista.mostrarMensaje("Primero registra un instructor");
            return null;
        }
        if (!fechaValida(inicio) || !fechaValida(fin)) {
            vista.mostrarMensaje("Las fechas deben existir y escribirse como aaaa-mm-dd");
            return null;
        }
        if (LocalDate.parse(fin).isBefore(LocalDate.parse(inicio))) {
            vista.mostrarMensaje("La fecha de fin no puede ser anterior a la fecha de inicio");
            return null;
        }

        Asignacion a = new Asignacion();
        a.setFichaId(((Ficha) vista.cmbFicha.getSelectedItem()).getId());
        a.setInstructorId(((Instructor) vista.cmbInstructor.getSelectedItem()).getId());
        a.setFechaInicio(inicio);
        a.setFechaFin(fin);
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
        Asignacion a = leerFormulario();
        if (a == null) {
            return;
        }
        if (dao.registrar(a)) {
            vista.estado("Asignacion registrada");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo registrar la asignacion");
        }
    }

    // busca por el numero de ficha que se escribe en el cuadro de dialogo
    private void buscar() {
        String numero = vista.pedirTexto("Numero de la ficha:");
        if (numero.isEmpty()) {
            return;
        }
        vista.cargarTabla(dao.listarPorFicha(numero));
        vista.estado("Asignaciones de la ficha " + numero);
    }

    private void modificar() {
        if (vista.getIdSeleccionado() == 0) {
            vista.mostrarMensaje("Primero selecciona una asignacion de la tabla o buscala por ficha");
            return;
        }
        Asignacion a = leerFormulario();
        if (a == null) {
            return;
        }
        a.setId(vista.getIdSeleccionado());
        if (dao.actualizar(a)) {
            vista.estado("Asignacion modificada");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo modificar la asignacion");
        }
    }

    private void eliminar() {
        int id = vista.getIdSeleccionado();
        if (id == 0) {
            vista.mostrarMensaje("Primero selecciona una asignacion de la tabla o buscala por ficha");
            return;
        }
        if (!vista.confirmar("Seguro que quieres eliminar esta asignacion?")) {
            return;
        }
        if (dao.eliminar(id)) {
            vista.estado("Asignacion eliminada");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo eliminar la asignacion");
        }
    }

    private void seleccionarFila() {
        int id = vista.idDeFilaSeleccionada();
        if (id == 0) {
            return;
        }
        Asignacion a = dao.leerPorId(id);
        if (a != null) {
            vista.mostrar(a);
        }
    }
}
