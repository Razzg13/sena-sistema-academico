package com.sena.cipa.entrega;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "entrega_archivos")
public class ArchivoEntrega {

    @Id
    private Long id;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "entrega_id")
    private Entrega entrega;

    @Lob
    @Column(name = "contenido", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] contenido;

    public ArchivoEntrega() {
    }

    public ArchivoEntrega(Entrega entrega, byte[] contenido) {
        this.entrega = entrega;
        this.contenido = contenido;
    }

    public Long getId() {
        return id;
    }

    public Entrega getEntrega() {
        return entrega;
    }

    public byte[] getContenido() {
        return contenido;
    }

    public void setContenido(byte[] contenido) {
        this.contenido = contenido;
    }
}
