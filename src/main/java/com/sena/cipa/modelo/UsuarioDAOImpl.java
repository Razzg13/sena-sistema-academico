package com.sena.cipa.modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class UsuarioDAOImpl implements UsuarioDAO {

    // no se trae la columna clave: la contrasena cifrada nunca sale de la base
    private static final String BASE =
            "SELECT id, documento, nombre_completo, correo, telefono, rol, estado FROM usuarios";

    private Usuario armar(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setDocumento(rs.getString("documento"));
        u.setNombreCompleto(rs.getString("nombre_completo"));
        u.setCorreo(rs.getString("correo"));
        u.setTelefono(rs.getString("telefono"));
        u.setRol(rs.getString("rol"));
        u.setEstado(rs.getString("estado"));
        return u;
    }

    // convierte la clave escrita en un texto cifrado (BCrypt); nunca se guarda la clave real
    private String cifrar(String clave) {
        return BCrypt.hashpw(clave, BCrypt.gensalt(10));
    }

    @Override
    public List<Usuario> listarTodos() {
        return Bd.consultar(BASE + " ORDER BY id", this::armar);
    }

    @Override
    public Usuario leerPorId(int id) {
        return Bd.uno(BASE + " WHERE id = ?", this::armar, id);
    }

    @Override
    public Usuario buscarPorDocumento(String documento) {
        return Bd.uno(BASE + " WHERE documento = ?", this::armar, documento);
    }

    @Override
    public List<Usuario> listarPorRol(String rol) {
        return Bd.consultar(BASE + " WHERE rol = ? AND estado = 'ACTIVO' ORDER BY nombre_completo",
                this::armar, rol);
    }

    @Override
    public boolean registrar(Usuario u) {
        return Bd.ejecutar("INSERT INTO usuarios (documento, nombre_completo, correo, telefono, clave, rol, estado) "
                + "VALUES (?,?,?,?,?,?,?)",
                u.getDocumento(), u.getNombreCompleto(), u.getCorreo(), u.getTelefono(),
                cifrar(u.getClave()), u.getRol(), u.getEstado());
    }

    // Si la clave viene vacia se conserva la que ya tenia
    @Override
    public boolean actualizar(Usuario u) {
        if (u.getClave() == null || u.getClave().isEmpty()) {
            return Bd.ejecutar("UPDATE usuarios SET documento=?, nombre_completo=?, correo=?, telefono=?, rol=?, estado=? "
                    + "WHERE id=?",
                    u.getDocumento(), u.getNombreCompleto(), u.getCorreo(), u.getTelefono(),
                    u.getRol(), u.getEstado(), u.getId());
        }
        return Bd.ejecutar("UPDATE usuarios SET documento=?, nombre_completo=?, correo=?, telefono=?, clave=?, rol=?, "
                + "estado=? WHERE id=?",
                u.getDocumento(), u.getNombreCompleto(), u.getCorreo(), u.getTelefono(),
                cifrar(u.getClave()), u.getRol(), u.getEstado(), u.getId());
    }

    // borrado logico: la fila se queda, solo cambia su estado
    @Override
    public boolean eliminar(int id) {
        return Bd.ejecutar("UPDATE usuarios SET estado='INACTIVO' WHERE id=?", id);
    }
}
