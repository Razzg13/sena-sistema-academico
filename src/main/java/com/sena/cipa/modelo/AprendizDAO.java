package com.sena.cipa.modelo;

import java.util.List;

public interface AprendizDAO extends CRUD<Aprendiz> {

    Aprendiz buscarPorDocumento(String documento);

    // aprendices que todavia se pueden matricular, para las listas desplegables
    List<Aprendiz> listarActivos();
}
