package com.sena.cipa.modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AsignacionDAOImpl implements AsignacionDAO {

    // se une con fichas, instructores y usuarios para poder mostrar los nombres en la tabla
    private static final String BASE =
            "SELECT a.id, a.ficha_id, a.instructor_id, a.fecha_inicio, a.fecha_fin, f.numero, "
            + "u.nombre_completo "
            + "FROM asignaciones_instructor a "
            + "JOIN fichas f ON f.id = a.ficha_id "
            + "JOIN instructores i ON i.id = a.instructor_id "
            + "JOIN usuarios u ON u.id = i.usuario_id";

    private Asignacion armar(ResultSet rs) throws SQLException {
        Asignacion a = new Asignacion();
        a.setId(rs.getInt("id"));
        a.setFichaId(rs.getInt("ficha_id"));
        a.setInstructorId(rs.getInt("instructor_id"));
        a.setFechaInicio(rs.getString("fecha_inicio"));
        a.setFechaFin(rs.getString("fecha_fin"));
        a.setNumeroFicha(rs.getString("numero"));
        a.setNombreInstructor(rs.getString("nombre_completo"));
        return a;
    }

    @Override
    public List<Asignacion> listarTodos() {
        return Bd.consultar(BASE + " ORDER BY f.numero, u.nombre_completo", this::armar);
    }

    @Override
    public List<Asignacion> listarPorFicha(String numeroFicha) {
        return Bd.consultar(BASE + " WHERE f.numero = ? ORDER BY u.nombre_completo", this::armar, numeroFicha);
    }

    @Override
    public Asignacion leerPorId(int id) {
        return Bd.uno(BASE + " WHERE a.id = ?", this::armar, id);
    }

    @Override
    public boolean registrar(Asignacion a) {
        return Bd.ejecutar("INSERT INTO asignaciones_instructor (ficha_id, instructor_id, fecha_inicio, fecha_fin) "
                + "VALUES (?,?,?,?)",
                a.getFichaId(), a.getInstructorId(), a.getFechaInicio(), a.getFechaFin());
    }

    @Override
    public boolean actualizar(Asignacion a) {
        return Bd.ejecutar("UPDATE asignaciones_instructor SET ficha_id=?, instructor_id=?, fecha_inicio=?, "
                + "fecha_fin=? WHERE id=?",
                a.getFichaId(), a.getInstructorId(), a.getFechaInicio(), a.getFechaFin(), a.getId());
    }

    // esta tabla no tiene columna estado: es el unico modulo con borrado real
    @Override
    public boolean eliminar(int id) {
        return Bd.ejecutar("DELETE FROM asignaciones_instructor WHERE id=?", id);
    }
}
