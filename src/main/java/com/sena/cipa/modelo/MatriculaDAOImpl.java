package com.sena.cipa.modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MatriculaDAOImpl implements MatriculaDAO {

    // se une con aprendices, usuarios y fichas para poder mostrar los nombres en la tabla
    private static final String BASE =
            "SELECT m.id, m.aprendiz_id, m.ficha_id, m.fecha_matricula, m.estado, u.documento, "
            + "u.nombre_completo, f.numero "
            + "FROM matriculas m "
            + "JOIN aprendices a ON a.id = m.aprendiz_id "
            + "JOIN usuarios u ON u.id = a.usuario_id "
            + "JOIN fichas f ON f.id = m.ficha_id";

    private Matricula armar(ResultSet rs) throws SQLException {
        Matricula m = new Matricula();
        m.setId(rs.getInt("id"));
        m.setAprendizId(rs.getInt("aprendiz_id"));
        m.setFichaId(rs.getInt("ficha_id"));
        m.setFechaMatricula(rs.getString("fecha_matricula"));
        m.setEstado(rs.getString("estado"));
        m.setDocumentoAprendiz(rs.getString("documento"));
        m.setNombreAprendiz(rs.getString("nombre_completo"));
        m.setNumeroFicha(rs.getString("numero"));
        return m;
    }

    @Override
    public List<Matricula> listarTodos() {
        return Bd.consultar(BASE + " ORDER BY f.numero, u.nombre_completo", this::armar);
    }

    @Override
    public List<Matricula> listarActivas() {
        return Bd.consultar(BASE + " WHERE m.estado = 'ACTIVA' ORDER BY f.numero, u.nombre_completo", this::armar);
    }

    @Override
    public List<Matricula> listarPorDocumento(String documento) {
        return Bd.consultar(BASE + " WHERE u.documento = ? ORDER BY f.numero", this::armar, documento);
    }

    @Override
    public Matricula leerPorId(int id) {
        return Bd.uno(BASE + " WHERE m.id = ?", this::armar, id);
    }

    @Override
    public boolean registrar(Matricula m) {
        return Bd.ejecutar("INSERT INTO matriculas (aprendiz_id, ficha_id, fecha_matricula, estado) VALUES (?,?,?,?)",
                m.getAprendizId(), m.getFichaId(), m.getFechaMatricula(), m.getEstado());
    }

    @Override
    public boolean actualizar(Matricula m) {
        return Bd.ejecutar("UPDATE matriculas SET aprendiz_id=?, ficha_id=?, fecha_matricula=?, estado=? WHERE id=?",
                m.getAprendizId(), m.getFichaId(), m.getFechaMatricula(), m.getEstado(), m.getId());
    }

    // una matricula no se borra: se cancela, porque queda en el historial del aprendiz
    @Override
    public boolean eliminar(int id) {
        return Bd.ejecutar("UPDATE matriculas SET estado='CANCELADA' WHERE id=?", id);
    }
}
