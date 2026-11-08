/**
 * SEGURIDAD: inicio de sesion, roles y permisos.
 *
 * SecurityConfig: que rol puede entrar a cada URL, pantalla de login, cierre
 * de sesion y cifrado de claves con BCrypt.
 * UsuarioDetailsService: busca el usuario por correo en la base (con UsuarioDao)
 * cuando alguien inicia sesion.
 * UsuarioPrincipal: el usuario que tiene la sesion abierta.
 *
 * Se ejecuta ANTES de cualquier controlador.
 */
package com.sena.cipa.seguridad;
