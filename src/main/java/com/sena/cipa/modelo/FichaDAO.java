package com.sena.cipa.modelo;

import java.util.List;

public interface FichaDAO extends CRUD<Ficha> {

    Ficha buscarPorNumero(String numero);

    // fichas que todavia se estan dictando, para las listas desplegables
    List<Ficha> listarEnEjecucion();
}
