package com.sena.cipa.modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EvaluacionDAOImpl implements EvaluacionDAO {

    private static final String BASE =
            "SELECT e.id, e.actividad_id, e.matricula_id, e.calificacion, e.juicio, e.retroalimentacion, "
            + "e.fecha_evaluacion, ac.nombre AS actividad, u.documento, u.nombre_completo, f.numero "
            + "FROM evaluaciones e "
            + "JOIN actividades_evaluativas ac ON ac.id = e.actividad_id "
            + "JOIN matriculas m ON m.id = e.matricula_id "
            + "JOIN aprendices ap ON ap.id = m.aprendiz_id "
            + "JOIN usuarios u ON u.id = ap.usuario_id "
            + "JOIN fichas f ON f.id = m.ficha_id";

    private Evaluacion armar(ResultSet rs) throws SQLException {
        Evaluacion e = new Evaluacion();
        e.setId(rs.getInt("id"));
        e.setActividadId(rs.getInt("actividad_id"));
        e.setMatriculaId(rs.getInt("matricula_id"));
        e.setCalificacion(rs.getString("calificacion"));
        e.setJuicio(rs.getString("juicio"));
        e.setRetroalimentacion(rs.getString("retroalimentacion"));
        e.setFechaEvaluacion(rs.getString("fecha_evaluacion"));
        e.setNombreActividad(rs.getString("actividad"));
        e.setNombreAprendiz(rs.getString("nombre_completo"));
        e.setNumeroFicha(rs.getString("numero"));
        return e;
    }

    // la calificacion puede ir vacia: en ese caso en la base queda NULL
    private Object nota(Evaluacion e) {
        return (e.getCalificacion() == null || e.getCalificacion().isEmpty()) ? null : e.getCalificacion();
    }

    private Object texto(String valor) {
        return (valor == null || valor.isEmpty()) ? null : valor;
    }

    @Override
    public List<Evaluacion> listarTodos() {
        return Bd.consultar(BASE + " ORDER BY ac.nombre, u.nombre_completo", this::armar);
    }

    @Override
    public List<Evaluacion> listarPorDocumento(String documento) {
        return Bd.consultar(BASE + " WHERE u.documento = ?", this::armar, documento);
    }

    @Override
    public List<Evaluacion> listarPorActividad(int actividadId) {
        return Bd.consultar(BASE + " WHERE e.actividad_id = ?", this::armar, actividadId);
    }

    @Override
    public Evaluacion leerPorId(int id) {
        return Bd.uno(BASE + " WHERE e.id = ?", this::armar, id);
    }

    @Override
    public boolean registrar(Evaluacion e) {
        return Bd.ejecutar("INSERT INTO evaluaciones (actividad_id, matricula_id, calificacion, juicio, "
                + "retroalimentacion, fecha_evaluacion) VALUES (?,?,?,?,?,?)",
                e.getActividadId(), e.getMatriculaId(), nota(e), e.getJuicio(),
                texto(e.getRetroalimentacion()), e.getFechaEvaluacion());
    }

    @Override
    public boolean actualizar(Evaluacion e) {
        return Bd.ejecutar("UPDATE evaluaciones SET actividad_id=?, matricula_id=?, calificacion=?, juicio=?, "
                + "retroalimentacion=?, fecha_evaluacion=? WHERE id=?",
                e.getActividadId(), e.getMatriculaId(), nota(e), e.getJuicio(),
                texto(e.getRetroalimentacion()), e.getFechaEvaluacion(), e.getId());
    }

    // la tabla no tiene columna estado: la evaluacion se borra de verdad
    @Override
    public boolean eliminar(int id) {
        return Bd.ejecutar("DELETE FROM evaluaciones WHERE id=?", id);
    }
}
