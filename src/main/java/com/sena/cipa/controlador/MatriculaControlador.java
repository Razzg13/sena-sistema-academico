package com.sena.cipa.controlador;

import com.sena.cipa.modelo.Aprendiz;
import com.sena.cipa.modelo.AprendizDAOImpl;
import com.sena.cipa.modelo.Ficha;
import com.sena.cipa.modelo.FichaDAOImpl;
import com.sena.cipa.modelo.Matricula;
import com.sena.cipa.modelo.MatriculaDAO;
import com.sena.cipa.vista.MatriculaVista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

// CONTROLADOR: recibe los clics de la vista, valida, llama al DAO (modelo) y actualiza la vista.
public class MatriculaControlador implements ActionListener {

    private MatriculaVista vista;
    private MatriculaDAO dao;

    public MatriculaControlador(MatriculaVista vista, MatriculaDAO dao) {
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

        // el aprendiz debe estar activo y la ficha tiene que estar en ejecucion
        vista.llenarCombo(vista.cmbAprendiz, new AprendizDAOImpl().listarActivos());
        vista.llenarCombo(vista.cmbFicha, new FichaDAOImpl().listarEnEjecucion());
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
            cancelar();
        } else if (origen == vista.btnLimpiar) {
            vista.limpiar();
        }
    }

    private Matricula leerFormulario() {
        String fecha = vista.txtFechaMatricula.getText().trim();

        if (vista.cmbAprendiz.getItemCount() == 0) {
            vista.mostrarMensaje("Primero registra un aprendiz");
            return null;
        }
        if (vista.cmbFicha.getItemCount() == 0) {
            vista.mostrarMensaje("Primero registra una ficha en ejecucion");
            return null;
        }
        if (!fechaValida(fecha)) {
            vista.mostrarMensaje("La fecha de matricula debe existir y escribirse como aaaa-mm-dd");
            return null;
        }

        Matricula m = new Matricula();
        m.setAprendizId(((Aprendiz) vista.cmbAprendiz.getSelectedItem()).getId());
        m.setFichaId(((Ficha) vista.cmbFicha.getSelectedItem()).getId());
        m.setFechaMatricula(fecha);
        m.setEstado((String) vista.cmbEstado.getSelectedItem());
        return m;
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
        Matricula m = leerFormulario();
        if (m == null) {
            return;
        }
        if (dao.registrar(m)) {
            vista.estado("Matricula registrada");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo registrar. Revisa que el aprendiz no este ya matriculado en esa ficha");
        }
    }

    // busca por el documento del aprendiz que se escribe en el cuadro de dialogo
    private void buscar() {
        String documento = vista.pedirTexto("Documento del aprendiz:");
        if (documento.isEmpty()) {
            return;
        }
        vista.cargarTabla(dao.listarPorDocumento(documento));
        vista.estado("Matriculas del documento " + documento);
    }

    private void modificar() {
        if (vista.getIdSeleccionado() == 0) {
            vista.mostrarMensaje("Primero selecciona una matricula de la tabla o buscala por documento");
            return;
        }
        Matricula m = leerFormulario();
        if (m == null) {
            return;
        }
        m.setId(vista.getIdSeleccionado());
        if (dao.actualizar(m)) {
            vista.estado("Matricula modificada");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo modificar. Revisa que el aprendiz no este ya matriculado en esa ficha");
        }
    }

    private void cancelar() {
        int id = vista.getIdSeleccionado();
        if (id == 0) {
            vista.mostrarMensaje("Primero selecciona una matricula de la tabla o buscala por documento");
            return;
        }
        if (!vista.confirmar("Seguro que quieres cancelar esta matricula?")) {
            return;
        }
        if (dao.eliminar(id)) {
            vista.estado("Matricula cancelada");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo cancelar la matricula");
        }
    }

    private void seleccionarFila() {
        int id = vista.idDeFilaSeleccionada();
        if (id == 0) {
            return;
        }
        Matricula m = dao.leerPorId(id);
        if (m != null) {
            vista.mostrar(m);
        }
    }
}
