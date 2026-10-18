package com.sena.cipa.vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.function.ToIntFunction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

// VISTA BASE: el esqueleto que comparten las ventanas de mantenimiento
// (encabezado verde, tarjeta del formulario, botones, tabla y barra de estado).
// Cada ventana solo agrega sus campos y sus columnas.
// No tiene logica ni habla con la base de datos.
public class VentanaCrud extends JFrame {

    private int idSeleccionado = 0;
    private int cantidadCampos = 0;
    private int filasFormulario = 0;
    private int filasTabla = 0;
    private final java.util.List<String> etiquetas = new java.util.ArrayList<>();
    private final java.util.List<JComponent> campos = new java.util.ArrayList<>();

    public JPanel formulario = new JPanel(new GridBagLayout());

    public JButton btnRegistrar = Estilo.boton("Registrar", Estilo.PRINCIPAL);
    public JButton btnBuscar = Estilo.boton("Buscar", Estilo.SECUNDARIO);
    public JButton btnModificar = Estilo.boton("Modificar", Estilo.SECUNDARIO);
    public JButton btnEliminar = Estilo.boton("Inactivar", Estilo.PELIGRO);
    public JButton btnLimpiar = Estilo.boton("Limpiar", Estilo.SECUNDARIO);

    public DefaultTableModel modelo;
    public JTable tabla;

    private JLabel lblCuenta = Estilo.etiqueta("Sin registros", 11.5f, false, Estilo.GRIS);
    private JLabel lblAviso = Estilo.etiqueta("Listo", 11.5f, false, Estilo.GRIS);
    private boolean conBuscar;
    private String subtitulo = "Base de datos sena_basico";

