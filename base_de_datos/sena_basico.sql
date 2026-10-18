-- ============================================================
-- Base de datos basica del Sistema Academico SENA (11 tablas)
-- ============================================================
DROP DATABASE IF EXISTS sena_basico;
CREATE DATABASE sena_basico;
USE sena_basico;

-- 1. usuarios: las personas que usan el sistema (con su clave cifrada y su rol)
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    documento VARCHAR(20) NOT NULL UNIQUE,
    nombre_completo VARCHAR(150) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    clave VARCHAR(255) NOT NULL,
    rol ENUM('ADMINISTRADOR','COORDINACION_ACADEMICA','INSTRUCTOR','APRENDIZ','CONSULTA_AUDITOR') NOT NULL,
    estado ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO'
);

-- 2. programas: catalogo de programas de formacion
CREATE TABLE programas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(200) NOT NULL,
    nivel ENUM('AUXILIAR','COMPLEMENTARIA','ESPECIALIZACION_TECNOLOGICA','OPERARIO','TECNICO','TECNOLOGO') NOT NULL,
    version VARCHAR(10) NOT NULL,
    duracion_horas INT NOT NULL,
    estado ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO'
);

-- 3. fichas: grupo de aprendices que cursa un programa
CREATE TABLE fichas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(20) NOT NULL UNIQUE,
    programa_id BIGINT NOT NULL,
    jornada ENUM('MANANA','TARDE','NOCHE','MIXTA','FIN_DE_SEMANA') NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    ambiente VARCHAR(100) NOT NULL,
    estado ENUM('EN_EJECUCION','APLAZADA','TERMINADA','CANCELADA') NOT NULL DEFAULT 'EN_EJECUCION',
    CONSTRAINT fk_fichas_programa FOREIGN KEY (programa_id) REFERENCES programas(id)
);

-- 4. instructores: extension de un usuario con rol INSTRUCTOR
--    (documento, nombre, correo y telefono ya estan en la tabla usuarios)
CREATE TABLE instructores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    especialidad VARCHAR(150) NOT NULL,
    estado ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT fk_instructores_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 5. asignaciones_instructor: que instructor atiende que ficha (y en que fechas)
CREATE TABLE asignaciones_instructor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ficha_id BIGINT NOT NULL,
    instructor_id BIGINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    CONSTRAINT fk_asignaciones_ficha FOREIGN KEY (ficha_id) REFERENCES fichas(id),
    CONSTRAINT fk_asignaciones_instructor FOREIGN KEY (instructor_id) REFERENCES instructores(id)
);

-- 6. aprendices: extension de un usuario con rol APRENDIZ
--    (documento, nombre, correo y telefono ya estan en la tabla usuarios)
CREATE TABLE aprendices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    estado_formativo ENUM('EN_FORMACION','APLAZADO','RETIRADO','CERTIFICADO') NOT NULL DEFAULT 'EN_FORMACION',
    estado ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT fk_aprendices_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 7. matriculas: une un aprendiz con una ficha (es la tabla bisagra)
CREATE TABLE matriculas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    aprendiz_id BIGINT NOT NULL,
    ficha_id BIGINT NOT NULL,
    fecha_matricula DATE NOT NULL,
    estado ENUM('ACTIVA','CANCELADA') NOT NULL DEFAULT 'ACTIVA',
    CONSTRAINT fk_matriculas_aprendiz FOREIGN KEY (aprendiz_id) REFERENCES aprendices(id),
    CONSTRAINT fk_matriculas_ficha FOREIGN KEY (ficha_id) REFERENCES fichas(id),
    CONSTRAINT uq_matriculas_aprendiz_ficha UNIQUE (aprendiz_id, ficha_id)
);

-- 8. sesiones_formacion: cada clase que se dicta a una ficha
CREATE TABLE sesiones_formacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ficha_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    tema VARCHAR(200) NOT NULL,
    CONSTRAINT fk_sesiones_ficha FOREIGN KEY (ficha_id) REFERENCES fichas(id)
);

-- 9. asistencias: si un aprendiz (matricula) asistio a una sesion
CREATE TABLE asistencias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sesion_id BIGINT NOT NULL,
    matricula_id BIGINT NOT NULL,
    estado ENUM('PRESENTE','AUSENTE','TARDANZA','EXCUSADO') NOT NULL,
    observacion VARCHAR(300),
    CONSTRAINT fk_asistencias_sesion FOREIGN KEY (sesion_id) REFERENCES sesiones_formacion(id),
    CONSTRAINT fk_asistencias_matricula FOREIGN KEY (matricula_id) REFERENCES matriculas(id),
    CONSTRAINT uq_asistencias_sesion_matricula UNIQUE (sesion_id, matricula_id)
);

-- 10. actividades_evaluativas: talleres, pruebas o proyectos de una ficha
CREATE TABLE actividades_evaluativas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ficha_id BIGINT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(500),
    fecha_limite DATE NOT NULL,
    CONSTRAINT fk_actividades_ficha FOREIGN KEY (ficha_id) REFERENCES fichas(id)
);

