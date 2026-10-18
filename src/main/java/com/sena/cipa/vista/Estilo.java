package com.sena.cipa.vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;

// ESTILO: los colores, las fuentes y los componentes con el mismo aspecto para todas
// las ventanas. Es solo presentacion: no tiene logica ni habla con la base de datos.
public final class Estilo {

    public static final Color VERDE = new Color(0x39A900);
    public static final Color VERDE_OSCURO = new Color(0x2B7A00);
    public static final Color VERDE_CLARO = new Color(0xE8F5E0);
    public static final Color VERDE_SELECCION = new Color(0xCFE8BE);
    public static final Color GRIS = new Color(0x64735C);
    public static final Color TEXTO = new Color(0x1B2A17);
    public static final Color FONDO = new Color(0xF3F7F0);
    public static final Color BLANCO = new Color(0xFFFFFF);
    public static final Color RAYA = new Color(0xF6FAF3);
    public static final Color BORDE = new Color(0xD6E0CF);
    public static final Color ROJO = new Color(0xC0392B);
    public static final String FUENTE = "Segoe UI";

    // tipos de boton
    public static final int PRINCIPAL = 1;
    public static final int SECUNDARIO = 2;
    public static final int PELIGRO = 3;

    private Estilo() {
    }

    public static Font fuente(float tamano, boolean negrita) {
        return new Font(FUENTE, negrita ? Font.BOLD : Font.PLAIN, Math.round(tamano));
    }

