# Sistema Academico SENA

Aplicacion web para la gestion academica: programas, fichas, instructores, aprendices,
matriculas, asistencia, actividades, entregas y calificaciones.

Tecnologias: Java 21, Spring Boot 3, Spring Security, Spring Data JPA, Thymeleaf y MySQL.

## Estructura

```
src/main/java/com/sena/cipa
  conexion      conexion a la base de datos
  modelo        entidades (tablas)
  modelo/enums  roles y estados
  dao           acceso a datos
  dto           formularios
  controlador   controladores
  servicio      logica de negocio
  seguridad     login y permisos
  config        datos iniciales
  excepcion     manejo de errores
  util          utilidades

src/main/resources
  vista         paginas html (thymeleaf)
  static        css e imagenes
  application.properties          base H2 de prueba
  application-mysql.properties    MySQL local
  application-railway.properties  MySQL en Railway

base_de_datos   scripts sql
```

## Ejecutar

```
mvn clean package -DskipTests
java -jar target/sena-cipa-web.war
```

Abrir http://localhost:8080

Con MySQL: ejecutar `base_de_datos/sena_basico.sql` en MySQL Workbench y arrancar asi:

```
java -jar target/sena-cipa-web.war --spring.profiles.active=mysql --DB_URL="jdbc:mysql://localhost:3306/sena_basico?allowPublicKeyRetrieval=true&useSSL=false" --DB_USERNAME=root --DB_PASSWORD=clave
```

En Tomcat 10.1 se copia el war a la carpeta webapps.

## Usuarios de prueba

| Correo | Clave | Rol |
|---|---|---|
| admin@sena.edu.co | admin123 | Administrador |
| coordinacion.demo@sena.edu.co | coordinacion123 | Coordinacion |
| instructor.demo@sena.edu.co | instructor123 | Instructor |
| aprendiz.demo@sena.edu.co | aprendiz123 | Aprendiz |
| auditor.demo@sena.edu.co | auditor123 | Auditor |

Los usuarios del script (ines, carlos, alberto, beatriz, camilo) tienen la clave sena1234.
