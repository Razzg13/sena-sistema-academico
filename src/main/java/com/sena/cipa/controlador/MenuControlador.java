package com.sena.cipa.controlador;

import com.sena.cipa.modelo.ActividadEvaluativaDAOImpl;
import com.sena.cipa.modelo.AprendizDAOImpl;
import com.sena.cipa.modelo.AsignacionDAOImpl;
import com.sena.cipa.modelo.AsistenciaDAOImpl;
import com.sena.cipa.modelo.Conexion;
import com.sena.cipa.modelo.EvaluacionDAOImpl;
import com.sena.cipa.modelo.FichaDAOImpl;
import com.sena.cipa.modelo.InstructorDAOImpl;
import com.sena.cipa.modelo.MatriculaDAOImpl;
import com.sena.cipa.modelo.ProgramaDAOImpl;
import com.sena.cipa.modelo.SesionFormacionDAOImpl;
import com.sena.cipa.modelo.UsuarioDAOImpl;
import com.sena.cipa.vista.ActividadEvaluativaVista;
import com.sena.cipa.vista.AprendizVista;
import com.sena.cipa.vista.AsignacionVista;
import com.sena.cipa.vista.AsistenciaVista;
import com.sena.cipa.vista.EvaluacionVista;
import com.sena.cipa.vista.FichaVista;
import com.sena.cipa.vista.InstructorVista;
import com.sena.cipa.vista.MatriculaVista;
import com.sena.cipa.vista.MenuVista;
import com.sena.cipa.vista.ProgramaVista;
import com.sena.cipa.vista.SesionFormacionVista;
import com.sena.cipa.vista.UsuarioVista;

import javax.swing.JFrame;

// CONTROLADOR del menu: comprueba la conexion y abre la ventana de cada modulo
// junto con su controlador. Para agregar un modulo nuevo se agrega una tarjeta aqui.
public class MenuControlador {

    private MenuVista vista;

    public MenuControlador(MenuVista vista) {
        this.vista = vista;

        // tarjetas del menu en el orden en que se muestran (clave, titulo, explicacion corta)
        vista.agregarModulo("programas", "Programas de formacion", "Catalogo de programas");
        vista.agregarModulo("fichas", "Fichas", "Grupos de aprendices");
        vista.agregarModulo("instructores", "Instructores", "Datos del instructor");
        vista.agregarModulo("aprendices", "Aprendices", "Datos del aprendiz");
        vista.agregarModulo("matriculas", "Matriculas", "Aprendiz en una ficha");
        vista.agregarModulo("asignaciones", "Asignaciones", "Instructor por ficha");
        vista.agregarModulo("sesiones", "Sesiones", "Clases dictadas");
        vista.agregarModulo("asistencias", "Asistencias", "Control de asistencia");
        vista.agregarModulo("actividades", "Actividades", "Talleres y pruebas");
        vista.agregarModulo("evaluaciones", "Evaluaciones", "Notas y juicios");
        vista.agregarModulo("usuarios", "Usuarios", "Cuentas, roles y claves");
        vista.agregarModulo("salir", "Salir", "Cerrar la aplicacion");

        vista.alSeleccionarModulo(this::abrir);

        // al abrir, verifica que la base de datos responda y se lo muestra al usuario
        Conexion conexion = new Conexion();
        boolean conectada = conexion.probar();
        vista.mostrarEstadoBaseDatos(conectada, conexion.getBd(), conexion.getUltimoError());
        vista.habilitarModulos(conectada);
    }

    // abre la ventana del modulo elegido con su controlador y su DAO
    private void abrir(String clave) {
        if ("programas".equals(clave)) {
            ProgramaVista ventana = new ProgramaVista();
            new ProgramaControlador(ventana, new ProgramaDAOImpl());
            mostrar(ventana);
        } else if ("fichas".equals(clave)) {
            FichaVista ventana = new FichaVista();
            new FichaControlador(ventana, new FichaDAOImpl());
            mostrar(ventana);
        } else if ("instructores".equals(clave)) {
            InstructorVista ventana = new InstructorVista();
            new InstructorControlador(ventana, new InstructorDAOImpl());
            mostrar(ventana);
        } else if ("aprendices".equals(clave)) {
            AprendizVista ventana = new AprendizVista();
            new AprendizControlador(ventana, new AprendizDAOImpl());
            mostrar(ventana);
        } else if ("matriculas".equals(clave)) {
            MatriculaVista ventana = new MatriculaVista();
            new MatriculaControlador(ventana, new MatriculaDAOImpl());
            mostrar(ventana);
        } else if ("asignaciones".equals(clave)) {
            AsignacionVista ventana = new AsignacionVista();
            new AsignacionControlador(ventana, new AsignacionDAOImpl());
            mostrar(ventana);
        } else if ("sesiones".equals(clave)) {
            SesionFormacionVista ventana = new SesionFormacionVista();
            new SesionFormacionControlador(ventana, new SesionFormacionDAOImpl());
            mostrar(ventana);
        } else if ("asistencias".equals(clave)) {
            AsistenciaVista ventana = new AsistenciaVista();
            new AsistenciaControlador(ventana, new AsistenciaDAOImpl());
            mostrar(ventana);
        } else if ("actividades".equals(clave)) {
            ActividadEvaluativaVista ventana = new ActividadEvaluativaVista();
            new ActividadEvaluativaControlador(ventana, new ActividadEvaluativaDAOImpl());
            mostrar(ventana);
        } else if ("evaluaciones".equals(clave)) {
            EvaluacionVista ventana = new EvaluacionVista();
            new EvaluacionControlador(ventana, new EvaluacionDAOImpl());
            mostrar(ventana);
        } else if ("usuarios".equals(clave)) {
            UsuarioVista ventana = new UsuarioVista();
            new UsuarioControlador(ventana, new UsuarioDAOImpl());
            mostrar(ventana);
        } else if ("salir".equals(clave)) {
            System.exit(0);
        }
    }

    private void mostrar(JFrame ventana) {
        ventana.setVisible(true);
    }
}
