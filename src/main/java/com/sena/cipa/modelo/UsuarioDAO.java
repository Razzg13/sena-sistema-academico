package com.sena.cipa.modelo;

import java.util.List;

public interface UsuarioDAO extends CRUD<Usuario> {

    Usuario buscarPorDocumento(String documento);

    // usuarios activos con un rol, para las listas desplegables de instructores y aprendices
    List<Usuario> listarPorRol(String rol);
}
