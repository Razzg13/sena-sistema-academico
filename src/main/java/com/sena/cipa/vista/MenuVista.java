package com.sena.cipa.vista;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

// VISTA: menu principal. Muestra una tarjeta por cada modulo del sistema, el estado
// de la conexion con la base de datos y el boton para salir.
// No sabe que hace cada modulo: solo avisa cual se eligio.
public class MenuVista extends JFrame {

    private final JPanel rejilla = new JPanel(new GridLayout(0, 3, 12, 12));
    private final List<Tarjeta> tarjetas = new ArrayList<>();
    private final List<Consumer<String>> escuchas = new ArrayList<>();
    private final JLabel lblBaseDatos = new JLabel("Comprobando la base de datos...", SwingConstants.CENTER);

    public MenuVista() {
        setTitle("SENA - Sistema Academico");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Estilo.FONDO);
        java.awt.Image icono = Estilo.icono(64);
        if (icono != null) {
            setIconImage(icono);
        }

        add(Estilo.encabezado("SENA | Sistema Academico",
                "Selecciona el modulo que quieres administrar"), BorderLayout.NORTH);

        rejilla.setBackground(Estilo.FONDO);
        rejilla.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        // las tarjetas conservan su alto: la rejilla no se estira para llenar la ventana
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Estilo.FONDO);
        contenedor.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        contenedor.add(rejilla, BorderLayout.NORTH);
        add(contenedor, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout());
        pie.setBackground(Estilo.BLANCO);
        pie.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Estilo.BORDE),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        lblBaseDatos.setFont(Estilo.fuente(12, true));
        lblBaseDatos.setForeground(Estilo.GRIS);
        pie.add(lblBaseDatos, BorderLayout.CENTER);
        add(pie, BorderLayout.SOUTH);

        setSize(760, 560);
        setLocationRelativeTo(null);
    }

    // agrega una tarjeta al menu; clave es el nombre interno del modulo
    public void agregarModulo(String clave, String titulo, String descripcion) {
        Tarjeta tarjeta = new Tarjeta(clave, titulo, descripcion);
        tarjetas.add(tarjeta);
        rejilla.add(tarjeta);
    }

    // el controlador se suscribe para enterarse de cual tarjeta se oprimio
    public void alSeleccionarModulo(Consumer<String> accion) {
        escuchas.add(accion);
    }

    public void habilitarModulos(boolean habilitar) {
        for (Tarjeta tarjeta : tarjetas) {
            tarjeta.habilitar(habilitar);
        }
    }

    // el controlador le dice a la vista si hay conexion con MySQL
    public void mostrarEstadoBaseDatos(boolean conectada, String base, String error) {
        if (conectada) {
            lblBaseDatos.setText("Base de datos: " + base + "  -  CONECTADA");
            lblBaseDatos.setForeground(Estilo.VERDE_OSCURO);
        } else {
            lblBaseDatos.setText("Base de datos: " + base + "  -  SIN CONEXION");
            lblBaseDatos.setForeground(Estilo.ROJO);
            JOptionPane.showMessageDialog(this,
                    "No se pudo conectar a la base de datos '" + base + "'.\n"
                    + "Revisa que MySQL este encendido y que la clave sea la correcta.\n\n" + error,
                    "Sin conexion", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void avisar(String clave) {
        for (Consumer<String> escucha : escuchas) {
            escucha.accept(clave);
        }
    }

    // TARJETA: un recuadro con el nombre del modulo y una linea que lo explica
    private class Tarjeta extends JPanel {

        private final String clave;
        private boolean habilitada = true;

        Tarjeta(String clave, String titulo, String descripcion) {
            this.clave = clave;
            setLayout(new BorderLayout(0, 2));
            setBackground(Estilo.BLANCO);
            setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Estilo.BORDE, 1, true),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(220, 58));

            JLabel nombre = Estilo.etiqueta(titulo, 13.5f, true, Estilo.VERDE_OSCURO);
            JLabel detalle = Estilo.etiqueta(descripcion, 11.5f, false, Estilo.GRIS);
            add(nombre, BorderLayout.CENTER);
            add(detalle, BorderLayout.SOUTH);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (habilitada) {
                        avisar(Tarjeta.this.clave);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (habilitada) {
                        setBackground(Estilo.VERDE_CLARO);
                        pintarHijos(Estilo.VERDE_CLARO);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(Estilo.BLANCO);
                    pintarHijos(Estilo.BLANCO);
                }
            });
        }

        private void pintarHijos(java.awt.Color fondo) {
            for (java.awt.Component hijo : getComponents()) {
                hijo.setBackground(fondo);
            }
            repaint();
        }

        void habilitar(boolean valor) {
            habilitada = valor;
            setCursor(new Cursor(valor ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            setBackground(valor ? Estilo.BLANCO : new java.awt.Color(0xEDEFEA));
            pintarHijos(valor ? Estilo.BLANCO : new java.awt.Color(0xEDEFEA));
        }
    }
}
