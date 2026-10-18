package com.sena.cipa.modelo;

import java.util.List;

public interface AsignacionDAO extends CRUD<Asignacion> {

    // instructores asignados a una ficha, buscada por su numero
    List<Asignacion> listarPorFicha(String numeroFicha);
}
