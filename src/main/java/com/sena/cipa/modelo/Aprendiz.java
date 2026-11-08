package com.sena.cipa.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import com.sena.cipa.modelo.enums.EstadoFormativo;
import com.sena.cipa.modelo.enums.EstadoGeneral;

@Entity
@Table(name = "aprendices")
public class Aprendiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_formativo", nullable = false, length = 20)
    private EstadoFormativo estadoFormativo = EstadoFormativo.EN_FORMACION;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EstadoGeneral estado = EstadoGeneral.ACTIVO;

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public EstadoFormativo getEstadoFormativo() {
        return estadoFormativo;
    }

    public void setEstadoFormativo(EstadoFormativo estadoFormativo) {
        this.estadoFormativo = estadoFormativo;
    }

    public EstadoGeneral getEstado() {
        return estado;
    }

    public void setEstado(EstadoGeneral estado) {
        this.estado = estado;
    }
}
