package com.sena.cipa.controlador;

import com.sena.cipa.modelo.Ficha;
import com.sena.cipa.modelo.FichaDAO;
import com.sena.cipa.modelo.Programa;
import com.sena.cipa.modelo.ProgramaDAOImpl;
import com.sena.cipa.vista.FichaVista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

// CONTROLADOR: recibe los clics de la vista, valida, llama al DAO (modelo) y actualiza la vista.
public class FichaControlador implements ActionListener {

    private FichaVista vista;
    private FichaDAO dao;

    public FichaControlador(FichaVista vista, FichaDAO dao) {
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

        // la lista de programas se llena con el DAO de programas
        vista.llenarCombo(vista.cmbPrograma, new ProgramaDAOImpl().listarActivos());
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

    private Ficha leerFormulario() {
        String numero = vista.txtNumero.getText().trim();
        String inicio = vista.txtFechaInicio.getText().trim();
        String fin = vista.txtFechaFin.getText().trim();
        String ambiente = vista.txtAmbiente.getText().trim();

        if (vista.cmbPrograma.getItemCount() == 0) {
            vista.mostrarMensaje("Todavia no hay programas de formacion.\nRegistra primero un programa.");
            return null;
        }
        if (numero.isEmpty() || ambiente.isEmpty()) {
            vista.mostrarMensaje("Completa el numero de ficha y el ambiente");
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

        Ficha f = new Ficha();
        f.setNumero(numero);
        f.setProgramaId(((Programa) vista.cmbPrograma.getSelectedItem()).getId());
        f.setJornada((String) vista.cmbJornada.getSelectedItem());
        f.setFechaInicio(inicio);
        f.setFechaFin(fin);
        f.setAmbiente(ambiente);
        f.setEstado((String) vista.cmbEstado.getSelectedItem());
        return f;
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
        Ficha f = leerFormulario();
        if (f == null) {
            return;
        }
        if (dao.registrar(f)) {
            vista.estado("Ficha registrada");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo registrar. Revisa que el numero de ficha no este repetido");
        }
    }

    private void buscar() {
        String numero = vista.txtNumero.getText().trim();
        if (numero.isEmpty()) {
            vista.mostrarMensaje("Escribe el numero de ficha que quieres buscar");
            return;
        }
        Ficha f = dao.buscarPorNumero(numero);
        if (f == null) {
            vista.mostrarMensaje("No existe una ficha con el numero " + numero);
        } else {
            vista.mostrar(f);
            vista.estado("Ficha " + numero + " encontrada");
        }
    }

    private void modificar() {
        if (vista.getIdSeleccionado() == 0) {
            vista.mostrarMensaje("Primero selecciona una ficha de la tabla o buscala por numero");
            return;
        }
        Ficha f = leerFormulario();
        if (f == null) {
            return;
        }
        f.setId(vista.getIdSeleccionado());
        if (dao.actualizar(f)) {
            vista.estado("Ficha modificada");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo modificar. Revisa que el numero de ficha no este repetido");
        }
    }

    private void cancelar() {
        int id = vista.getIdSeleccionado();
        if (id == 0) {
            vista.mostrarMensaje("Primero selecciona una ficha de la tabla o buscala por numero");
            return;
        }
        if (!vista.confirmar("Seguro que quieres cancelar esta ficha?")) {
            return;
        }
        if (dao.eliminar(id)) {
            vista.estado("Ficha cancelada");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo cancelar la ficha");
        }
    }

    private void seleccionarFila() {
        int id = vista.idDeFilaSeleccionada();
        if (id == 0) {
            return;
        }
        Ficha f = dao.leerPorId(id);
        if (f != null) {
            vista.mostrar(f);
        }
    }
}