-- 11. evaluaciones: la nota de un aprendiz (matricula) en una actividad
CREATE TABLE evaluaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    actividad_id BIGINT NOT NULL,
    matricula_id BIGINT NOT NULL,
    calificacion DECIMAL(4,2),
    juicio ENUM('APROBADO','NO_APROBADO','POR_MEJORAR') NOT NULL,
    retroalimentacion VARCHAR(500),
    fecha_evaluacion DATE NOT NULL,
    CONSTRAINT fk_evaluaciones_actividad FOREIGN KEY (actividad_id) REFERENCES actividades_evaluativas(id),
    CONSTRAINT fk_evaluaciones_matricula FOREIGN KEY (matricula_id) REFERENCES matriculas(id),
    CONSTRAINT uq_evaluaciones_actividad_matricula UNIQUE (actividad_id, matricula_id)
);

-- ============================================================
-- Datos de ejemplo (pocos, para ver la base funcionando)
-- ============================================================
-- Clave de TODOS los usuarios de ejemplo: sena1234 (se guarda cifrada con BCrypt)
INSERT INTO usuarios (documento, nombre_completo, correo, telefono, clave, rol) VALUES
    ('1010101', 'Ines Instructora', 'ines@sena.edu.co', '3001112233', '$2a$10$buhwo33DjLN6NqtIOQhksuqYLjPoaFdwkBTHDqTRa3YPXK8vIyVW2', 'INSTRUCTOR'),
    ('1010102', 'Carlos Contador', 'carlos@sena.edu.co', NULL, '$2a$10$buhwo33DjLN6NqtIOQhksuqYLjPoaFdwkBTHDqTRa3YPXK8vIyVW2', 'INSTRUCTOR'),
    ('2020201', 'Alberto Aprendiz', 'alberto@correo.com', '3105556677', '$2a$10$buhwo33DjLN6NqtIOQhksuqYLjPoaFdwkBTHDqTRa3YPXK8vIyVW2', 'APRENDIZ'),
    ('2020202', 'Beatriz Aprendiz', 'beatriz@correo.com', NULL, '$2a$10$buhwo33DjLN6NqtIOQhksuqYLjPoaFdwkBTHDqTRa3YPXK8vIyVW2', 'APRENDIZ'),
    ('2020203', 'Camilo Aprendiz', 'camilo@correo.com', NULL, '$2a$10$buhwo33DjLN6NqtIOQhksuqYLjPoaFdwkBTHDqTRa3YPXK8vIyVW2', 'APRENDIZ');

INSERT INTO programas (codigo, nombre, nivel, version, duracion_horas) VALUES
    ('228106', 'Analisis y Desarrollo de Software', 'TECNOLOGO', '1', 2200),
    ('133101', 'Gestion Contable', 'TECNICO', '1', 880),
    ('233104', 'Bases de Datos Basico', 'COMPLEMENTARIA', '1', 40);

INSERT INTO fichas (numero, programa_id, jornada, fecha_inicio, fecha_fin, ambiente) VALUES
    ('2900001', 1, 'MANANA', '2026-02-16', '2028-02-16', 'Sala de sistemas 1'),
    ('2900002', 2, 'NOCHE', '2026-03-02', '2027-03-02', 'Aula 204');

INSERT INTO instructores (usuario_id, especialidad) VALUES
    (1, 'Desarrollo de software'),
    (2, 'Contabilidad');

INSERT INTO asignaciones_instructor (ficha_id, instructor_id, fecha_inicio, fecha_fin) VALUES
    (1, 1, '2026-02-16', '2027-02-16'),
    (2, 2, '2026-03-02', '2027-03-02');

INSERT INTO aprendices (usuario_id) VALUES
    (3),
    (4),
    (5);

INSERT INTO matriculas (aprendiz_id, ficha_id, fecha_matricula) VALUES
    (1, 1, '2026-02-16'),
    (2, 1, '2026-02-16'),
    (3, 2, '2026-03-02');

INSERT INTO sesiones_formacion (ficha_id, fecha, hora_inicio, hora_fin, tema) VALUES
    (1, '2026-02-17', '08:00:00', '12:00:00', 'Introduccion a bases de datos'),
    (1, '2026-02-18', '08:00:00', '12:00:00', 'Modelo entidad-relacion');

INSERT INTO asistencias (sesion_id, matricula_id, estado, observacion) VALUES
    (1, 1, 'PRESENTE', NULL),
    (1, 2, 'TARDANZA', 'Llego 15 minutos tarde');

INSERT INTO actividades_evaluativas (ficha_id, nombre, descripcion, fecha_limite) VALUES
    (1, 'Taller de modelo entidad-relacion', 'Disenar el modelo de una biblioteca', '2026-03-01');

INSERT INTO evaluaciones (actividad_id, matricula_id, calificacion, juicio, retroalimentacion, fecha_evaluacion) VALUES
    (1, 1, 4.50, 'APROBADO', 'Buen trabajo', '2026-03-02');
