# Sistema Académico SENA

Aplicación web local para centralizar procesos académicos básicos de un centro de formación SENA
(identidades y roles, instructores, aprendices, programas, fichas, matrículas, asistencia,
evaluación, evidencias, archivos, reportes PDF y auditoría).

Basado en el documento `01_Definicion_Integral_Proyecto_SENA`.

## Stack

- Java 21 (ejecuta sobre JDK 25 instalado, compila con `--release 21`)
- Spring Boot 4.1.1 (Web, Security, Data JPA, Thymeleaf, Validation, DevTools)
- H2 en memoria para desarrollo (perfil `dev`) — MySQL listo para producción local (perfil `mysql`)
- Maven (usa el wrapper `mvnw` incluido, no necesitas Maven instalado)

## Cómo ejecutar

```bash
./mvnw spring-boot:run
```

Abre `http://localhost:8080`. Te redirige a `/login`.

**Usuario administrador inicial** (se crea automáticamente al arrancar si no existe):

- Correo: `admin@sena.edu.co`
- Clave: `admin123`

**Usuario instructor de demostración** (también se crea solo, para poder probar la asignación de
instructores a fichas sin tener que dar de alta un usuario manualmente):

- Correo: `instructor.demo@sena.edu.co`
- Clave: `instructor123`

**Usuario aprendiz de demostración** (mismo propósito, con perfil de aprendiz ya creado):

- Correo: `aprendiz.demo@sena.edu.co`
- Clave: `aprendiz123`

**Usuarios de coordinación académica y auditoría** (para poder probar esos dos roles, que no
tienen un perfil propio como instructor/aprendiz — solo son un usuario con el rol):

- Coordinación: `coordinacion.demo@sena.edu.co` / `coordinacion123`
- Auditor: `auditor.demo@sena.edu.co` / `auditor123`

Cámbialas cuando implementes la gestión de usuarios (RF-01, RF-17).

## Cambiar a MySQL

1. Instala MySQL y crea la base de datos:
   ```sql
   CREATE DATABASE sena_academico;
   ```
2. Edita `src/main/resources/application-mysql.properties` con tu usuario/clave.
3. En `src/main/resources/application.properties` cambia:
   ```properties
   spring.profiles.active=mysql
   ```

## Estructura del proyecto

Organizado por módulos (package by feature): un paquete por módulo, con todas sus clases juntas
(entidad, repositorio, servicio, controlador) en vez de subcarpetas por capa:

```
com.sena.academico
├── common       → AuditableEntity (base con auditoría), EstadoRegistro (borrado lógico), NegocioException
├── config       → SecurityConfig, JpaAuditingConfig, GlobalControllerAdvice, NavegacionController
├── seguridad    → Usuario, roles (RolNombre), autenticación
├── auditoria    → bitácora de operaciones críticas
├── programa     → programas, competencias, resultados de aprendizaje
├── ficha        → fichas, asignación de instructores
├── instructor   → perfil de instructor
├── aprendiz     → perfil de aprendiz
├── matricula    → matrícula aprendiz + ficha
├── asistencia   → sesiones y llamado a lista
├── evaluacion   → actividades, calificaciones y juicios
├── evidencia    → entregas ligadas a actividad + archivo
├── archivo      → almacenamiento local, validación y descarga
└── reporte      → PDFs de asistencia, calificaciones, evidencias, consolidado y trazabilidad
```

Todos los módulos están implementados. Antes cada módulo estaba dividido en subpaquetes
`domain/repository/service/web`; se aplanó a un solo paquete por módulo para que la estructura
sea más simple de recorrer.

Cada módulo stub tiene un `package-info.java` con su responsabilidad tomada del documento
(sección "4. Alcance funcional") y las carpetas `domain/repository/service/web` ya creadas.

Los estilos compartidos de todas las páginas están en `src/main/resources/static/css/main.css`
(una sola hoja de estilos, para no repetir CSS en cada plantilla nueva).

## Lo que ya funciona (base de seguridad + auditoría)

