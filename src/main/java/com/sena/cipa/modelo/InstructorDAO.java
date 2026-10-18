package com.sena.cipa.modelo;

import java.util.List;

public interface InstructorDAO extends CRUD<Instructor> {

    Instructor buscarPorDocumento(String documento);

    // instructores que todavia pueden atender fichas, para las listas desplegables
    List<Instructor> listarActivos();
}
