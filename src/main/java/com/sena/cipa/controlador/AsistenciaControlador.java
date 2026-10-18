package com.sena.cipa.controlador;

import com.sena.cipa.modelo.Asistencia;
import com.sena.cipa.modelo.AsistenciaDAO;
import com.sena.cipa.modelo.Matricula;
import com.sena.cipa.modelo.MatriculaDAOImpl;
import com.sena.cipa.modelo.SesionFormacion;
import com.sena.cipa.modelo.SesionFormacionDAOImpl;
import com.sena.cipa.vista.AsistenciaVista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// CONTROLADOR: recibe los clics de la vista, valida, llama al DAO (modelo) y actualiza la vista.
public class AsistenciaControlador implements ActionListener {

    private AsistenciaVista vista;
    private AsistenciaDAO dao;

    public AsistenciaControlador(AsistenciaVista vista, AsistenciaDAO dao) {
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

        // las listas se llenan con los DAO de sesiones y de matriculas
        vista.llenarCombo(vista.cmbSesion, new SesionFormacionDAOImpl().listarTodos());
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

    private Asistencia leerFormulario() {
        if (vista.cmbSesion.getItemCount() == 0) {
            vista.mostrarMensaje("Todavia no hay sesiones de formacion.\nRegistra primero una sesion.");
            return null;
        }
        if (vista.cmbMatricula.getItemCount() == 0) {
            vista.mostrarMensaje("Todavia no hay matriculas activas.\nRegistra primero una matricula.");
            return null;
        }
        Asistencia a = new Asistencia();
        a.setSesionId(((SesionFormacion) vista.cmbSesion.getSelectedItem()).getId());
        a.setMatriculaId(((Matricula) vista.cmbMatricula.getSelectedItem()).getId());
        a.setEstado((String) vista.cmbEstado.getSelectedItem());
        a.setObservacion(vista.txtObservacion.getText().trim());
        return a;
    }

    private void registrar() {
        Asistencia a = leerFormulario();
        if (a == null) {
            return;
        }
        if (dao.registrar(a)) {
            vista.estado("Asistencia registrada");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo registrar. Revisa que el aprendiz no tenga ya una asistencia "
                    + "en esa sesion");
        }
    }

    private void buscar() {
        String documento = vista.pedirTexto("Documento del aprendiz:");
        if (documento.isEmpty()) {
            return;
        }
        vista.cargarTabla(dao.listarPorDocumento(documento));
        vista.estado("Asistencias del documento " + documento);
    }

    private void modificar() {
        if (vista.getIdSeleccionado() == 0) {
            vista.mostrarMensaje("Primero selecciona una asistencia de la tabla");
            return;
        }
        Asistencia a = leerFormulario();
        if (a == null) {
            return;
        }
        a.setId(vista.getIdSeleccionado());
        if (dao.actualizar(a)) {
            vista.estado("Asistencia modificada");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo modificar. Revisa que el aprendiz no tenga ya una asistencia "
                    + "en esa sesion");
        }
    }

    private void eliminar() {
        int id = vista.getIdSeleccionado();
        if (id == 0) {
            vista.mostrarMensaje("Primero selecciona una asistencia de la tabla");
            return;
        }
        if (!vista.confirmar("Seguro que quieres eliminar esta asistencia?")) {
            return;
        }
        if (dao.eliminar(id)) {
            vista.estado("Asistencia eliminada");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo eliminar la asistencia");
        }
    }

    private void seleccionarFila() {
        int id = vista.idDeFilaSeleccionada();
        if (id == 0) {
            return;
        }
        Asistencia a = dao.leerPorId(id);
        if (a != null) {
            vista.mostrar(a);
        }
    }
}
