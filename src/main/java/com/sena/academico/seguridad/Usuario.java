package com.sena.academico.seguridad;

import com.sena.academico.common.AuditableEntity;
import com.sena.academico.common.EstadoRegistro;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.EnumSet;
import java.util.Set;

@Entity
@Table(name = "usuarios", uniqueConstraints = {
        @jakarta.persistence.UniqueConstraint(columnNames = "documento"),
        @jakarta.persistence.UniqueConstraint(columnNames = "correo")
})
public class Usuario extends AuditableEntity {

    @NotBlank
    @Column(nullable = false, length = 20)
    private String documento;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String nombreCompleto;

    @Email
    @NotBlank
    @Column(nullable = false, length = 150)
    private String correo;

    @Column(length = 20)
    private String telefono;

    @NotBlank
    @Column(nullable = false)
    private String credenciales;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoRegistro estado = EstadoRegistro.ACTIVO;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 30)
    private Set<RolNombre> roles = EnumSet.noneOf(RolNombre.class);

    protected Usuario() {
    }

    public Usuario(String documento, String nombreCompleto, String correo, String credenciales, Set<RolNombre> roles) {
        this.documento = documento;
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.credenciales = credenciales;
        this.roles = roles;
    }

    public String getDocumento() {
        return documento;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCredenciales() {
        return credenciales;
    }

    public void setCredenciales(String credenciales) {
        this.credenciales = credenciales;
    }

    public EstadoRegistro getEstado() {
        return estado;
    }

    public void desactivar() {
        this.estado = EstadoRegistro.INACTIVO;
    }

    public void activar() {
        this.estado = EstadoRegistro.ACTIVO;
    }

    public Set<RolNombre> getRoles() {
        return roles;
    }
}
