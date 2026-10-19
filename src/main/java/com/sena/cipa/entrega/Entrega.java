package com.sena.cipa.entrega;

import java.time.LocalDateTime;

import com.sena.cipa.evaluacion.ActividadEvaluativa;
import com.sena.cipa.matricula.Matricula;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "entregas",
        uniqueConstraints = @UniqueConstraint(name = "uq_entregas_actividad_matricula",
                columnNames = {"actividad_id", "matricula_id"}))
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "actividad_id", nullable = false)
    private ActividadEvaluativa actividad;

    @ManyToOne(optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @Column(length = 1000)
    private String comentario;

    @Column(name = "nombre_archivo", length = 255)
    private String nombreArchivo;

    @Column(name = "tipo_contenido", length = 150)
    private String tipoContenido;

    @Column(name = "tamano_bytes")
    private Long tamanoBytes;

    @Column(name = "fecha_entrega", nullable = false)
    private LocalDateTime fechaEntrega;

    public boolean isTieneArchivo() {
        return nombreArchivo != null;
    }

    public boolean isTardia() {
        return fechaEntrega != null && actividad != null && actividad.getFechaLimite() != null
                && fechaEntrega.toLocalDate().isAfter(actividad.getFechaLimite());
    }

    public Long getId() {
        return id;
    }

    public ActividadEvaluativa getActividad() {
        return actividad;
    }

    public void setActividad(ActividadEvaluativa actividad) {
        this.actividad = actividad;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getTipoContenido() {
        return tipoContenido;
    }

    public void setTipoContenido(String tipoContenido) {
        this.tipoContenido = tipoContenido;
    }

    public Long getTamanoBytes() {
        return tamanoBytes;
    }

    public void setTamanoBytes(Long tamanoBytes) {
        this.tamanoBytes = tamanoBytes;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }
}
