package com.sena.cipa.modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AprendizDAOImpl implements AprendizDAO {

    // se une con usuarios para poder mostrar el documento y el nombre en la tabla
    private static final String BASE =
            "SELECT a.id, a.usuario_id, a.estado_formativo, a.estado, u.documento, u.nombre_completo "
            + "FROM aprendices a JOIN usuarios u ON u.id = a.usuario_id";

    private Aprendiz armar(ResultSet rs) throws SQLException {
        Aprendiz a = new Aprendiz();
        a.setId(rs.getInt("id"));
        a.setUsuarioId(rs.getInt("usuario_id"));
        a.setEstadoFormativo(rs.getString("estado_formativo"));
        a.setEstado(rs.getString("estado"));
        a.setDocumento(rs.getString("documento"));
        a.setNombreCompleto(rs.getString("nombre_completo"));
        return a;
    }

    @Override
    public List<Aprendiz> listarTodos() {
        return Bd.consultar(BASE + " ORDER BY u.nombre_completo", this::armar);
    }

    @Override
    public List<Aprendiz> listarActivos() {
        return Bd.consultar(BASE + " WHERE a.estado = 'ACTIVO' ORDER BY u.nombre_completo", this::armar);
    }

    @Override
    public Aprendiz leerPorId(int id) {
        return Bd.uno(BASE + " WHERE a.id = ?", this::armar, id);
    }

    @Override
    public Aprendiz buscarPorDocumento(String documento) {
        return Bd.uno(BASE + " WHERE u.documento = ?", this::armar, documento);
    }

    @Override
    public boolean registrar(Aprendiz a) {
        return Bd.ejecutar("INSERT INTO aprendices (usuario_id, estado_formativo, estado) VALUES (?,?,?)",
                a.getUsuarioId(), a.getEstadoFormativo(), a.getEstado());
    }

    @Override
    public boolean actualizar(Aprendiz a) {
        return Bd.ejecutar("UPDATE aprendices SET usuario_id=?, estado_formativo=?, estado=? WHERE id=?",
                a.getUsuarioId(), a.getEstadoFormativo(), a.getEstado(), a.getId());
    }

    // un aprendiz no se borra: se inactiva, porque sus matriculas quedan en el historial
    @Override
    public boolean eliminar(int id) {
        return Bd.ejecutar("UPDATE aprendices SET estado='INACTIVO' WHERE id=?", id);
    }
}
