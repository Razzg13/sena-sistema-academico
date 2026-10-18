package com.sena.cipa.modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    // la base basica del proyecto (se puede cambiar con -Dbd=otra_base)
    String bd = System.getProperty("bd", "sena_basico");
    String url = "jdbc:mysql://localhost:3306/";
    String user = "root";
    String clave = "123456";
    String driver = "com.mysql.cj.jdbc.Driver";
    Connection cx;
    String ultimoError = "";

    public Conexion() {}

    public Connection conecta() {
        try {
            Class.forName(driver);
            cx = DriverManager.getConnection(url + bd, user, clave);
        } catch (ClassNotFoundException | SQLException ex) {
            ultimoError = ex.getMessage();
            System.out.println("NO se conecto con la BD: " + ex.getMessage());
        }
        return cx;
    }

    public void desconecta() {
        try {
            if (cx != null) {
                cx.close();
            }
        } catch (SQLException ex) {
            System.out.println("Error al desconectar la BD");
        }
    }

    // Abre y cierra una conexion para comprobar que MySQL responde
    public boolean probar() {
        boolean ok = conecta() != null;
        desconecta();
        return ok;
    }

    public String getBd() {
        return bd;
    }

    public String getUltimoError() {
        return ultimoError;
    }
}
