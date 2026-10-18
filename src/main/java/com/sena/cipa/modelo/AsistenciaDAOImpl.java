package com.sena.cipa.modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AsistenciaDAOImpl implements AsistenciaDAO {

    // se une con sesiones, matriculas, aprendices, usuarios y fichas para mostrar
    // de una vez la fecha, el tema, el aprendiz y la ficha
    private static final String BASE =
            "SELECT a.id, a.sesion_id, a.matricula_id, a.estado, a.observacion, "
            + "s.fecha, s.tema, f.numero, u.documento, u.nombre_completo "
            + "FROM asistencias a "
            + "JOIN sesiones_formacion s ON s.id = a.sesion_id "
            + "JOIN matriculas m ON m.id = a.matricula_id "
            + "JOIN aprendices ap ON ap.id = m.aprendiz_id "
            + "JOIN usuarios u ON u.id = ap.usuario_id "
            + "JOIN fichas f ON f.id = m.ficha_id";

    private Asistencia armar(ResultSet rs) throws SQLException {
        Asistencia a = new Asistencia();
        a.setId(rs.getInt("id"));
        a.setSesionId(rs.getInt("sesion_id"));
        a.setMatriculaId(rs.getInt("matricula_id"));
        a.setEstado(rs.getString("estado"));
        a.setObservacion(rs.getString("observacion"));
        a.setFechaSesion(rs.getString("fecha"));
        a.setTemaSesion(rs.getString("tema"));
        a.setNumeroFicha(rs.getString("numero"));
        a.setDocumentoAprendiz(rs.getString("documento"));
        a.setNombreAprendiz(rs.getString("nombre_completo"));
        return a;
    }

    @Override
    public List<Asistencia> listarTodos() {
        return Bd.consultar(BASE + " ORDER BY s.fecha, u.nombre_completo", this::armar);
    }

    @Override
    public List<Asistencia> listarPorDocumento(String documento) {
        return Bd.consultar(BASE + " WHERE u.documento = ? ORDER BY s.fecha", this::armar, documento);
    }

    @Override
    public List<Asistencia> listarPorSesion(int sesionId) {
        return Bd.consultar(BASE + " WHERE a.sesion_id = ? ORDER BY u.nombre_completo", this::armar, sesionId);
    }

    @Override
    public Asistencia leerPorId(int id) {
        return Bd.uno(BASE + " WHERE a.id = ?", this::armar, id);
    }

    @Override
    public boolean registrar(Asistencia a) {
        return Bd.ejecutar("INSERT INTO asistencias (sesion_id, matricula_id, estado, observacion) VALUES (?,?,?,?)",
                a.getSesionId(), a.getMatriculaId(), a.getEstado(),
                a.getObservacion().isEmpty() ? null : a.getObservacion());
    }

    @Override
    public boolean actualizar(Asistencia a) {
        return Bd.ejecutar("UPDATE asistencias SET sesion_id=?, matricula_id=?, estado=?, observacion=? WHERE id=?",
                a.getSesionId(), a.getMatriculaId(), a.getEstado(),
                a.getObservacion().isEmpty() ? null : a.getObservacion(), a.getId());
    }

    // la tabla no tiene columna estado: la asistencia se borra de verdad
    @Override
    public boolean eliminar(int id) {
        return Bd.ejecutar("DELETE FROM asistencias WHERE id=?", id);
    }
}
