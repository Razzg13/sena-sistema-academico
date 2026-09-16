package com.sena.academico.common;

/**
 * Error de validacion o regla de negocio (RF-17: unicidad, estados, formatos, fechas)
 * que debe mostrarse al usuario en pantalla, no como un error 500.
 */
public class NegocioException extends RuntimeException {

    public NegocioException(String mensaje) {
        super(mensaje);
    }
}
