package com.sena.cipa.modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class InstructorDAOImpl implements InstructorDAO {

    // se une con usuarios para poder mostrar el documento y el nombre en la tabla
    private static final String BASE =
            "SELECT i.id, i.usuario_id, i.especialidad, i.estado, u.documento, u.nombre_completo "
            + "FROM instructores i JOIN usuarios u ON u.id = i.usuario_id";

    private Instructor armar(ResultSet rs) throws SQLException {
        Instructor i = new Instructor();
        i.setId(rs.getInt("id"));
        i.setUsuarioId(rs.getInt("usuario_id"));
        i.setEspecialidad(rs.getString("especialidad"));
        i.setEstado(rs.getString("estado"));
        i.setDocumento(rs.getString("documento"));
        i.setNombreCompleto(rs.getString("nombre_completo"));
        return i;
    }

    @Override
    public List<Instructor> listarTodos() {
        return Bd.consultar(BASE + " ORDER BY u.nombre_completo", this::armar);
    }

    @Override
    public List<Instructor> listarActivos() {
        return Bd.consultar(BASE + " WHERE i.estado = 'ACTIVO' ORDER BY u.nombre_completo", this::armar);
    }

    @Override
    public Instructor leerPorId(int id) {
        return Bd.uno(BASE + " WHERE i.id = ?", this::armar, id);
    }

    @Override
    public Instructor buscarPorDocumento(String documento) {
        return Bd.uno(BASE + " WHERE u.documento = ?", this::armar, documento);
    }

    @Override
    public boolean registrar(Instructor i) {
        return Bd.ejecutar("INSERT INTO instructores (usuario_id, especialidad, estado) VALUES (?,?,?)",
                i.getUsuarioId(), i.getEspecialidad(), i.getEstado());
    }

    @Override
    public boolean actualizar(Instructor i) {
        return Bd.ejecutar("UPDATE instructores SET usuario_id=?, especialidad=?, estado=? WHERE id=?",
                i.getUsuarioId(), i.getEspecialidad(), i.getEstado(), i.getId());
    }

    // un instructor no se borra: se inactiva, porque sus asignaciones quedan en el historial
    @Override
    public boolean eliminar(int id) {
        return Bd.ejecutar("UPDATE instructores SET estado='INACTIVO' WHERE id=?", id);
    }
}
