package com.sena.academico.seguridad;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class NuevoUsuarioForm {

    @NotBlank
    private String documento;

    @NotBlank
    private String nombreCompleto;

    @Email
    @NotBlank
    private String correo;

    @NotBlank
    @Size(min = 6, message = "La clave debe tener al menos 6 caracteres.")
    private String clave;

    @NotEmpty(message = "Selecciona al menos un rol.")
    private Set<RolNombre> roles;

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

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Set<RolNombre> getRoles() {
        return roles;
    }

    public void setRoles(Set<RolNombre> roles) {
        this.roles = roles;
    }
}
