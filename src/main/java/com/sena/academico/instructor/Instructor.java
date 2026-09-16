package com.sena.academico.instructor;

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
import jakarta.validation.constraints.NotBlank;

/**
 * Perfil de instructor (RF-03). Se vincula 1 a 1 con un {@link Usuario} que
 * ya tiene el rol INSTRUCTOR; la vinculacion con fichas vive en el modulo ficha.
 */
@Entity
@Table(name = "instructores", uniqueConstraints = @UniqueConstraint(columnNames = "usuario_id"))
public class Instructor extends AuditableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String especialidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoRegistro estado = EstadoRegistro.ACTIVO;

    protected Instructor() {
    }

    public Instructor(Usuario usuario, String especialidad) {
        this.usuario = usuario;
        this.especialidad = especialidad;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public EstadoRegistro getEstado() {
        return estado;
    }

    public void desactivar() {
        this.estado = EstadoRegistro.INACTIVO;
    }
}