- **RF-01** Autenticación con Spring Security (formulario, BCrypt).
- **RF-02** Un usuario puede tener uno o varios roles (`ADMINISTRADOR`, `COORDINACION_ACADEMICA`,
  `INSTRUCTOR`, `APRENDIZ`, `CONSULTA_AUDITOR`) — ver tabla "5. Actores y permisos" del documento.
- Reglas de acceso por ruta (`/admin/**`, `/coordinacion/**`, `/instructor/**`, `/aprendiz/**`,
  `/reportes/**`, `/auditoria/**`) ya mapeadas en `SecurityConfig` según esos roles — ajusta los
  prefijos cuando crees los controladores de cada módulo.
- Auditoría automática de `fecha_creacion`, `fecha_actualizacion`, `creado_por`, `actualizado_por`
  en cualquier entidad que extienda `AuditableEntity` (usa Spring Data JPA Auditing).
- Borrado lógico vía enum `EstadoRegistro` (ACTIVO/INACTIVO), reutilizable en cualquier módulo (RF-18).
- **RF-15/RF-16** Módulo `auditoria/` completo: entidad `RegistroAuditoria` (usuario, acción,
  entidad, identificador, fecha, IP, detalle), `AuditoriaService.registrar(...)` para que
  cualquier módulo registre una operación crítica, y una pantalla `/auditoria` con filtros
  (usuario, entidad, acción, rango de fechas) y paginación, visible para `ADMINISTRADOR` y
  `CONSULTA_AUDITOR`.
- **RF-04** Módulo `programa/`: alta de programas (código, nombre, nivel, versión, duración),
  validación de código único, competencias y resultados de aprendizaje anidados. Pantallas en
  `/coordinacion/programas`. Cada alta/cambio queda en la bitácora de auditoría.
- **RF-05/RF-07** Módulo `ficha/`: alta de fichas (número único, programa, jornada, fechas,
  ambiente, estado), validación de fechas y número único, y asignación de instructores con rango
  de fechas (`AsignacionInstructor`). Pantallas en `/coordinacion/fichas`.
- Módulo `instructor/` (versión mínima, RF-03 parcial): perfil de instructor vinculado 1 a 1 a un
  `Usuario` que ya tenga el rol INSTRUCTOR, con especialidad. Pantalla en `/coordinacion/instructores`.
- Módulo `aprendiz/` (versión mínima, RF-03 parcial): perfil de aprendiz vinculado 1 a 1 a un
  `Usuario` con rol APRENDIZ, con estado formativo (EN_FORMACION, APLAZADO, RETIRADO, CERTIFICADO).
  Pantalla en `/coordinacion/aprendices`. Falta en ambos: edición y desactivación desde la pantalla.
- **RF-06** Módulo `matricula/`: vincula un `Aprendiz` con una `Ficha`, validando que no exista ya
  una matrícula ACTIVA para ese mismo par (evita duplicados). Se matricula/cancela desde el
  detalle de la ficha (`/coordinacion/fichas/{id}`), y hay un listado general en
  `/coordinacion/matriculas`.
- **RF-08/RF-09** Módulo `asistencia/`: se programan sesiones de formación desde el detalle de la
  ficha (fecha, horario, tema), y desde cada sesión se abre un "llamado a lista"
  (`/coordinacion/sesiones/{id}/llamado`) con un aprendiz por fila (solo los matriculados
  ACTIVA), donde se marca PRESENTE/AUSENTE/TARDANZA/EXCUSADO y una observación. Guardar es
  idempotente: si ya existe un registro para esa sesión+matrícula lo actualiza en vez de
  duplicarlo, y al volver a abrir el llamado se precargan los valores guardados.
- **RF-10/RF-11** Módulo `evaluacion/`: se crean actividades evaluativas desde el detalle de la
  ficha, cada una asociada a un `ResultadoAprendizaje` concreto del programa (el desplegable se
  arma agrupando por competencia). Desde cada actividad se abre una pantalla de calificación
  (`/coordinacion/actividades/{id}/calificar`), un aprendiz por fila, con calificación numérica,
  juicio de evaluación (APROBADO/POR_MEJORAR/NO_APROBADO) y retroalimentación. Mismo patrón
  idempotente que Asistencia (actualiza si ya existe en vez de duplicar).
