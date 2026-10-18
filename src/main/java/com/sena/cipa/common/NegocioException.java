package com.sena.cipa.common;

public class NegocioException extends RuntimeException {

    private final String destino;

    public NegocioException(String mensaje) {
        this(mensaje, null);
    }

    public NegocioException(String mensaje, String destino) {
        super(mensaje);
        this.destino = destino;
    }

    public String getDestino() {
        return destino;
    }
}
