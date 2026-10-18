package com.sena.cipa.modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class FichaDAOImpl implements FichaDAO {

    // se une con programas para poder mostrar el nombre del programa en la tabla
    private static final String BASE =
            "SELECT f.id, f.numero, f.programa_id, f.jornada, f.fecha_inicio, f.fecha_fin, f.ambiente, f.estado, "
            + "p.nombre AS nombre_programa "
            + "FROM fichas f JOIN programas p ON p.id = f.programa_id";

    private Ficha armar(ResultSet rs) throws SQLException {
        Ficha f = new Ficha();
        f.setId(rs.getInt("id"));
        f.setNumero(rs.getString("numero"));
        f.setProgramaId(rs.getInt("programa_id"));
        f.setJornada(rs.getString("jornada"));
        f.setFechaInicio(rs.getString("fecha_inicio"));
        f.setFechaFin(rs.getString("fecha_fin"));
        f.setAmbiente(rs.getString("ambiente"));
        f.setEstado(rs.getString("estado"));
        f.setNombrePrograma(rs.getString("nombre_programa"));
        return f;
    }

    @Override
    public List<Ficha> listarTodos() {
        return Bd.consultar(BASE + " ORDER BY f.numero", this::armar);
    }

    @Override
    public List<Ficha> listarEnEjecucion() {
        return Bd.consultar(BASE + " WHERE f.estado = 'EN_EJECUCION' ORDER BY f.numero", this::armar);
    }

    @Override
    public Ficha leerPorId(int id) {
        return Bd.uno(BASE + " WHERE f.id = ?", this::armar, id);
    }

    @Override
    public Ficha buscarPorNumero(String numero) {
        return Bd.uno(BASE + " WHERE f.numero = ?", this::armar, numero);
    }

    @Override
    public boolean registrar(Ficha f) {
        return Bd.ejecutar("INSERT INTO fichas (numero, programa_id, jornada, fecha_inicio, fecha_fin, ambiente, "
                + "estado) VALUES (?,?,?,?,?,?,?)",
                f.getNumero(), f.getProgramaId(), f.getJornada(), f.getFechaInicio(), f.getFechaFin(),
                f.getAmbiente(), f.getEstado());
    }

    @Override
    public boolean actualizar(Ficha f) {
        return Bd.ejecutar("UPDATE fichas SET numero=?, programa_id=?, jornada=?, fecha_inicio=?, fecha_fin=?, "
                + "ambiente=?, estado=? WHERE id=?",
                f.getNumero(), f.getProgramaId(), f.getJornada(), f.getFechaInicio(), f.getFechaFin(),
                f.getAmbiente(), f.getEstado(), f.getId());
    }

    // una ficha no se borra: se cancela, porque sus matriculas quedan en el historial
    @Override
    public boolean eliminar(int id) {
        return Bd.ejecutar("UPDATE fichas SET estado='CANCELADA' WHERE id=?", id);
    }
}