- **RF-12/RF-13** Módulos `archivo/` + `evidencia/`: desde la pantalla de calificar se recibe un
  archivo por aprendiz y actividad (`ArchivoAlmacenamientoService` valida tipo MIME —PDF, PNG,
  JPG, ZIP, Word— y tamaño máximo 10 MB, y lo guarda en `./almacenamiento` con nombre UUID para
  evitar colisiones/path traversal). El personal puede cambiar el estado de la evidencia
  (ENTREGADA/ACEPTADA/RECHAZADA). La descarga (`/coordinacion/evidencias/{id}/descargar`) exige
  autorización explícita por evidencia, no solo por rol: `ADMINISTRADOR`/`COORDINACION_ACADEMICA`/
  `CONSULTA_AUDITOR` siempre pueden, un `INSTRUCTOR` solo si está asignado a la ficha de esa
  actividad, y un `APRENDIZ` solo si la evidencia es suya — verificado con los tres casos vía
  `curl` (dueño → 200, instructor sin asignar → 403, instructor asignado → 200). Nota de diseño:
  esta ruta necesitó una excepción explícita en `SecurityConfig` antes de la regla general
  `/coordinacion/**` (que solo permite ADMINISTRADOR/COORDINACION_ACADEMICA), porque si no,
  instructores y aprendices nunca llegaban a que `EvidenciaService` evaluara su caso.
