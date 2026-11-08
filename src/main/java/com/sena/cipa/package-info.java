/**
 * SISTEMA ACADEMICO SENA: organizado por capas (MVC + DAO + DTO).
 *
 * App: punto de arranque.
 * modelo      M  entidades (tablas)        controlador  C  recibe las peticiones
 * dao            acceso a la base          dto             datos de formularios
 * servicio       reglas de negocio         seguridad       login y permisos
 * conexion       conexion a la base        config          tareas al arrancar
 * excepcion      manejo de errores         util            fechas
 * La V (vista) esta en src/main/resources/vista/ y los estilos en static/.
 *
 * Recorrido de una peticion:
 * navegador -> seguridad -> controlador -> servicio -> dao -> conexion -> base de datos
 * y de vuelta: controlador -> vista (HTML) -> navegador.
 */
package com.sena.cipa;
