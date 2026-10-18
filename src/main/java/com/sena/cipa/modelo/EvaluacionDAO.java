package com.sena.cipa.modelo;

import java.util.List;

public interface EvaluacionDAO extends CRUD<Evaluacion> {

    List<Evaluacion> listarPorDocumento(String documento);

    List<Evaluacion> listarPorActividad(int actividadId);
}
