-- Datos de ejemplo para la base H2 de prueba (los mismos de base_de_datos/sena_basico.sql).
-- Se cargan solos cuando la app arranca sin MySQL. Clave de todos: sena1234.

INSERT INTO usuarios (documento, nombre_completo, correo, telefono, clave, rol, estado) VALUES
    ('1010101', 'Ines Instructora', 'ines@sena.edu.co', '3001112233', '$2a$10$buhwo33DjLN6NqtIOQhksuqYLjPoaFdwkBTHDqTRa3YPXK8vIyVW2', 'INSTRUCTOR', 'ACTIVO'),
    ('1010102', 'Carlos Contador', 'carlos@sena.edu.co', NULL, '$2a$10$buhwo33DjLN6NqtIOQhksuqYLjPoaFdwkBTHDqTRa3YPXK8vIyVW2', 'INSTRUCTOR', 'ACTIVO'),
    ('2020201', 'Alberto Aprendiz', 'alberto@correo.com', '3105556677', '$2a$10$buhwo33DjLN6NqtIOQhksuqYLjPoaFdwkBTHDqTRa3YPXK8vIyVW2', 'APRENDIZ', 'ACTIVO'),
    ('2020202', 'Beatriz Aprendiz', 'beatriz@correo.com', NULL, '$2a$10$buhwo33DjLN6NqtIOQhksuqYLjPoaFdwkBTHDqTRa3YPXK8vIyVW2', 'APRENDIZ', 'ACTIVO'),
    ('2020203', 'Camilo Aprendiz', 'camilo@correo.com', NULL, '$2a$10$buhwo33DjLN6NqtIOQhksuqYLjPoaFdwkBTHDqTRa3YPXK8vIyVW2', 'APRENDIZ', 'ACTIVO');

INSERT INTO programas (codigo, nombre, nivel, version, duracion_horas, estado) VALUES
    ('228106', 'Analisis y Desarrollo de Software', 'TECNOLOGO', '1', 2200, 'ACTIVO'),
    ('133101', 'Gestion Contable', 'TECNICO', '1', 880, 'ACTIVO'),
    ('233104', 'Bases de Datos Basico', 'COMPLEMENTARIA', '1', 40, 'ACTIVO');

INSERT INTO fichas (numero, programa_id, jornada, fecha_inicio, fecha_fin, ambiente, estado) VALUES
    ('2900001', 1, 'MANANA', '2026-02-16', '2028-02-16', 'Sala de sistemas 1', 'EN_EJECUCION'),
    ('2900002', 2, 'NOCHE', '2026-03-02', '2027-03-02', 'Aula 204', 'EN_EJECUCION');

INSERT INTO instructores (usuario_id, especialidad, estado) VALUES
    (1, 'Desarrollo de software', 'ACTIVO'),
    (2, 'Contabilidad', 'ACTIVO');

INSERT INTO asignaciones_instructor (ficha_id, instructor_id, fecha_inicio, fecha_fin) VALUES
    (1, 1, '2026-02-16', '2027-02-16'),
    (2, 2, '2026-03-02', '2027-03-02');

INSERT INTO aprendices (usuario_id, estado_formativo, estado) VALUES
    (3, 'EN_FORMACION', 'ACTIVO'),
    (4, 'EN_FORMACION', 'ACTIVO'),
    (5, 'EN_FORMACION', 'ACTIVO');

INSERT INTO matriculas (aprendiz_id, ficha_id, fecha_matricula, estado) VALUES
    (1, 1, '2026-02-16', 'ACTIVA'),
    (2, 1, '2026-02-16', 'ACTIVA'),
    (3, 2, '2026-03-02', 'ACTIVA');

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
