package com.sena.cipa;

import com.sena.cipa.controlador.MenuControlador;
import com.sena.cipa.vista.Estilo;
import com.sena.cipa.vista.MenuVista;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class App {

    public static void main(String[] args) {
        // si algo falla dentro de una ventana, se le muestra el error en vez de perderse en silencio
        Thread.setDefaultUncaughtExceptionHandler((hilo, error) -> {
            error.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ocurrio un error inesperado:\n" + error,
                    "Error", JOptionPane.ERROR_MESSAGE);
        });

        // deja el mismo aspecto (colores y fuentes) en todas las ventanas
        Estilo.aplicar();

        SwingUtilities.invokeLater(() -> {
            MenuVista menu = new MenuVista();
            new MenuControlador(menu);
            menu.setVisible(true);
        });
    }
}
