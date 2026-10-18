package com.sena.cipa.modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ActividadEvaluativaDAOImpl implements ActividadEvaluativaDAO {

    // se une con fichas para poder mostrar el numero de ficha en la tabla
    private static final String BASE =
            "SELECT a.id, a.ficha_id, a.nombre, a.descripcion, a.fecha_limite, f.numero "
            + "FROM actividades_evaluativas a JOIN fichas f ON f.id = a.ficha_id";

    private ActividadEvaluativa armar(ResultSet rs) throws SQLException {
        ActividadEvaluativa a = new ActividadEvaluativa();
        a.setId(rs.getInt("id"));
        a.setFichaId(rs.getInt("ficha_id"));
        a.setNombre(rs.getString("nombre"));
        a.setDescripcion(rs.getString("descripcion"));
        a.setFechaLimite(rs.getString("fecha_limite"));
        a.setNumeroFicha(rs.getString("numero"));
        return a;
    }

    @Override
    public List<ActividadEvaluativa> listarTodos() {
        return Bd.consultar(BASE + " ORDER BY a.fecha_limite", this::armar);
    }

    @Override
    public List<ActividadEvaluativa> listarPorFicha(int fichaId) {
        return Bd.consultar(BASE + " WHERE a.ficha_id = ? ORDER BY a.fecha_limite", this::armar, fichaId);
    }

    @Override
    public ActividadEvaluativa leerPorId(int id) {
        return Bd.uno(BASE + " WHERE a.id = ?", this::armar, id);
    }

    @Override
    public ActividadEvaluativa buscarPorNombre(String nombre) {
        return Bd.uno(BASE + " WHERE a.nombre = ? ORDER BY a.fecha_limite", this::armar, nombre);
    }

    @Override
    public boolean registrar(ActividadEvaluativa a) {
        return Bd.ejecutar("INSERT INTO actividades_evaluativas (ficha_id, nombre, descripcion, fecha_limite) "
                + "VALUES (?,?,?,?)",
                a.getFichaId(), a.getNombre(), a.getDescripcion(), a.getFechaLimite());
    }

    @Override
    public boolean actualizar(ActividadEvaluativa a) {
        return Bd.ejecutar("UPDATE actividades_evaluativas SET ficha_id=?, nombre=?, descripcion=?, fecha_limite=? "
                + "WHERE id=?",
                a.getFichaId(), a.getNombre(), a.getDescripcion(), a.getFechaLimite(), a.getId());
    }

    // la tabla no tiene estado: la actividad se borra de verdad
    @Override
    public boolean eliminar(int id) {
        return Bd.ejecutar("DELETE FROM actividades_evaluativas WHERE id=?", id);
    }
}
