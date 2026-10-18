package com.sena.cipa.modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Ayudante para no repetir en cada DAO el mismo codigo de JDBC
// (abrir conexion, preparar el SQL, ejecutar y cerrar).
public class Bd {

    // Le dice a consultar() como convertir UNA fila del resultado en un objeto
    public interface Fila<T> {
        T armar(ResultSet rs) throws SQLException;
    }

    // Llena los signos ? del SQL con los valores, en orden
    private static void poner(PreparedStatement pst, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pst.setObject(i + 1, params[i]);
        }
    }

    // Para los SELECT: devuelve una lista de objetos
    public static <T> List<T> consultar(String sql, Fila<T> fila, Object... params) {
        List<T> lista = new ArrayList<>();
        Conexion conectar = new Conexion();
        Connection cn = conectar.conecta();
        if (cn == null) {
            return lista;
        }
        try {
            PreparedStatement pst = cn.prepareStatement(sql);
            poner(pst, params);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                lista.add(fila.armar(rs));
            }
        } catch (SQLException ex) {
            System.out.println("Error en la consulta: " + ex.getMessage());
        } finally {
            conectar.desconecta();
        }
        return lista;
    }

    // Para los SELECT que devuelven una sola fila (o null si no hay)
    public static <T> T uno(String sql, Fila<T> fila, Object... params) {
        List<T> lista = consultar(sql, fila, params);
        return lista.isEmpty() ? null : lista.get(0);
    }

    // Para INSERT, UPDATE y DELETE: devuelve true si afecto alguna fila
    public static boolean ejecutar(String sql, Object... params) {
        Conexion conectar = new Conexion();
        Connection cn = conectar.conecta();
        if (cn == null) {
            return false;
        }
        try {
            PreparedStatement pst = cn.prepareStatement(sql);
            poner(pst, params);
            return pst.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Error al ejecutar: " + ex.getMessage());
            return false;
        } finally {
            conectar.desconecta();
        }
    }
}