    // deja el aspecto general del programa (fuentes y ventanas de dialogo)
    public static void aplicar() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // si el sistema no responde, se sigue con el aspecto por defecto de Java
        }
        UIManager.put("Label.font", fuente(12, false));
        UIManager.put("Button.font", fuente(12.5f, true));
        UIManager.put("TextField.font", fuente(12, false));
        UIManager.put("PasswordField.font", fuente(12, false));
        UIManager.put("ComboBox.font", fuente(12, false));
        UIManager.put("Table.font", fuente(12, false));
        UIManager.put("TableHeader.font", fuente(12, true));
        UIManager.put("OptionPane.messageFont", fuente(12.5f, false));
        UIManager.put("OptionPane.buttonFont", fuente(12, true));
        UIManager.put("ToolTip.font", fuente(11.5f, false));
    }

    // ------------------------------------------------------------------ botones
    public static JButton boton(String texto, int tipo) {
        return new Boton(texto, tipo);
    }

    // Boton redondeado y plano, para que se vea igual en cualquier sistema operativo
    public static class Boton extends JButton {

        private final Color relleno;
        private final Color rellenoEncima;
        private final Color colorTexto;
        private final Color borde;

        public Boton(String texto, int tipo) {
            super(texto);
            setFont(fuente(12.5f, true));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
            if (tipo == PRINCIPAL) {
                relleno = VERDE;
                rellenoEncima = VERDE_OSCURO;
                colorTexto = BLANCO;
                borde = null;
            } else if (tipo == PELIGRO) {
                relleno = BLANCO;
                rellenoEncima = new Color(0xFBECEA);
                colorTexto = ROJO;
                borde = new Color(0xE8C4C0);
            } else {
                relleno = BLANCO;
                rellenoEncima = VERDE_CLARO;
                colorTexto = TEXTO;
                borde = BORDE;
            }
            setForeground(colorTexto);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isRollover() && isEnabled() ? rellenoEncima : relleno);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            if (borde != null) {
                g2.setColor(borde);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
            g2.dispose();
            super.paintComponent(g);   // el texto lo dibuja el propio boton
        }
    }

    // ----------------------------------------------------------------- etiquetas
    public static JLabel etiqueta(String texto, float tamano, boolean negrita, Color color) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(fuente(tamano, negrita));
        etiqueta.setForeground(color);
        return etiqueta;
    }

    // -------------------------------------------------------------------- paneles
    // tarjeta blanca con borde suave, para agrupar el formulario y la tabla
    public static JPanel tarjeta() {
        JPanel panel = new JPanel();
        panel.setBackground(BLANCO);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDE, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 12, 12)));
        return panel;
    }

    // franja verde del titulo, igual en todas las ventanas, con el logo del SENA
    public static JPanel encabezado(String titulo, String subtitulo) {
        JPanel panel = new JPanel(new java.awt.BorderLayout(10, 0));
        panel.setBackground(VERDE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        izquierda.setOpaque(false);
        Image logo = logo(38, true);
        if (logo != null) {
            izquierda.add(new JLabel(new ImageIcon(logo)));
        }
        izquierda.add(etiqueta(titulo, 17, true, BLANCO));
        panel.add(izquierda, java.awt.BorderLayout.WEST);

        if (subtitulo != null && !subtitulo.isEmpty()) {
            JLabel segundo = etiqueta(subtitulo, 11.5f, false, new Color(0xE4F5D8));
            segundo.setHorizontalAlignment(SwingConstants.RIGHT);
            panel.add(segundo, java.awt.BorderLayout.EAST);
        }
        return panel;
    }

    // --------------------------------------------------------------- el logo
    // lee el logo del SENA que esta en src\main\resources y lo ajusta al alto pedido
    public static Image logo(int alto, boolean blanco) {
        try {
            URL url = Estilo.class.getResource(blanco ? "/logo_sena_blanco.png" : "/logo_sena.png");
            if (url == null) {
                return null;   // si falta el archivo, la ventana se dibuja sin logo
            }
            Image original = ImageIO.read(url);
            int ancho = Math.max(1, Math.round(alto * original.getWidth(null)
                    / (float) original.getHeight(null)));
            return original.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        } catch (Exception ex) {
            return null;
        }
    }

    // icono pequeno para la barra de tareas y el borde de las ventanas
    public static Image icono(int lado) {
        return logo(lado, false);
    }

    // -------------------------------------------------------------------- campos
    // da el mismo alto, fuente y borde a los campos de un formulario
    public static void estiloCampo(JComponent campo) {
        campo.setFont(fuente(12, false));
        if (campo instanceof JTextComponent) {
            JTextComponent texto = (JTextComponent) campo;
            texto.setBorder(bordeCampo(false));
            texto.setBackground(BLANCO);
            texto.setPreferredSize(new Dimension(260, 30));
            texto.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    texto.setBorder(bordeCampo(true));
                }

                @Override
                public void focusLost(FocusEvent e) {
                    texto.setBorder(bordeCampo(false));
                }
            });
        } else if (campo instanceof JComboBox) {
            campo.setPreferredSize(new Dimension(260, 30));
            campo.setBackground(BLANCO);
        }
    }

    private static Border bordeCampo(boolean conFoco) {
        return BorderFactory.createCompoundBorder(
                new LineBorder(conFoco ? VERDE : BORDE, 1, true),
                BorderFactory.createEmptyBorder(3, 8, 3, 8));
    }

    // --------------------------------------------------------------------- tabla
    public static void estiloTabla(JTable tabla) {
        tabla.setRowHeight(28);
        tabla.setFont(fuente(12, false));
        tabla.setForeground(TEXTO);
        tabla.setBackground(BLANCO);
        tabla.setGridColor(new Color(0xE8EEE4));
        tabla.setShowVerticalLines(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(VERDE_SELECCION);
        tabla.setSelectionForeground(TEXTO);
        tabla.setFillsViewportHeight(true);
        // con pocas filas no aparece la barra de desplazamiento
        tabla.setPreferredScrollableViewportSize(new Dimension(600, 120));
        // las columnas se reparten el ancho de la ventana (no aparece barra horizontal)
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.setDefaultRenderer(Object.class, new RenderFilas());
        JTableHeader cabecera = tabla.getTableHeader();
        cabecera.setFont(fuente(12, true));
        cabecera.setPreferredSize(new Dimension(10, 32));
        cabecera.setReorderingAllowed(false);
        cabecera.setDefaultRenderer(new RenderCabecera());
    }

    // filas con rayas suaves para leer mejor la tabla
    private static class RenderFilas extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionado,
                boolean conFoco, int fila, int columna) {
            super.getTableCellRendererComponent(tabla, valor, seleccionado, conFoco, fila, columna);
            setFont(fuente(12, false));
            setOpaque(true);
            if (seleccionado) {
                setBackground(VERDE_SELECCION);
            } else {
                setBackground(fila % 2 == 0 ? BLANCO : RAYA);
            }
            setForeground(TEXTO);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            return this;
        }
    }

    // cabecera verde con letra blanca
    private static class RenderCabecera extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionado,
                boolean conFoco, int fila, int columna) {
            super.getTableCellRendererComponent(tabla, valor, seleccionado, conFoco, fila, columna);
            setOpaque(true);
            setBackground(VERDE);
            setForeground(BLANCO);
            setFont(fuente(12, true));
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            return this;
        }
    }
}
