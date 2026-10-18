package com.sena.cipa.vista;

import com.sena.cipa.modelo.Ficha;
import com.sena.cipa.modelo.SesionFormacion;

import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class SesionFormacionVista extends VentanaCrud {

    public JComboBox<Ficha> cmbFicha = new JComboBox<>();
    public JTextField txtFecha = new JTextField();
    public JTextField txtHoraInicio = new JTextField();
    public JTextField txtHoraFin = new JTextField();
    public JTextField txtTema = new JTextField();

    public SesionFormacionVista() {
        super("Sesiones de formacion",
                new String[]{"Id", "Ficha", "Fecha", "Inicio", "Fin", "Tema"},
                "Buscar por tema", "Eliminar");
        setSubtitulo("Clases dictadas a cada ficha");
        agregarCampo("Ficha", cmbFicha);
        agregarCampo("Fecha (aaaa-mm-dd)", txtFecha);
        agregarCampo("Hora de inicio (HH:mm)", txtHoraInicio);
        agregarCampo("Hora de fin (HH:mm)", txtHoraFin);
        agregarCampo("Tema", txtTema);
        terminar();
        anchoColumna(0, 40);
        anchoColumna(1, 80);
        anchoColumna(2, 90);
        anchoColumna(3, 70);
        anchoColumna(4, 70);
    }

    public void mostrar(SesionFormacion s) {
        setIdSeleccionado(s.getId());
        elegir(cmbFicha, Ficha::getId, s.getFichaId());
        txtFecha.setText(s.getFecha());
        txtHoraInicio.setText(soloHora(s.getHoraInicio()));
        txtHoraFin.setText(soloHora(s.getHoraFin()));
        txtTema.setText(s.getTema());
    }

    public void limpiar() {
        limpiarSeleccion();
        if (cmbFicha.getItemCount() > 0) {
            cmbFicha.setSelectedIndex(0);
        }
        txtFecha.setText("");
        txtHoraInicio.setText("");
        txtHoraFin.setText("");
        txtTema.setText("");
    }

    public void cargarTabla(List<SesionFormacion> lista) {
        vaciarTabla();
        for (SesionFormacion s : lista) {
            agregarFila(s.getId(), s.getNumeroFicha(), s.getFecha(), s.getHoraInicio(),
                    s.getHoraFin(), s.getTema());
        }
    }

    // de "HH:mm:00" que devuelve la base se muestra solo "HH:mm" en el formulario
    private String soloHora(String hora) {
        if (hora == null) {
            return "";
        }
        return hora.length() >= 5 ? hora.substring(0, 5) : hora;
    }
}
