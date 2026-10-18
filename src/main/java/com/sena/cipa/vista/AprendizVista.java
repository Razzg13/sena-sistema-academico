package com.sena.cipa.vista;

import com.sena.cipa.modelo.Aprendiz;
import com.sena.cipa.modelo.Usuario;

import java.util.List;
import javax.swing.JComboBox;

public class AprendizVista extends VentanaCrud {

    public JComboBox<Usuario> cmbUsuario = new JComboBox<>();
    public JComboBox<String> cmbEstadoFormativo = new JComboBox<>(new String[]{
        "EN_FORMACION", "APLAZADO", "RETIRADO", "CERTIFICADO"});
    public JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"});

    public AprendizVista() {
        super("Aprendices",
                new String[]{"Id", "Documento", "Nombre", "Estado formativo", "Estado"},
                "Buscar por documento", "Inactivar");
        setSubtitulo("Datos propios del aprendiz");
        agregarCampo("Usuario (rol APRENDIZ)", cmbUsuario);
        agregarCampo("Estado formativo", cmbEstadoFormativo);
        agregarCampo("Estado", cmbEstado);
        terminar();
        anchoColumna(0, 40);
        anchoColumna(1, 100);
        anchoColumna(2, 220);
        anchoColumna(3, 140);
        anchoColumna(4, 90);
    }

    public void mostrar(Aprendiz a) {
        setIdSeleccionado(a.getId());
        elegir(cmbUsuario, Usuario::getId, a.getUsuarioId());
        cmbEstadoFormativo.setSelectedItem(a.getEstadoFormativo());
        cmbEstado.setSelectedItem(a.getEstado());
    }

    public void limpiar() {
        limpiarSeleccion();
        if (cmbUsuario.getItemCount() > 0) {
            cmbUsuario.setSelectedIndex(0);
        }
        cmbEstadoFormativo.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
    }

    public void cargarTabla(List<Aprendiz> lista) {
        vaciarTabla();
        for (Aprendiz a : lista) {
            agregarFila(a.getId(), a.getDocumento(), a.getNombreCompleto(), a.getEstadoFormativo(), a.getEstado());
        }
    }
}
