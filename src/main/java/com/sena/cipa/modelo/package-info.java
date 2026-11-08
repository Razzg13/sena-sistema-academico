/**
 * MODELO (la M de MVC): las entidades.
 *
 * Cada clase es una tabla de la base de datos y cada atributo es una columna
 * (anotaciones @Entity, @Table, @Column). Hay 13: Usuario, Programa, Ficha,
 * Instructor, AsignacionInstructor, Aprendiz, Matricula, SesionFormacion,
 * Asistencia, ActividadEvaluativa, Evaluacion, Entrega y ArchivoEntrega.
 * Las relaciones entre tablas se ven con @ManyToOne / @OneToOne.
 *
 * Se conecta con: dao (que las guarda y las busca), controlador y servicio
 * (que las usan), y vista (que muestra sus datos).
 */
package com.sena.cipa.modelo;
