package com.sena.cipa.modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SesionFormacionDAOImpl implements SesionFormacionDAO {

    // se une con fichas para poder mostrar el numero de ficha en la tabla
    private static final String BASE =
            "SELECT s.id, s.ficha_id, s.fecha, s.hora_inicio, s.hora_fin, s.tema, f.numero "
            + "FROM sesiones_formacion s JOIN fichas f ON f.id = s.ficha_id";

    private SesionFormacion armar(ResultSet rs) throws SQLException {
        SesionFormacion s = new SesionFormacion();
        s.setId(rs.getInt("id"));
        s.setFichaId(rs.getInt("ficha_id"));
        s.setFecha(rs.getString("fecha"));
        s.setHoraInicio(rs.getString("hora_inicio"));
        s.setHoraFin(rs.getString("hora_fin"));
        s.setTema(rs.getString("tema"));
        s.setNumeroFicha(rs.getString("numero"));
        return s;
    }

    @Override
    public List<SesionFormacion> listarTodos() {
        return Bd.consultar(BASE + " ORDER BY s.fecha, s.hora_inicio", this::armar);
    }

    @Override
    public List<SesionFormacion> listarPorFicha(int fichaId) {
        return Bd.consultar(BASE + " WHERE s.ficha_id = ? ORDER BY s.fecha, s.hora_inicio", this::armar, fichaId);
    }

    @Override
    public SesionFormacion leerPorId(int id) {
        return Bd.uno(BASE + " WHERE s.id = ?", this::armar, id);
    }

    @Override
    public SesionFormacion buscarPorTema(String tema) {
        return Bd.uno(BASE + " WHERE s.tema = ? ORDER BY s.fecha, s.hora_inicio", this::armar, tema);
    }

    @Override
    public boolean registrar(SesionFormacion s) {
        return Bd.ejecutar("INSERT INTO sesiones_formacion (ficha_id, fecha, hora_inicio, hora_fin, tema) "
                + "VALUES (?,?,?,?,?)",
                s.getFichaId(), s.getFecha(), s.getHoraInicio(), s.getHoraFin(), s.getTema());
    }

    @Override
    public boolean actualizar(SesionFormacion s) {
        return Bd.ejecutar("UPDATE sesiones_formacion SET ficha_id=?, fecha=?, hora_inicio=?, hora_fin=?, tema=? "
                + "WHERE id=?",
                s.getFichaId(), s.getFecha(), s.getHoraInicio(), s.getHoraFin(), s.getTema(), s.getId());
    }

    // la tabla no tiene estado: la sesion se borra de verdad
    @Override
    public boolean eliminar(int id) {
        return Bd.ejecutar("DELETE FROM sesiones_formacion WHERE id=?", id);
    }
}
