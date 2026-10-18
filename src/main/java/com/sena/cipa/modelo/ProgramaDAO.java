package com.sena.cipa.modelo;

import java.util.List;

public interface ProgramaDAO extends CRUD<Programa> {

    Programa buscarPorCodigo(String codigo);
    List<Programa> listarActivos();
}
