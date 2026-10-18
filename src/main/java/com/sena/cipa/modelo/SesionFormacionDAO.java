package com.sena.cipa.modelo;

import java.util.List;

public interface SesionFormacionDAO extends CRUD<SesionFormacion> {

    // todas las sesiones que se le dictan a una ficha
    List<SesionFormacion> listarPorFicha(int fichaId);

    SesionFormacion buscarPorTema(String tema);
}
