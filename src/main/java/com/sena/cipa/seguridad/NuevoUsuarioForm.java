package com.sena.cipa.seguridad;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NuevoUsuarioForm {

    @NotBlank
    private String documento;

    @NotBlank
    private String nombreCompleto;

    @NotBlank
    @Email
    private String correo;

    private String telefono;

    @NotBlank
    @Size(min = 6, message = "La clave debe tener al menos 6 caracteres")
    private String clave;

    private RolNombre rol;

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
}
