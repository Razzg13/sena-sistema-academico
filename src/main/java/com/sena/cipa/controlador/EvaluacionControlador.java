package com.sena.cipa.controlador;

import com.sena.cipa.modelo.ActividadEvaluativa;
import com.sena.cipa.modelo.ActividadEvaluativaDAOImpl;
import com.sena.cipa.modelo.Evaluacion;
import com.sena.cipa.modelo.EvaluacionDAO;
import com.sena.cipa.modelo.Matricula;
import com.sena.cipa.modelo.MatriculaDAOImpl;
import com.sena.cipa.vista.EvaluacionVista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

// CONTROLADOR: recibe los clics de la vista, valida, llama al DAO (modelo) y actualiza la vista.
public class EvaluacionControlador implements ActionListener {

    private EvaluacionVista vista;
    private EvaluacionDAO dao;

    public EvaluacionControlador(EvaluacionVista vista, EvaluacionDAO dao) {
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

        vista.llenarCombo(vista.cmbActividad, new ActividadEvaluativaDAOImpl().listarTodos());
        vista.llenarCombo(vista.cmbMatricula, new MatriculaDAOImpl().listarActivas());
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

    private Evaluacion leerFormulario() {
        if (vista.cmbActividad.getItemCount() == 0) {
            vista.mostrarMensaje("Todavia no hay actividades evaluativas.\nRegistra primero una actividad.");
            return null;
        }
        if (vista.cmbMatricula.getItemCount() == 0) {
            vista.mostrarMensaje("Todavia no hay matriculas activas.\nRegistra primero una matricula.");
            return null;
        }
        String nota = vista.txtCalificacion.getText().trim().replace(',', '.');
        String fecha = vista.txtFecha.getText().trim();

        if (!nota.isEmpty()) {
            try {
                double valor = Double.parseDouble(nota);
                if (valor < 0 || valor > 5) {
                    vista.mostrarMensaje("La calificacion debe estar entre 0.00 y 5.00");
                    return null;
                }
            } catch (NumberFormatException ex) {
                vista.mostrarMensaje("La calificacion debe ser un numero, por ejemplo 4.50");
                return null;
            }
        }
        try {
            LocalDate.parse(fecha);
        } catch (DateTimeParseException ex) {
            vista.mostrarMensaje("La fecha de evaluacion debe escribirse como aaaa-mm-dd");
            return null;
        }

        Evaluacion e = new Evaluacion();
        e.setActividadId(((ActividadEvaluativa) vista.cmbActividad.getSelectedItem()).getId());
        e.setMatriculaId(((Matricula) vista.cmbMatricula.getSelectedItem()).getId());
        e.setCalificacion(nota);
        e.setJuicio((String) vista.cmbJuicio.getSelectedItem());
        e.setRetroalimentacion(vista.txtRetroalimentacion.getText().trim());
        e.setFechaEvaluacion(fecha);
        return e;
    }

    private void registrar() {
        Evaluacion e = leerFormulario();
        if (e == null) {
            return;
        }
        if (dao.registrar(e)) {
            vista.estado("Evaluacion registrada");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo registrar. Revisa que el aprendiz no tenga ya una nota "
                    + "en esa actividad");
        }
    }

    private void buscar() {
        String documento = vista.pedirTexto("Documento del aprendiz:");
        if (documento.isEmpty()) {
            return;
        }
        vista.cargarTabla(dao.listarPorDocumento(documento));
        vista.estado("Evaluaciones del documento " + documento);
    }

    private void modificar() {
        if (vista.getIdSeleccionado() == 0) {
            vista.mostrarMensaje("Primero selecciona una evaluacion de la tabla");
            return;
        }
        Evaluacion e = leerFormulario();
        if (e == null) {
            return;
        }
        e.setId(vista.getIdSeleccionado());
        if (dao.actualizar(e)) {
            vista.estado("Evaluacion modificada");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo modificar. Revisa que el aprendiz no tenga ya una nota "
                    + "en esa actividad");
        }
    }

    private void eliminar() {
        int id = vista.getIdSeleccionado();
        if (id == 0) {
            vista.mostrarMensaje("Primero selecciona una evaluacion de la tabla");
            return;
        }
        if (!vista.confirmar("Seguro que quieres eliminar esta evaluacion?")) {
            return;
        }
        if (dao.eliminar(id)) {
            vista.estado("Evaluacion eliminada");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo eliminar la evaluacion");
        }
    }

    private void seleccionarFila() {
        int id = vista.idDeFilaSeleccionada();
        if (id == 0) {
            return;
        }
        Evaluacion e = dao.leerPorId(id);
        if (e != null) {
            vista.mostrar(e);
        }
    }
}