- **RF-14** Módulo `reporte/`: genera PDFs (librería OpenPDF) con encabezado común ("SENA |
  Sistema Académico", título, fecha de generación y filtros aplicados — tal como exige el RF).
  Cuatro reportes por ficha (enlaces en el detalle de la ficha): **Consolidado** (resumen +
  aprendices matriculados), **Asistencia** (por sesión), **Calificaciones** (por actividad) y
  **Evidencias**. Más un quinto de **trazabilidad** (`/reportes/auditoria`), que exporta a PDF la
  bitácora de auditoría respetando los mismos filtros que la pantalla `/auditoria` (hay un botón
  "Exportar a PDF" ahí que los preserva). Todos bajo `/reportes/**`, ya restringido a
  ADMINISTRADOR/COORDINACION_ACADEMICA/CONSULTA_AUDITOR. Verificados los 5 con `curl` (PDF válido,
  content-type correcto) y renderizado visualmente uno para confirmar que las tildes se ven bien
  en pantalla (la extracción de texto plano vía `pdftotext` las muestra mal por una limitación de
  las fuentes base de OpenPDF sin CMap, pero es solo un problema de copiar texto, no de cómo se
  ve el PDF).
- `NegocioException` + `GlobalControllerAdvice`: cualquier validación de negocio (códigos
  duplicados, fechas inválidas, etc. — RF-17) se lanza como excepción y se muestra en pantalla
  como mensaje de error, sin necesidad de repetir ese manejo en cada controlador.

## Próximos pasos sugeridos

Todos los módulos funcionales del documento (RF-01 a RF-14) están implementados. Lo que sigue es
trabajo de pulido, no módulos nuevos:

1. ~~**Autogestión del instructor**~~ — Implementada. Un usuario con SOLO el rol INSTRUCTOR (sin
   ADMINISTRADOR/COORDINACION_ACADEMICA) que entra a `/coordinacion/fichas` ve únicamente las
   fichas donde tiene una `AsignacionInstructor` ("Mis fichas" en el inicio), y `/coordinacion/
   fichas/{id}` rechaza el acceso (`NegocioException` → redirige con error) si intenta ver una
   ficha ajena adivinando el ID. Dentro de su ficha puede programar sesiones, tomar asistencia,
   crear actividades evaluativas, calificar y recibir/evaluar evidencias (coincide con la
   sección 5 del documento: "Instructor: Gestiona sesiones, asistencia, actividades,
   calificaciones, retroalimentación y evidencias de **fichas asignadas**"). Lo que NO puede
   hacer (oculto en la plantilla y bloqueado en `SecurityConfig`, verificado con `curl`: 403):
   cambiar el estado de la ficha, asignar otros instructores, matricular aprendices, crear
   fichas/programas nuevos, ni generar los reportes PDF — eso sigue siendo de
   ADMINISTRADOR/COORDINACION_ACADEMICA. Nota de diseño: la regla de ruta en `SecurityConfig`
   solo verifica el rol; el filtrado real por ficha vive en `FichaService.listarPorInstructor` /
   `estaAsignado`, llamado desde `FichaController`.
2. ~~**Autogestión del aprendiz**~~ — Implementada. Bajo `/aprendiz/fichas` un aprendiz ve solo
   sus propias matrículas ("Mis fichas"), y `/aprendiz/fichas/{id}` verifica la propiedad via
   `MatriculaService.obtenerPropia(fichaId, usuarioId)` (si no está matriculado ahí, `NegocioException`
   → redirige con error, igual que con el instructor). A diferencia de las pantallas de personal
   (que muestran el curso completo), esta vista está acotada a sus propios datos: su asistencia
   por sesión, su calificación/juicio/retroalimentación por actividad, y el estado de su propia
   evidencia — nunca los datos de otros aprendices. Puede entregar evidencia
   (`POST /aprendiz/actividades/{id}/evidencias`) solo para su propia matrícula (el controlador
   resuelve la matrícula a partir de `fichaId` + su propio usuario, ignorando cualquier
   `matriculaId` que pudiera intentar falsificar). Descarga su propia evidencia con la misma ruta
   y autorización que ya existían para RF-13. Verificado con `curl`: sin matrícula no ve nada y
   el acceso al detalle es rechazado; matriculado, ve y opera solo sobre sus propios datos.
3. **Edición/desactivación** de Instructor y Aprendiz desde su propia pantalla (hoy solo se crean
   y, en el caso de Aprendiz, se les cambia el estado formativo).
4. **RNF pendientes**: paginación consistente en todos los listados (hoy Auditoría, Programas y
   Fichas la tienen; "Mis fichas" del instructor no pagina porque en la práctica son pocas),
   índices en BD al migrar a MySQL, y pruebas automatizadas (unitarias/integración) — el
   documento pide "servicios, pruebas y convenciones de nombres" como parte de la
   mantenibilidad, y hasta ahora todo se verificó manualmente en el navegador o con `curl`.

## Revisión de permisos por rol (verificada con `curl`)

Hasta ahora todo se había probado con ADMINISTRADOR, INSTRUCTOR y APRENDIZ. Faltaban
COORDINACION_ACADEMICA y CONSULTA_AUDITOR — se agregaron usuarios de prueba para ellos (ver
arriba) y se verificó que la matriz de permisos coincide con la tabla "5. Actores y permisos"
del documento:

| Acción | ADMINISTRADOR | COORDINACION_ACADEMICA | INSTRUCTOR | APRENDIZ | CONSULTA_AUDITOR |
|---|---|---|---|---|---|
| Gestionar programas/fichas/instructores/aprendices/matrículas | ✅ | ✅ | ❌ | ❌ | ❌ |
| Ver/gestionar sus fichas asignadas (sesiones, calificar, evidencias) | ✅ | ✅ | ✅ (solo asignadas) | ❌ | ❌ |
| Ver/gestionar su propia matrícula (asistencia, resultados, evidencias) | ✅ | ❌ | ❌ | ✅ (solo la suya) | ❌ |
| Ver `/auditoria` (pantalla filtrable) | ✅ | ❌ | ❌ | ❌ | ✅ |
| Generar reportes PDF (incluida trazabilidad) | ✅ | ✅ | ❌ | ❌ | ✅ |

La fila que podría sorprender: COORDINACION_ACADEMICA puede **generar el reporte PDF de
trazabilidad** pero no puede abrir la pantalla `/auditoria`. No es un descuido — el documento le
da a Coordinación "reportes" explícitamente (y el RF-14 lista trazabilidad como uno de los
reportes), pero solo ADMINISTRADOR y CONSULTA_AUDITOR tienen la pantalla de auditoría en vivo
según la tabla de actores.

Sigue el orden anterior o dime qué módulo quieres que implementemos primero.
