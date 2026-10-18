package com.sena.cipa.modelo;

import java.util.List;

public interface MatriculaDAO extends CRUD<Matricula> {

    // matriculas vigentes, para los listados de trabajo
    List<Matricula> listarActivas();

    // todas las matriculas de un aprendiz, buscadas por su documento
    List<Matricula> listarPorDocumento(String documento);
}
