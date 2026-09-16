package com.sena.academico.archivo;

import com.sena.academico.common.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Metadatos de un archivo almacenado localmente (RF-13). El contenido real vive en
 * disco bajo el nombre {@link #nombreAlmacenado}; esta entidad solo guarda metadatos.
 */
@Entity
@Table(name = "archivos")
public class Archivo extends AuditableEntity {

    @NotBlank
    @Column(name = "nombre_original", nullable = false, length = 255)
    private String nombreOriginal;

    @NotBlank
    @Column(name = "nombre_almacenado", nullable = false, length = 255, unique = true)
    private String nombreAlmacenado;

    @NotBlank
    @Column(name = "tipo_contenido", nullable = false, length = 150)
    private String tipoContenido;

    @NotNull
    @Positive
    @Column(name = "tamanio_bytes", nullable = false)
    private Long tamanioBytes;

    protected Archivo() {
    }

    public Archivo(String nombreOriginal, String nombreAlmacenado, String tipoContenido, Long tamanioBytes) {
        this.nombreOriginal = nombreOriginal;
        this.nombreAlmacenado = nombreAlmacenado;
        this.tipoContenido = tipoContenido;
        this.tamanioBytes = tamanioBytes;
    }

    public String getNombreOriginal() {
        return nombreOriginal;
    }

    public String getNombreAlmacenado() {
        return nombreAlmacenado;
    }

    public String getTipoContenido() {
        return tipoContenido;
    }

    public Long getTamanioBytes() {
        return tamanioBytes;
    }
}