    // textoBuscar = null significa que esta ventana no tiene boton de buscar
    public VentanaCrud(String nombre, String[] columnas, String textoBuscar, String textoEliminar) {
        setTitle("SENA - " + nombre);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(Estilo.FONDO);
        java.awt.Image icono = Estilo.icono(64);
        if (icono != null) {
            setIconImage(icono);
        }

        conBuscar = textoBuscar != null;
        if (conBuscar) {
            btnBuscar.setText(textoBuscar);
        }
        btnEliminar.setText(textoEliminar);

        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;   // la tabla es solo para ver
            }
        };
        tabla = new JTable(modelo);
        Estilo.estiloTabla(tabla);
    }

    // texto que aparece a la derecha del encabezado
    public void setSubtitulo(String texto) {
        this.subtitulo = texto;
    }

    // agrega una fila "etiqueta + campo" al formulario, ya con el estilo de la casa.
    // Los campos se guardan y se acomodan al final, cuando ya se sabe cuantos son.
    public void agregarCampo(String etiqueta, JComponent campo) {
        Estilo.estiloCampo(campo);
        // con Enter en un campo de texto se busca (o se registra si no hay busqueda)
        if (campo instanceof JTextField) {
            campo.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        (conBuscar ? btnBuscar : btnRegistrar).doClick();
                    }
                }
            });
        }
        etiquetas.add(etiqueta);
        campos.add(campo);
        cantidadCampos++;
    }

    // arma la rejilla del formulario: una columna, o dos si son muchos campos
    private void armarFormulario() {
        int columnas = cantidadCampos > 6 ? 2 : 1;
        formulario.removeAll();
        for (int i = 0; i < cantidadCampos; i++) {
            int columna = i % columnas;
            int fila = i / columnas;

            GridBagConstraints izquierda = new GridBagConstraints();
            izquierda.gridx = columna * 2;
            izquierda.gridy = fila;
            izquierda.anchor = GridBagConstraints.WEST;
            izquierda.insets = new Insets(5, columna == 0 ? 0 : 18, 5, 10);
            formulario.add(Estilo.etiqueta(etiquetas.get(i), 12, false, Estilo.GRIS), izquierda);

            GridBagConstraints derecha = new GridBagConstraints();
            derecha.gridx = columna * 2 + 1;
            derecha.gridy = fila;
            derecha.weightx = 1;
            derecha.fill = GridBagConstraints.HORIZONTAL;
            derecha.insets = new Insets(5, 0, 5, 0);
            formulario.add(campos.get(i), derecha);
        }
        filasFormulario = (cantidadCampos + columnas - 1) / columnas;
    }

    // Cada ventana llama a esto al final de su constructor, cuando ya agrego todos sus campos
    public void terminar() {
        armarFormulario();
        setLayout(new BorderLayout());
        add(Estilo.encabezado(getTitle().replace("SENA - ", ""), subtitulo), BorderLayout.NORTH);

        // ---- tarjeta del formulario ----
        JPanel tarjetaFormulario = Estilo.tarjeta();
        tarjetaFormulario.setLayout(new BorderLayout(0, 6));
        tarjetaFormulario.add(Estilo.etiqueta("Datos del registro", 13, true, Estilo.VERDE_OSCURO),
                BorderLayout.NORTH);
        formulario.setOpaque(false);
        tarjetaFormulario.add(formulario, BorderLayout.CENTER);

        // ---- fila de botones ----
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botones.setOpaque(false);
        botones.add(btnRegistrar);
        if (conBuscar) {
            botones.add(btnBuscar);
        }
        botones.add(btnModificar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        JPanel arriba = new JPanel();
        arriba.setLayout(new BoxLayout(arriba, BoxLayout.Y_AXIS));
        arriba.setOpaque(false);
        arriba.add(tarjetaFormulario);
        arriba.add(javax.swing.Box.createVerticalStrut(10));
        arriba.add(botones);

        // ---- tarjeta de la tabla ----
        JPanel tarjetaTabla = Estilo.tarjeta();
        tarjetaTabla.setLayout(new BorderLayout(0, 6));
        JPanel tituloTabla = new JPanel(new BorderLayout());
        tituloTabla.setOpaque(false);
        tituloTabla.add(Estilo.etiqueta("Registros guardados", 13, true, Estilo.VERDE_OSCURO), BorderLayout.WEST);
        JLabel ayuda = Estilo.etiqueta("Haz clic en una fila para cargarla en el formulario", 11.5f, false, Estilo.GRIS);
        ayuda.setHorizontalAlignment(SwingConstants.RIGHT);
        tituloTabla.add(ayuda, BorderLayout.EAST);
        tarjetaTabla.add(tituloTabla, BorderLayout.NORTH);
        JScrollPane desplazador = new JScrollPane(tabla);
        desplazador.setBorder(BorderFactory.createEmptyBorder());
        desplazador.getViewport().setBackground(Estilo.BLANCO);
        tarjetaTabla.add(desplazador, BorderLayout.CENTER);

        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setBackground(Estilo.FONDO);
        centro.setBorder(BorderFactory.createEmptyBorder(12, 14, 8, 14));
        centro.add(arriba, BorderLayout.NORTH);
        centro.add(tarjetaTabla, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        // ---- barra de estado ----
        add(barraEstado(), BorderLayout.SOUTH);

        // el tamano depende de cuantas filas de campos hay, pero nunca mas grande que la pantalla
        Rectangle pantalla = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int ancho = Math.min(980, pantalla.width - 30);
        int alto = Math.min(210 + filasFormulario * 40 + 200, pantalla.height - 20);
        setSize(ancho, alto);
        setLocationRelativeTo(null);
        actualizarCuenta();
    }

    private JPanel barraEstado() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Estilo.BLANCO);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Estilo.BORDE),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        lblAviso.setHorizontalAlignment(SwingConstants.RIGHT);
        barra.add(lblCuenta, BorderLayout.WEST);
        barra.add(lblAviso, BorderLayout.EAST);
        return barra;
    }

    // ------------------------------------------------------------- estado
    // mensaje corto en la barra de abajo (no abre ninguna ventana)
    public void estado(String mensaje) {
        lblAviso.setForeground(Estilo.VERDE_OSCURO);
        lblAviso.setText(mensaje);
    }

    private void actualizarCuenta() {
        lblCuenta.setText(filasTabla == 1 ? "1 registro en la tabla" : filasTabla + " registros en la tabla");
    }

    // ------------------------------------------------------------- seleccion
    public int getIdSeleccionado() {
        return idSeleccionado;
    }

    public void setIdSeleccionado(int id) {
        this.idSeleccionado = id;
    }

    // deja la ventana sin nada seleccionado (las ventanas hijas tambien vacian sus campos)
    public void limpiarSeleccion() {
        idSeleccionado = 0;
        tabla.clearSelection();
    }

    public void vaciarTabla() {
        modelo.setRowCount(0);
        filasTabla = 0;
        actualizarCuenta();
    }

    public void agregarFila(Object... valores) {
        modelo.addRow(valores);
        filasTabla++;
        actualizarCuenta();
    }

    // id del registro de la fila seleccionada (esta en la primera columna) o 0 si no hay
    public int idDeFilaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return 0;
        }
        return (int) modelo.getValueAt(fila, 0);
    }

    // ancho preferido de una columna de la tabla
    public void anchoColumna(int columna, int ancho) {
        tabla.getColumnModel().getColumn(columna).setPreferredWidth(ancho);
    }

    // ------------------------------------------------------------- combos
    // llena una lista desplegable con objetos (se ven con su toString)
    public <T> void llenarCombo(JComboBox<T> combo, List<T> items) {
        combo.removeAllItems();
        for (T item : items) {
            combo.addItem(item);
        }
    }

    // elige en la lista desplegable el objeto que tenga ese id
    public <T> void elegir(JComboBox<T> combo, ToIntFunction<T> idDe, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (idDe.applyAsInt(combo.getItemAt(i)) == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    // ------------------------------------------------------------- mensajes
    public void mostrarMensaje(String mensaje) {
        lblAviso.setForeground(Estilo.GRIS);
        lblAviso.setText(mensaje);
        JOptionPane.showMessageDialog(this, mensaje);
    }

    public boolean confirmar(String pregunta) {
        return JOptionPane.showConfirmDialog(this, pregunta, "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public String pedirTexto(String pregunta) {
        String r = JOptionPane.showInputDialog(this, pregunta);
        return r == null ? "" : r.trim();
    }
}
