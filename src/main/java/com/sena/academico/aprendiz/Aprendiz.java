package com.sena.academico.aprendiz;

import com.sena.academico.common.AuditableEntity;
import com.sena.academico.common.EstadoRegistro;
import com.sena.academico.seguridad.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Perfil de aprendiz (RF-03). Se vincula 1 a 1 con un {@link Usuario} que ya tiene
 * el rol APRENDIZ; la matricula a fichas vive en el futuro modulo matricula.
 */
@Entity
@Table(name = "aprendices", uniqueConstraints = @UniqueConstraint(columnNames = "usuario_id"))
public class Aprendiz extends AuditableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_formativo", nullable = false, length = 20)
    private EstadoFormativo estadoFormativo = EstadoFormativo.EN_FORMACION;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoRegistro estado = EstadoRegistro.ACTIVO;

    protected Aprendiz() {
    }

    public Aprendiz(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public EstadoFormativo getEstadoFormativo() {
        return estadoFormativo;
    }

    public void cambiarEstadoFormativo(EstadoFormativo nuevoEstado) {
        this.estadoFormativo = nuevoEstado;
    }

    public EstadoRegistro getEstado() {
        return estado;
    }

    public void desactivar() {
        this.estado = EstadoRegistro.INACTIVO;
    }
}
