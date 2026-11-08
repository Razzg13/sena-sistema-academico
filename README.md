# Sistema Académico SENA

Aplicación web para llevar la formación: programas, fichas, instructores, aprendices,
matrículas, sesiones y asistencia, actividades, entregas y notas. Cada rol (administrador,
coordinación, instructor, aprendiz y auditor) ve y hace solo lo que le corresponde.

Hecha con Java 21, Spring Boot 3 (MVC, Security, Data JPA), Thymeleaf y MySQL.

## Estructura del proyecto (MVC)

```
src/main/
├── java/com/sena/cipa/
│   ├── App.java              Punto de arranque
│   │
│   ├── modelo/               MODELO: las tablas (entidades) y sus estados (enums)
│   │                         Usuario, Programa, Ficha, Matricula, Entrega, EstadoFicha...
│   ├── repositorio/          MODELO: acceso a la base de datos (consultas)
│   │                         UsuarioRepository, FichaRepository, MatriculaRepository...
│   │
│   ├── controlador/          CONTROLADOR: reciben cada URL, validan y eligen la pantalla
│   │                         FichaController, SesionFormacionController, EntregaController...
│   │
│   ├── servicio/             Lógica compartida
│   │                         AccesoFichaService, DashboardService, DatosIniciales,
│   │                         UsuarioDetailsService
│   ├── dto/                  Objetos para llevar datos entre formularios y pantallas
│   │                         NuevoUsuarioForm, LlamadoForm, CalificarForm, Dashboard
│   ├── seguridad/            Login, roles y permisos por ruta (SecurityConfig)
│   ├── excepcion/            Errores propios (NegocioException)
│   └── util/                 Utilidades (Fechas: hora de Colombia)
│
└── resources/
    ├── templates/            VISTA: las páginas HTML (Thymeleaf), una carpeta por módulo
    ├── static/               VISTA: CSS e imágenes
    └── application*.properties   Configuración (base de datos, perfiles)

base_de_datos/                Scripts SQL (sena_basico.sql y la versión para Railway)
```

### Cómo viaja una petición

```
Navegador → seguridad (¿puede entrar?) → controlador → repositorio → modelo (BD)
                                              ↓
                              vista (plantilla HTML) → Navegador
```

## Ejecutar

Requiere Java 21.

```
mvn clean package -DskipTests
java -jar target/sena-cipa-web.war        # abre http://localhost:8080/
```

También se puede copiar `target/sena-cipa-web.war` a la carpeta `webapps` de un Tomcat 10.1
(queda en `http://localhost:8080/sena-cipa-web/`).

Por defecto usa una base H2 en memoria. Para MySQL, cargar `base_de_datos/sena_basico.sql` y
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
