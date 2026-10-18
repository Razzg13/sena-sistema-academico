package com.sena.cipa.modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProgramaDAOImpl implements ProgramaDAO {

    private static final String BASE =
            "SELECT id, codigo, nombre, nivel, version, duracion_horas, estado FROM programas";

    // convierte UNA fila de la tabla programas en un objeto Programa
    private Programa armar(ResultSet rs) throws SQLException {
        Programa p = new Programa();
        p.setId(rs.getInt("id"));
        p.setCodigo(rs.getString("codigo"));
        p.setNombre(rs.getString("nombre"));
        p.setNivel(rs.getString("nivel"));
        p.setVersion(rs.getString("version"));
        p.setDuracionHoras(rs.getInt("duracion_horas"));
        p.setEstado(rs.getString("estado"));
        return p;
    }

    @Override
    public List<Programa> listarTodos() {
        return Bd.consultar(BASE + " ORDER BY id", this::armar);
    }

    @Override
    public List<Programa> listarActivos() {
        return Bd.consultar(BASE + " WHERE estado = 'ACTIVO' ORDER BY nombre", this::armar);
    }

    @Override
    public Programa leerPorId(int id) {
        return Bd.uno(BASE + " WHERE id = ?", this::armar, id);
    }

    @Override
    public Programa buscarPorCodigo(String codigo) {
        return Bd.uno(BASE + " WHERE codigo = ?", this::armar, codigo);
    }

    @Override
    public boolean registrar(Programa p) {
        return Bd.ejecutar("INSERT INTO programas (codigo, nombre, nivel, version, duracion_horas, estado) "
                + "VALUES (?,?,?,?,?,?)",
                p.getCodigo(), p.getNombre(), p.getNivel(), p.getVersion(), p.getDuracionHoras(), p.getEstado());
    }

    @Override
    public boolean actualizar(Programa p) {
        return Bd.ejecutar("UPDATE programas SET codigo=?, nombre=?, nivel=?, version=?, duracion_horas=?, estado=? "
                + "WHERE id=?",
                p.getCodigo(), p.getNombre(), p.getNivel(), p.getVersion(), p.getDuracionHoras(), p.getEstado(), p.getId());
    }

    // borrado logico: la fila se queda, solo cambia su estado
    @Override
    public boolean eliminar(int id) {
        return Bd.ejecutar("UPDATE programas SET estado='INACTIVO' WHERE id=?", id);
    }
}
