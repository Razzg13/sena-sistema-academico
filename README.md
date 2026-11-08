# Sistema Académico SENA

Aplicación web para llevar la formación: programas, fichas, instructores, aprendices,
matrículas, sesiones y asistencia, actividades, entregas y notas. Cada rol (administrador,
coordinación, instructor, aprendiz y auditor) ve y hace solo lo que le corresponde.

Hecha con Java 21, Spring Boot 3 (MVC, Security, Data JPA), Thymeleaf y MySQL.

## Estructura del proyecto (por capas: MVC + DAO + DTO)

```
src/main/
├── java/com/sena/cipa/
│   ├── App.java              Punto de arranque
│   │
│   ├── modelo/               MODELO (M): las entidades, una clase por tabla (13)
│   │   │                     Usuario, Programa, Ficha, Matricula, Entrega, Evaluacion...
│   │   └── enums/            Listas de opciones fijas: roles y estados
│   │                         RolNombre, EstadoFicha, EstadoAsistencia, JuicioEvaluacion...
│   │
│   ├── vista → ver resources/vista/
│   │
│   ├── controlador/          CONTROLADOR (C): recibe cada URL, valida y elige la pantalla
│   │                         FichaController, SesionFormacionController, EntregaController...
│   │
│   ├── dao/                  DAO: acceso a la base de datos (guardar, buscar, borrar)
│   │                         UsuarioDao, FichaDao, MatriculaDao... (Spring Data JPA)
│   ├── dto/                  DTO: datos que viajan entre formularios y pantallas
│   │                         NuevoUsuarioForm, LlamadoForm, CalificarForm, Dashboard
│   ├── servicio/             SERVICIO: reglas de negocio compartidas
│   │                         AccesoFichaService (alcance del instructor), DashboardService
│   │
│   ├── seguridad/            Login, roles y permisos por ruta
│   │                         SecurityConfig, UsuarioDetailsService, UsuarioPrincipal
│   ├── config/               Tareas al arrancar: datos iniciales, reparación de tablas,
│   │                         cuentas de prueba (DatosIniciales, ReparadorEsquema, CuentasDemo)
│   ├── excepcion/            Errores propios y manejo global de errores
│   │                         NegocioException, GlobalExceptionHandler
│   └── util/                 Utilidades (Fechas: hora de Colombia)
│
└── resources/
    ├── vista/                VISTA (V): las páginas HTML (Thymeleaf), una carpeta por módulo
    ├── static/               CSS e imágenes de la vista
    ├── datos-ejemplo.sql     Datos de ejemplo para la base H2 de prueba
    └── application*.properties   Configuración (base de datos, perfiles)

base_de_datos/                Scripts SQL (sena_basico.sql y la versión para Railway)
```

### Cómo viaja una petición

```
Navegador → seguridad (¿puede entrar?) → controlador → servicio (reglas) → dao → base de datos
                                              ↓                                   ↓
                              vista (HTML) ← controlador ← modelo / dto  ←────────┘
                                   ↓
                               Navegador
```

## Ejecutar

Requiere Java 21.

```
mvn clean package -DskipTests
java -jar target/sena-cipa-web.war        # abre http://localhost:8080/
```

También se puede copiar `target/sena-cipa-web.war` a la carpeta `webapps` de un Tomcat 10.1
(queda en `http://localhost:8080/sena-cipa-web/`).

Por defecto usa una base H2 en memoria con datos de ejemplo, y el login muestra las cuentas\nde prueba. Para MySQL, cargar `base_de_datos/sena_basico.sql` y
arrancar con el perfil `mysql`:

```
java -Dspring.profiles.active=mysql -DDB_URL="jdbc:mysql://localhost:3306/sena_basico?allowPublicKeyRetrieval=true&useSSL=false" -DDB_USERNAME=root -DDB_PASSWORD=su_clave -jar target/sena-cipa-web.war
```

## Roles

| Rol | Qué hace |
|---|---|
| Administrador | Todo, incluida la gestión de usuarios |
| Coordinación académica | Programas, fichas, personas, matrículas, sesiones y notas |
| Instructor | Sesiones, asistencia y calificaciones de sus fichas asignadas |
| Aprendiz | Ve su información y entrega actividades |
| Auditor | Consulta de solo lectura |
