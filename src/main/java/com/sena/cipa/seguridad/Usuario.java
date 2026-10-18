package com.sena.cipa.seguridad;

import com.sena.cipa.common.EstadoGeneral;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String documento;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    @Column(length = 20)
    private String telefono;

    @Column(nullable = false, length = 255)
    private String clave;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RolNombre rol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EstadoGeneral estado = EstadoGeneral.ACTIVO;

    public Usuario() {
    }

    public Usuario(String documento, String nombreCompleto, String correo, String telefono, String clave, RolNombre rol) {
        this.documento = documento;
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.telefono = telefono;
        this.clave = clave;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public RolNombre getRol() {
        return rol;
    }

    public void setRol(RolNombre rol) {
        this.rol = rol;
    }

    public EstadoGeneral getEstado() {
        return estado;
    }

    public void setEstado(EstadoGeneral estado) {
        this.estado = estado;
    }
}
