package com.sena.cipa.modelo;

import java.util.List;

public interface CRUD<T> {

    List<T> listarTodos();
    T leerPorId(int id);
    boolean registrar(T t);
    boolean actualizar(T t);
    boolean eliminar(int id);   // borrado logico: cambia el estado, no borra la fila
}
