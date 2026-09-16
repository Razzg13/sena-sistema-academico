package com.sena.academico.archivo;

import com.sena.academico.archivo.Archivo;
import com.sena.academico.archivo.ArchivoRepository;
import com.sena.academico.common.NegocioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Guarda y sirve archivos en el disco local (RF-13: "almacenamiento local, validacion
 * de tipo/tamano y descarga autorizada"). La autorizacion de descarga vive en
 * {@code evidencia.EvidenciaService}, esta clase solo se ocupa del disco.
 */
@Service
public class ArchivoAlmacenamientoService {

    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
            "application/pdf",
            "image/png",
            "image/jpeg",
            "application/zip",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );
    private static final long TAMANIO_MAXIMO_BYTES = 10L * 1024 * 1024; // 10 MB

    private final ArchivoRepository archivoRepository;
    private final Path directorioAlmacenamiento;

    public ArchivoAlmacenamientoService(ArchivoRepository archivoRepository,
                                         @Value("${sena.almacenamiento.ruta:./almacenamiento}") String rutaAlmacenamiento) {
        this.archivoRepository = archivoRepository;
        this.directorioAlmacenamiento = Paths.get(rutaAlmacenamiento).toAbsolutePath().normalize();
        try {
            Files.createDirectories(directorioAlmacenamiento);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear el directorio de almacenamiento local.", e);
        }
    }

    @Transactional
    public Archivo guardar(MultipartFile archivoSubido) {
        if (archivoSubido == null || archivoSubido.isEmpty()) {
            throw new NegocioException("Debes seleccionar un archivo.");
        }
        if (archivoSubido.getSize() > TAMANIO_MAXIMO_BYTES) {
            throw new NegocioException("El archivo supera el tamaño máximo permitido (10 MB).");
        }
        String tipoContenido = archivoSubido.getContentType();
        if (tipoContenido == null || !TIPOS_PERMITIDOS.contains(tipoContenido)) {
            throw new NegocioException("Tipo de archivo no permitido. Tipos aceptados: PDF, imágenes, ZIP, Word.");
        }

        String nombreOriginal = archivoSubido.getOriginalFilename() != null
                ? archivoSubido.getOriginalFilename() : "archivo";
        String extension = obtenerExtension(nombreOriginal);
        String nombreAlmacenado = UUID.randomUUID() + extension;

        Path destino = directorioAlmacenamiento.resolve(nombreAlmacenado).normalize();
        if (!destino.getParent().equals(directorioAlmacenamiento)) {
            throw new NegocioException("Nombre de archivo inválido.");
        }

        try {
            Files.copy(archivoSubido.getInputStream(), destino);
        } catch (IOException e) {
            throw new NegocioException("No se pudo guardar el archivo en el disco local.");
        }

        Archivo archivo = new Archivo(nombreOriginal, nombreAlmacenado, tipoContenido, archivoSubido.getSize());
        return archivoRepository.save(archivo);
    }

    public Resource cargarComoRecurso(Archivo archivo) {
        try {
            Path ruta = directorioAlmacenamiento.resolve(archivo.getNombreAlmacenado()).normalize();
            Resource recurso = new UrlResource(ruta.toUri());
            if (!recurso.exists() || !recurso.isReadable()) {
                throw new NegocioException("El archivo ya no está disponible en el almacenamiento local.");
            }
            return recurso;
        } catch (MalformedURLException e) {
            throw new NegocioException("Ruta de archivo inválida.");
        }
    }

    private String obtenerExtension(String nombreOriginal) {
        int puntoIndex = nombreOriginal.lastIndexOf('.');
        return puntoIndex >= 0 ? nombreOriginal.substring(puntoIndex) : "";
    }

    public static List<String> tiposPermitidos() {
        return TIPOS_PERMITIDOS.stream().sorted().toList();
    }
}
