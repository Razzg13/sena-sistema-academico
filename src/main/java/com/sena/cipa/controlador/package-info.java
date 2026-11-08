/**
 * CONTROLADOR (la C de MVC): recibe cada URL que pide el navegador.
 *
 * Cada metodo tiene @GetMapping (mostrar) o @PostMapping (guardar). Recibe los
 * datos, valida, llama a servicio o dao, pone los datos en el Model y devuelve
 * el nombre de la pantalla de src/main/resources/vista/ que se debe mostrar.
 *
 * Flujo: navegador -> seguridad -> CONTROLADOR -> servicio/dao -> vista -> navegador.
 */
package com.sena.cipa.controlador;
