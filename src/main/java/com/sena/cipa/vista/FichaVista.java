package com.sena.cipa.vista;

import com.sena.cipa.modelo.Ficha;
import com.sena.cipa.modelo.Programa;

import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class FichaVista extends VentanaCrud {

    public JTextField txtNumero = new JTextField();
    public JComboBox<Programa> cmbPrograma = new JComboBox<>();
    public JComboBox<String> cmbJornada = new JComboBox<>(new String[]{
        "MANANA", "TARDE", "NOCHE", "MIXTA", "FIN_DE_SEMANA"});
    public JTextField txtFechaInicio = new JTextField();
    public JTextField txtFechaFin = new JTextField();
    public JTextField txtAmbiente = new JTextField();
    public JComboBox<String> cmbEstado = new JComboBox<>(new String[]{
        "EN_EJECUCION", "APLAZADA", "TERMINADA", "CANCELADA"});

    public FichaVista() {
        super("Fichas de formacion",
                new String[]{"Id", "Numero", "Programa", "Jornada", "Inicio", "Fin", "Ambiente", "Estado"},
                "Buscar por numero", "Cancelar ficha");
        setSubtitulo("Grupos de aprendices por programa");
        agregarCampo("Numero de ficha", txtNumero);
        agregarCampo("Programa", cmbPrograma);
        agregarCampo("Jornada", cmbJornada);
        agregarCampo("Fecha de inicio (aaaa-mm-dd)", txtFechaInicio);
        agregarCampo("Fecha de fin (aaaa-mm-dd)", txtFechaFin);
        agregarCampo("Ambiente", txtAmbiente);
        agregarCampo("Estado", cmbEstado);
        terminar();
        anchoColumna(0, 40);
        anchoColumna(1, 80);
        anchoColumna(2, 220);
        anchoColumna(7, 110);
    }

    public void mostrar(Ficha f) {
        setIdSeleccionado(f.getId());
        txtNumero.setText(f.getNumero());
        elegir(cmbPrograma, Programa::getId, f.getProgramaId());
        cmbJornada.setSelectedItem(f.getJornada());
        txtFechaInicio.setText(f.getFechaInicio());
        txtFechaFin.setText(f.getFechaFin());
        txtAmbiente.setText(f.getAmbiente());
        cmbEstado.setSelectedItem(f.getEstado());
    }

    public void limpiar() {
        limpiarSeleccion();
        txtNumero.setText("");
        if (cmbPrograma.getItemCount() > 0) {
            cmbPrograma.setSelectedIndex(0);
        }
        cmbJornada.setSelectedIndex(0);
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
        txtAmbiente.setText("");
        cmbEstado.setSelectedIndex(0);
    }

    public void cargarTabla(List<Ficha> lista) {
        vaciarTabla();
        for (Ficha f : lista) {
            agregarFila(f.getId(), f.getNumero(), f.getNombrePrograma(), f.getJornada(),
                    f.getFechaInicio(), f.getFechaFin(), f.getAmbiente(), f.getEstado());
        }
    }
}
