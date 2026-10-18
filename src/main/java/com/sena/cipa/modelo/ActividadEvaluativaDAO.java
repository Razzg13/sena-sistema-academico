package com.sena.cipa.modelo;

import java.util.List;

public interface ActividadEvaluativaDAO extends CRUD<ActividadEvaluativa> {

    // todas las actividades que tiene una ficha
    List<ActividadEvaluativa> listarPorFicha(int fichaId);

    ActividadEvaluativa buscarPorNombre(String nombre);
}
