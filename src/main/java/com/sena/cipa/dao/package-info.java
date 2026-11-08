/**
 * DAO (Data Access Object): acceso a la base de datos.
 *
 * Una interfaz por tabla (UsuarioDao, FichaDao, MatriculaDao...). Extienden
 * JpaRepository, que ya trae save, findById, findAll y delete. Las consultas
 * extra se escriben solo con el nombre del metodo (por ejemplo findByCorreo) y
 * Spring Data genera el SQL.
 *
 * Flujo: controlador o servicio -> DAO -> Hibernate -> conexion -> base de datos.
 * Todos los datos del sistema se guardan y se leen por aqui.
 */
package com.sena.cipa.dao;
