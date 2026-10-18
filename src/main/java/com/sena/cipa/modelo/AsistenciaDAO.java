package com.sena.cipa.modelo;

import java.util.List;

public interface AsistenciaDAO extends CRUD<Asistencia> {

    List<Asistencia> listarPorDocumento(String documento);

    List<Asistencia> listarPorSesion(int sesionId);
}
