package com.sena.cipa.controlador;

import com.sena.cipa.modelo.Ficha;
import com.sena.cipa.modelo.FichaDAOImpl;
import com.sena.cipa.modelo.SesionFormacion;
import com.sena.cipa.modelo.SesionFormacionDAO;
import com.sena.cipa.vista.SesionFormacionVista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

// CONTROLADOR: recibe los clics de la vista, valida, llama al DAO (modelo) y actualiza la vista.
public class SesionFormacionControlador implements ActionListener {

    private SesionFormacionVista vista;
    private SesionFormacionDAO dao;

    public SesionFormacionControlador(SesionFormacionVista vista, SesionFormacionDAO dao) {
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

    private SesionFormacion leerFormulario() {
        String fecha = vista.txtFecha.getText().trim();
        String inicio = vista.txtHoraInicio.getText().trim();
        String fin = vista.txtHoraFin.getText().trim();
        String tema = vista.txtTema.getText().trim();

        if (vista.cmbFicha.getItemCount() == 0) {
            vista.mostrarMensaje("Primero registra una ficha");
            return null;
        }
        if (tema.isEmpty()) {
            vista.mostrarMensaje("Completa el tema de la sesion");
            return null;
        }
        if (!fechaValida(fecha)) {
            vista.mostrarMensaje("La fecha debe existir y escribirse como aaaa-mm-dd");
            return null;
        }
        if (!horaValida(inicio) || !horaValida(fin)) {
            vista.mostrarMensaje("Las horas deben escribirse como HH:mm");
            return null;
        }
        if (!LocalTime.parse(fin).isAfter(LocalTime.parse(inicio))) {
            vista.mostrarMensaje("La hora de fin debe ser posterior a la hora de inicio");
            return null;
        }

        SesionFormacion s = new SesionFormacion();
        s.setFichaId(((Ficha) vista.cmbFicha.getSelectedItem()).getId());
        s.setFecha(fecha);
        s.setHoraInicio(normalizarHora(inicio));
        s.setHoraFin(normalizarHora(fin));
        s.setTema(tema);
        return s;
    }

    private boolean fechaValida(String texto) {
        try {
            LocalDate.parse(texto);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    private boolean horaValida(String texto) {
        try {
            LocalTime.parse(texto);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    // la base guarda la hora como TIME: se envia siempre "HH:mm:00"
    private String normalizarHora(String texto) {
        LocalTime hora = LocalTime.parse(texto);
        return String.format("%02d:%02d:00", hora.getHour(), hora.getMinute());
    }

    private void registrar() {
        SesionFormacion s = leerFormulario();
        if (s == null) {
            return;
        }
        if (dao.registrar(s)) {
            vista.estado("Sesion registrada");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo registrar la sesion. Revisa los datos de la ficha");
        }
    }

    private void buscar() {
        String tema = vista.txtTema.getText().trim();
        if (tema.isEmpty()) {
            vista.mostrarMensaje("Escribe el tema de la sesion que quieres buscar");
            return;
        }
        SesionFormacion s = dao.buscarPorTema(tema);
        if (s == null) {
            vista.mostrarMensaje("No existe una sesion con el tema " + tema);
        } else {
            vista.mostrar(s);
            vista.estado("Sesion encontrada: " + tema);
        }
    }

    private void modificar() {
        if (vista.getIdSeleccionado() == 0) {
            vista.mostrarMensaje("Primero selecciona una sesion de la tabla o buscala por tema");
            return;
        }
        SesionFormacion s = leerFormulario();
        if (s == null) {
            return;
        }
        s.setId(vista.getIdSeleccionado());
        if (dao.actualizar(s)) {
            vista.estado("Sesion modificada");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo modificar la sesion. Revisa los datos de la ficha");
        }
    }

    private void eliminar() {
        int id = vista.getIdSeleccionado();
        if (id == 0) {
            vista.mostrarMensaje("Primero selecciona una sesion de la tabla o buscala por tema");
            return;
        }
        if (!vista.confirmar("Seguro que quieres eliminar esta sesion?")) {
            return;
        }
        if (dao.eliminar(id)) {
            vista.estado("Sesion eliminada");
            vista.limpiar();
            vista.cargarTabla(dao.listarTodos());
        } else {
            vista.mostrarMensaje("No se pudo eliminar: revisa si la sesion ya tiene asistencias registradas");
        }
    }

    private void seleccionarFila() {
        int id = vista.idDeFilaSeleccionada();
        if (id == 0) {
            return;
        }
        SesionFormacion s = dao.leerPorId(id);
        if (s != null) {
            vista.mostrar(s);
        }
    }
}
