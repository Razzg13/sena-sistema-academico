package com.sena.cipa.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class Fechas {

    public static final ZoneId ZONA = ZoneId.of("America/Bogota");

    private Fechas() {
    }

    public static LocalDate hoy() {
        return LocalDate.now(ZONA);
    }

    public static LocalDateTime ahora() {
        return LocalDateTime.now(ZONA);
    }
}
