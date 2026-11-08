/**
 * DTO (Data Transfer Object): datos que viajan entre pantallas y controladores.
 *
 * No son tablas. Sirven para recibir formularios con validaciones
 * (NuevoUsuarioForm, LlamadoForm, CalificarForm) o para armar los datos del
 * tablero de inicio (Dashboard).
 *
 * Flujo: formulario de la vista -> DTO -> controlador -> modelo -> DAO.
 */
package com.sena.cipa.dto;
