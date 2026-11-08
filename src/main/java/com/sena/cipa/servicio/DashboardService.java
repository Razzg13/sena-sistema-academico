package com.sena.cipa.servicio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sena.cipa.dao.ActividadEvaluativaDao;
import com.sena.cipa.dao.AprendizDao;
import com.sena.cipa.dao.AsignacionInstructorDao;
import com.sena.cipa.dao.AsistenciaDao;
import com.sena.cipa.dao.EntregaDao;
import com.sena.cipa.dao.EvaluacionDao;
import com.sena.cipa.dao.FichaDao;
import com.sena.cipa.dao.InstructorDao;
import com.sena.cipa.dao.MatriculaDao;
import com.sena.cipa.dao.ProgramaDao;
import com.sena.cipa.dao.SesionFormacionDao;
import com.sena.cipa.dao.UsuarioDao;
import com.sena.cipa.dto.Dashboard.Item;
import com.sena.cipa.dto.Dashboard.Lista;
import com.sena.cipa.dto.Dashboard.Medidor;
import com.sena.cipa.dto.Dashboard.Metrica;
import com.sena.cipa.dto.Dashboard;
import com.sena.cipa.modelo.ActividadEvaluativa;
import com.sena.cipa.modelo.Aprendiz;
import com.sena.cipa.modelo.Asistencia;
import com.sena.cipa.modelo.Entrega;
import com.sena.cipa.modelo.Evaluacion;
import com.sena.cipa.modelo.Ficha;
import com.sena.cipa.modelo.Instructor;
import com.sena.cipa.modelo.Matricula;
import com.sena.cipa.modelo.SesionFormacion;
import com.sena.cipa.modelo.Usuario;
import com.sena.cipa.modelo.enums.EstadoActividad;
import com.sena.cipa.modelo.enums.EstadoAsistencia;
import com.sena.cipa.modelo.enums.EstadoFicha;
import com.sena.cipa.modelo.enums.EstadoFormativo;
import com.sena.cipa.modelo.enums.EstadoGeneral;
import com.sena.cipa.modelo.enums.EstadoMatricula;
import com.sena.cipa.modelo.enums.JuicioEvaluacion;
import com.sena.cipa.modelo.enums.RolNombre;
import com.sena.cipa.util.Fechas;

@Service
public class DashboardService {

    private static final int MAX_ITEMS = 5;
    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter DIA_HORA = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private final UsuarioDao usuarioDao;
    private final ProgramaDao programaDao;
    private final FichaDao fichaDao;
    private final AprendizDao aprendizDao;
    private final InstructorDao instructorDao;
    private final AsignacionInstructorDao asignacionDao;
    private final MatriculaDao matriculaDao;
    private final SesionFormacionDao sesionDao;
    private final AsistenciaDao asistenciaDao;
    private final ActividadEvaluativaDao actividadDao;
    private final EvaluacionDao evaluacionDao;
    private final EntregaDao entregaDao;
    private final AccesoFichaService acceso;

    public DashboardService(UsuarioDao usuarioDao, ProgramaDao programaDao,
                             FichaDao fichaDao, AprendizDao aprendizDao,
                             InstructorDao instructorDao, AsignacionInstructorDao asignacionDao,
                             MatriculaDao matriculaDao, SesionFormacionDao sesionDao,
                             AsistenciaDao asistenciaDao, ActividadEvaluativaDao actividadDao,
                             EvaluacionDao evaluacionDao, EntregaDao entregaDao,
                             AccesoFichaService acceso) {
        this.usuarioDao = usuarioDao;
        this.programaDao = programaDao;
        this.fichaDao = fichaDao;
        this.aprendizDao = aprendizDao;
        this.instructorDao = instructorDao;
        this.asignacionDao = asignacionDao;
        this.matriculaDao = matriculaDao;
        this.sesionDao = sesionDao;
        this.asistenciaDao = asistenciaDao;
        this.actividadDao = actividadDao;
        this.evaluacionDao = evaluacionDao;
        this.entregaDao = entregaDao;
        this.acceso = acceso;
    }

    public Dashboard construir(Usuario usuario) {
        return switch (usuario.getRol()) {
            case ADMINISTRADOR -> administrador();
            case COORDINACION_ACADEMICA -> coordinacion();
            case INSTRUCTOR -> instructor(usuario);
            case APRENDIZ -> aprendiz(usuario);
            case CONSULTA_AUDITOR -> auditor();
        };
    }

    private Dashboard administrador() {
        long cuentas = usuarioDao.count();
        List<Metrica> metricas = List.of(
                new Metrica(String.valueOf(usuarioDao.countByEstado(EstadoGeneral.ACTIVO)), "Usuarios activos",
                        "de " + cuentas + " cuentas", ""),
                new Metrica(String.valueOf(fichaDao.countByEstado(EstadoFicha.EN_EJECUCION)), "Fichas en ejecucion",
                        "de " + fichaDao.count() + " fichas", ""),
                new Metrica(String.valueOf(aprendizDao.countByEstadoFormativo(EstadoFormativo.EN_FORMACION)),
                        "Aprendices en formacion", "de " + aprendizDao.count() + " registrados", ""),
                new Metrica(String.valueOf(instructorDao.countByEstado(EstadoGeneral.ACTIVO)), "Instructores activos",
                        "", ""),
                new Metrica(String.valueOf(matriculaDao.countByEstado(EstadoMatricula.ACTIVA)), "Matriculas activas",
                        "", ""),
                new Metrica(String.valueOf(programaDao.countByEstado(EstadoGeneral.ACTIVO)), "Programas activos",
                        "", ""));

        List<Medidor> medidores = new ArrayList<>();
        for (RolNombre rol : RolNombre.values()) {
            long total = usuarioDao.countByRol(rol);
            medidores.add(new Medidor(nombreRol(rol), porcentaje(total, cuentas), total + " cuenta(s)", ""));
        }

        return new Dashboard("Resumen general", "Estado actual del sistema academico.", null, metricas, medidores,
                List.of(listaFichasSinInstructor(), listaCuentasSinPerfil(), listaActividadesPorVencer(null)));
    }

    private Dashboard coordinacion() {
        List<Ficha> fichas = fichaDao.findAll();
        long sinInstructor = fichasSinInstructor().size();
        List<Metrica> metricas = List.of(
                new Metrica(String.valueOf(programaDao.countByEstado(EstadoGeneral.ACTIVO)), "Programas activos", "", ""),
                new Metrica(String.valueOf(fichaDao.countByEstado(EstadoFicha.EN_EJECUCION)), "Fichas en ejecucion",
                        "de " + fichas.size() + " fichas", ""),
                new Metrica(String.valueOf(aprendizDao.countByEstadoFormativo(EstadoFormativo.EN_FORMACION)),
                        "Aprendices en formacion", "", ""),
                new Metrica(String.valueOf(matriculaDao.countByEstado(EstadoMatricula.ACTIVA)), "Matriculas activas", "", ""),
                new Metrica(String.valueOf(instructorDao.countByEstado(EstadoGeneral.ACTIVO)), "Instructores activos", "", ""),
                new Metrica(String.valueOf(sinInstructor), "Fichas sin instructor",
                        sinInstructor == 0 ? "Todas cubiertas" : "En ejecucion y sin asignar", sinInstructor == 0 ? "ok" : "alerta"));

        List<Medidor> medidores = new ArrayList<>();
        for (EstadoFicha estado : EstadoFicha.values()) {
            long total = fichaDao.countByEstado(estado);
            medidores.add(new Medidor("Fichas " + legible(estado.name()), porcentaje(total, fichas.size()), total + " ficha(s)", ""));
        }
        long registrados = aprendizDao.count();
        for (EstadoFormativo estado : EstadoFormativo.values()) {
            long total = aprendizDao.countByEstadoFormativo(estado);
            medidores.add(new Medidor("Aprendices " + legible(estado.name()), porcentaje(total, registrados),
                    total + " aprendiz(ces)", ""));
        }

        LocalDate hoy = Fechas.hoy();
        List<Item> terminando = fichaDao.findByEstado(EstadoFicha.EN_EJECUCION).stream()
                .filter(f -> f.getFechaFin() != null && !f.getFechaFin().isBefore(hoy) && !f.getFechaFin().isAfter(hoy.plusDays(60)))
                .sorted(Comparator.comparing(Ficha::getFechaFin))
                .limit(MAX_ITEMS)
                .map(f -> new Item("Ficha " + f.getNumero(), f.getPrograma().getNombre(), "Termina " + DIA.format(f.getFechaFin()),
                        "estado-pendiente", "/fichas"))
                .toList();

        return new Dashboard("Resumen academico", "Cobertura de fichas, aprendices e instructores.", null, metricas, medidores,
                List.of(listaFichasSinInstructor(), listaCuentasSinPerfil(),
                        new Lista("Fichas que terminan en 60 dias", "Ninguna ficha termina en los proximos 60 dias.", terminando),
                        listaActividadesPorVencer(null)));
    }

    private Dashboard instructor(Usuario usuario) {
        Optional<Instructor> perfil = instructorDao.findByUsuarioId(usuario.getId());
        if (perfil.isEmpty()) {
            return sinContenido("Tu resumen", "Tu cuenta todavia no tiene un perfil de instructor asociado. "
                    + "Contacta al administrador o a coordinacion academica para que lo cree.");
        }
        Set<Long> fichaIds = acceso.fichasAsignadas(usuario);
        if (fichaIds.isEmpty()) {
            return sinContenido("Tu resumen", "Todavia no tienes fichas asignadas. Cuando coordinacion te asigne una, "
                    + "aqui veras el avance de tus aprendices.");
        }

        LocalDate hoy = Fechas.hoy();
        List<Matricula> matriculas = matriculaDao.findByFichaIdInAndEstado(fichaIds, EstadoMatricula.ACTIVA);
        Map<Long, Long> matriculadosPorFicha = matriculas.stream()
                .collect(Collectors.groupingBy(m -> m.getFicha().getId(), Collectors.counting()));
        List<ActividadEvaluativa> actividades = actividadDao.findByFichaIdIn(fichaIds);
        List<Entrega> entregas = entregaDao.findByActividadFichaIdIn(fichaIds);
        List<Evaluacion> evaluaciones = evaluacionDao.findByActividadFichaIdIn(fichaIds);

        Set<String> calificadas = evaluaciones.stream().filter(e -> e.getCalificacion() != null)
                .map(e -> clave(e.getActividad().getId(), e.getMatricula().getId())).collect(Collectors.toSet());
        List<Entrega> porCalificar = entregas.stream()
                .filter(e -> !calificadas.contains(clave(e.getActividad().getId(), e.getMatricula().getId())))
                .sorted(Comparator.comparing(Entrega::getFechaEntrega))
                .toList();

        List<ActividadEvaluativa> vigentes = actividades.stream().filter(a -> !a.getFechaLimite().isBefore(hoy)).toList();
        List<SesionFormacion> proximas = sesionDao
                .findByFichaIdInAndFechaGreaterThanEqualOrderByFechaAscHoraInicioAsc(fichaIds, hoy);
        long proximasSemana = proximas.stream().filter(s -> !s.getFecha().isAfter(hoy.plusDays(7))).count();

        List<Metrica> metricas = List.of(
                new Metrica(String.valueOf(fichaIds.size()), "Fichas asignadas", "", ""),
                new Metrica(String.valueOf(matriculas.size()), "Aprendices a cargo", "Con matricula activa", ""),
                new Metrica(String.valueOf(porCalificar.size()), "Entregas por calificar",
                        porCalificar.isEmpty() ? "Al dia" : "Esperan tu revision", porCalificar.isEmpty() ? "ok" : "alerta"),
                new Metrica(String.valueOf(proximasSemana), "Sesiones en 7 dias", "", ""),
                new Metrica(String.valueOf(vigentes.size()), "Actividades vigentes", "de " + actividades.size() + " asignadas", ""));

        List<Medidor> medidores = new ArrayList<>();
        medidores.add(medidorAsistencia("Asistencia promedio de tus fichas",
                asistenciaDao.findBySesionFichaIdIn(fichaIds)));

        long esperadas = vigentes.stream().mapToLong(a -> matriculadosPorFicha.getOrDefault(a.getFicha().getId(), 0L)).sum();
        Set<Long> idsVigentes = vigentes.stream().map(ActividadEvaluativa::getId).collect(Collectors.toSet());
        long recibidas = entregas.stream().filter(e -> idsVigentes.contains(e.getActividad().getId())).count();
        medidores.add(medidorProgreso("Entregas recibidas (actividades vigentes)", recibidas, esperadas,
                recibidas + " de " + esperadas + " esperadas"));

        medidores.add(medidorProgreso("Avance de calificacion", entregas.size() - porCalificar.size(), entregas.size(),
                (entregas.size() - porCalificar.size()) + " de " + entregas.size() + " entregas calificadas"));

        List<Item> itemsPorCalificar = porCalificar.stream().limit(MAX_ITEMS)
                .map(e -> new Item(e.getMatricula().getAprendiz().getUsuario().getNombreCompleto(),
                        e.getActividad().getNombre() + " · Ficha " + e.getActividad().getFicha().getNumero(),
                        e.isTardia() ? "Tardia" : "A tiempo", e.isTardia() ? "estado-vencida" : "estado-activa",
                        "/actividades/" + e.getActividad().getId() + "/calificar"))
                .toList();

        List<Item> itemsSesiones = proximas.stream().limit(MAX_ITEMS)
                .map(s -> new Item(s.getTema(), "Ficha " + s.getFicha().getNumero() + " · "
                        + DIA.format(s.getFecha()) + " " + s.getHoraInicio(), "Tomar asistencia", "estado-entregada",
                        "/sesiones/" + s.getId() + "/llamado"))
                .toList();

        Map<Long, Long> entregasPorActividad = entregas.stream()
                .collect(Collectors.groupingBy(e -> e.getActividad().getId(), Collectors.counting()));
        List<Item> itemsActividades = vigentes.stream()
                .sorted(Comparator.comparing(ActividadEvaluativa::getFechaLimite))
                .limit(MAX_ITEMS)
                .map(a -> new Item(a.getNombre(), "Ficha " + a.getFicha().getNumero() + " · Entregas "
                        + entregasPorActividad.getOrDefault(a.getId(), 0L) + " de "
                        + matriculadosPorFicha.getOrDefault(a.getFicha().getId(), 0L),
                        "Vence " + DIA.format(a.getFechaLimite()), "estado-pendiente",
                        "/actividades/" + a.getId() + "/calificar"))
                .toList();

        return new Dashboard("Tu resumen como instructor", "Seguimiento de las fichas que tienes asignadas.", null, metricas,
                medidores, List.of(
                        new Lista("Entregas por calificar", "No tienes entregas pendientes de calificar.", itemsPorCalificar),
                        new Lista("Proximas sesiones", "No hay sesiones programadas. Registra una desde Mis fichas.", itemsSesiones),
                        new Lista("Actividades por vencer", "No hay actividades vigentes en tus fichas.", itemsActividades)));
    }

    private Dashboard aprendiz(Usuario usuario) {
        Optional<Aprendiz> perfil = aprendizDao.findByUsuarioId(usuario.getId());
        if (perfil.isEmpty()) {
            return sinContenido("Tu resumen", "Tu cuenta todavia no tiene un perfil de aprendiz asociado. "
                    + "Contacta al administrador o a coordinacion academica para que lo cree.");
        }
        List<Matricula> activas = matriculaDao.findByAprendizId(perfil.get().getId()).stream()
                .filter(m -> m.getEstado() == EstadoMatricula.ACTIVA).toList();
        if (activas.isEmpty()) {
            return sinContenido("Tu resumen", "No tienes una matricula activa en este momento. "
                    + "Cuando coordinacion te matricule en una ficha veras aqui tu avance.");
        }

        LocalDate hoy = Fechas.hoy();
        List<Long> matriculaIds = activas.stream().map(Matricula::getId).toList();
        Set<Long> fichaIds = activas.stream().map(m -> m.getFicha().getId()).collect(Collectors.toSet());
        Map<String, Entrega> entregas = entregaDao.findByMatriculaIdIn(matriculaIds).stream()
                .collect(Collectors.toMap(e -> clave(e.getActividad().getId(), e.getMatricula().getId()), Function.identity()));
        List<Evaluacion> evaluacionesLista = evaluacionDao.findByMatriculaIdIn(matriculaIds);
        Map<String, Evaluacion> evaluaciones = evaluacionesLista.stream()
                .collect(Collectors.toMap(e -> clave(e.getActividad().getId(), e.getMatricula().getId()), Function.identity()));

        record Fila(ActividadEvaluativa actividad, EstadoActividad estado) {
        }
        List<Fila> filas = new ArrayList<>();
        for (Matricula matricula : activas) {
            for (ActividadEvaluativa actividad : actividadDao.findByFichaId(matricula.getFicha().getId())) {
                String k = clave(actividad.getId(), matricula.getId());
                filas.add(new Fila(actividad, EstadoActividad.de(actividad, entregas.get(k), evaluaciones.get(k), hoy)));
            }
        }
        long pendientes = filas.stream().filter(f -> f.estado() == EstadoActividad.PENDIENTE).count();
        long vencidas = filas.stream().filter(f -> f.estado() == EstadoActividad.VENCIDA).count();
        long completadas = filas.stream()
                .filter(f -> f.estado() == EstadoActividad.ENTREGADA || f.estado() == EstadoActividad.CALIFICADA).count();

        List<Evaluacion> calificadas = evaluacionesLista.stream().filter(e -> e.getCalificacion() != null).toList();
        String promedio = calificadas.isEmpty() ? "-" : calificadas.stream().map(Evaluacion::getCalificacion)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(calificadas.size()), 2, RoundingMode.HALF_UP).toPlainString();

        List<Asistencia> asistencias = asistenciaDao.findByMatriculaIdIn(matriculaIds);
        Medidor medidorAsistencia = medidorAsistencia("Tu asistencia", asistencias);

        List<Metrica> metricas = List.of(
                new Metrica(String.valueOf(activas.size()), "Fichas activas", "", ""),
                new Metrica(medidorAsistencia.detalle().startsWith("Sin") ? "-" : medidorAsistencia.porcentaje() + "%",
                        "Asistencia", medidorAsistencia.detalle(), medidorAsistencia.tono()),
                new Metrica(promedio, "Promedio de notas", calificadas.size() + " calificada(s)", ""),
                new Metrica(String.valueOf(pendientes + vencidas), "Por entregar",
                        vencidas > 0 ? vencidas + " vencida(s)" : (pendientes == 0 ? "Estas al dia" : "Dentro del plazo"),
                        vencidas > 0 ? "critico" : (pendientes > 0 ? "alerta" : "ok")));

        long aprobadas = calificadas.stream().filter(e -> e.getJuicio() == JuicioEvaluacion.APROBADO).count();
        List<Medidor> medidores = List.of(
                medidorAsistencia,
                medidorProgreso("Actividades entregadas", completadas, filas.size(), completadas + " de " + filas.size() + " actividades"),
                medidorProgreso("Actividades aprobadas", aprobadas, calificadas.size(),
                        aprobadas + " de " + calificadas.size() + " calificadas"));

        List<Item> porEntregar = filas.stream()
                .filter(f -> f.estado() == EstadoActividad.PENDIENTE || f.estado() == EstadoActividad.VENCIDA)
                .sorted(Comparator.comparing(f -> f.actividad().getFechaLimite()))
                .limit(MAX_ITEMS)
                .map(f -> new Item(f.actividad().getNombre(), "Ficha " + f.actividad().getFicha().getNumero()
                        + " · Limite " + DIA.format(f.actividad().getFechaLimite()), f.estado().getEtiqueta(),
                        f.estado().getClaseCss(), "/mis-actividades/" + f.actividad().getId()))
                .toList();

        List<Item> ultimas = calificadas.stream()
                .sorted(Comparator.comparing(Evaluacion::getFechaEvaluacion).reversed())
                .limit(MAX_ITEMS)
                .map(e -> new Item(e.getActividad().getNombre(), "Nota " + e.getCalificacion().toPlainString(),
                        legible(e.getJuicio().name()), "estado-" + e.getJuicio().name().toLowerCase(),
                        "/mis-actividades/" + e.getActividad().getId()))
                .toList();

        List<Item> sesiones = sesionDao
                .findByFichaIdInAndFechaGreaterThanEqualOrderByFechaAscHoraInicioAsc(fichaIds, hoy).stream()
                .limit(MAX_ITEMS)
                .map(s -> new Item(s.getTema(), "Ficha " + s.getFicha().getNumero() + " · " + DIA.format(s.getFecha())
                        + " " + s.getHoraInicio() + " - " + s.getHoraFin(), null, "", null))
                .toList();

        return new Dashboard("Tu resumen", "Tu avance en las fichas donde estas matriculado(a).", null, metricas, medidores,
                List.of(new Lista("Actividades por entregar", "No tienes actividades pendientes.", porEntregar),
                        new Lista("Ultimas calificaciones", "Aun no tienes calificaciones.", ultimas),
                        new Lista("Proximas sesiones", "No hay sesiones programadas.", sesiones)));
    }

    private Dashboard auditor() {
        List<Asistencia> asistencias = asistenciaDao.findAll();
        List<Evaluacion> evaluaciones = evaluacionDao.findAll();
        Medidor global = medidorAsistencia("Asistencia global", asistencias);
        long aprobadas = evaluaciones.stream().filter(e -> e.getJuicio() == JuicioEvaluacion.APROBADO).count();

        List<Metrica> metricas = List.of(
                new Metrica(String.valueOf(programaDao.count()), "Programas", "", ""),
                new Metrica(String.valueOf(fichaDao.count()), "Fichas", fichaDao.countByEstado(EstadoFicha.EN_EJECUCION) + " en ejecucion", ""),
                new Metrica(String.valueOf(aprendizDao.count()), "Aprendices", "", ""),
                new Metrica(String.valueOf(instructorDao.count()), "Instructores", "", ""),
                new Metrica(global.detalle().startsWith("Sin") ? "-" : global.porcentaje() + "%", "Asistencia global",
                        global.detalle(), global.tono()),
                new Metrica(evaluaciones.isEmpty() ? "-" : porcentaje(aprobadas, evaluaciones.size()) + "%", "Aprobacion global",
                        evaluaciones.size() + " evaluacion(es)", ""));

        List<Medidor> medidores = new ArrayList<>();
        for (EstadoAsistencia estado : EstadoAsistencia.values()) {
            long total = asistencias.stream().filter(a -> a.getEstado() == estado).count();
            medidores.add(new Medidor("Asistencia: " + legible(estado.name()), porcentaje(total, asistencias.size()),
                    total + " registro(s)", ""));
        }
        for (JuicioEvaluacion juicio : JuicioEvaluacion.values()) {
            long total = evaluaciones.stream().filter(e -> e.getJuicio() == juicio).count();
            medidores.add(new Medidor("Juicio: " + legible(juicio.name()), porcentaje(total, evaluaciones.size()),
                    total + " evaluacion(es)", ""));
        }

        Map<Long, List<Asistencia>> porFicha = asistencias.stream()
                .collect(Collectors.groupingBy(a -> a.getSesion().getFicha().getId()));
        List<Item> menorAsistencia = new ArrayList<>();
        porFicha.entrySet().stream()
                .map(e -> Map.entry(e.getValue().get(0).getSesion().getFicha(), tasaAsistencia(e.getValue())))
                .filter(e -> e.getValue() != null)
                .sorted(Map.Entry.comparingByValue())
                .limit(MAX_ITEMS)
                .forEach(e -> menorAsistencia.add(new Item("Ficha " + e.getKey().getNumero(), e.getKey().getPrograma().getNombre(),
                        e.getValue() + "%", claseTono(tonoAsistencia(e.getValue())), "/auditoria")));

        return new Dashboard("Resumen de auditoria", "Indicadores globales de solo lectura.", null, metricas, medidores,
                List.of(new Lista("Fichas con menor asistencia", "Aun no hay asistencia registrada.", menorAsistencia),
                        listaFichasSinInstructor()));
    }

    private List<Ficha> fichasSinInstructor() {
        return fichaDao.findByEstado(EstadoFicha.EN_EJECUCION).stream()
                .filter(f -> asignacionDao.findByFichaId(f.getId()).isEmpty())
                .toList();
    }

    private Lista listaFichasSinInstructor() {
        List<Item> items = fichasSinInstructor().stream().limit(MAX_ITEMS)
                .map(f -> new Item("Ficha " + f.getNumero(), f.getPrograma().getNombre(), "Sin instructor",
                        "estado-pendiente", "/instructores"))
                .toList();
        return new Lista("Fichas sin instructor", "Todas las fichas en ejecucion tienen instructor.", items);
    }

    private Lista listaCuentasSinPerfil() {
        List<Item> items = new ArrayList<>();
        for (Usuario u : usuarioDao.findAll()) {
            if (u.getEstado() != EstadoGeneral.ACTIVO) {
                continue;
            }
            if (u.getRol() == RolNombre.APRENDIZ && !aprendizDao.existsByUsuarioId(u.getId())) {
                items.add(new Item(u.getNombreCompleto(), u.getCorreo(), "Sin perfil de aprendiz", "estado-pendiente",
                        "/aprendices/nuevo"));
            } else if (u.getRol() == RolNombre.INSTRUCTOR && !instructorDao.existsByUsuarioId(u.getId())) {
                items.add(new Item(u.getNombreCompleto(), u.getCorreo(), "Sin perfil de instructor", "estado-pendiente",
                        "/instructores/nuevo"));
            }
        }
        return new Lista("Cuentas sin perfil", "Todas las cuentas de aprendiz e instructor tienen perfil.",
                items.stream().limit(MAX_ITEMS).toList());
    }

    private Lista listaActividadesPorVencer(Collection<Long> fichaIds) {
        LocalDate hoy = Fechas.hoy();
        List<Item> items = actividadDao.findByFechaLimiteBetweenOrderByFechaLimiteAsc(hoy, hoy.plusDays(7)).stream()
                .filter(a -> fichaIds == null || fichaIds.contains(a.getFicha().getId()))
                .limit(MAX_ITEMS)
                .map(a -> new Item(a.getNombre(), "Ficha " + a.getFicha().getNumero(), "Vence " + DIA.format(a.getFechaLimite()),
                        "estado-pendiente", "/actividades"))
                .toList();
        return new Lista("Actividades por vencer (7 dias)", "No hay actividades que venzan esta semana.", items);
    }

    private Dashboard sinContenido(String titulo, String aviso) {
        return new Dashboard(titulo, "", aviso, List.of(), List.of(), List.of());
    }

    private String clave(Long actividadId, Long matriculaId) {
        return actividadId + ":" + matriculaId;
    }

    private int porcentaje(long parte, long total) {
        return total == 0 ? 0 : (int) Math.round(100.0 * parte / total);
    }

    private Integer tasaAsistencia(Collection<Asistencia> registros) {
        long contables = registros.stream().filter(a -> a.getEstado() != EstadoAsistencia.EXCUSADO).count();
        if (contables == 0) {
            return null;
        }
        long asistio = registros.stream()
                .filter(a -> a.getEstado() == EstadoAsistencia.PRESENTE || a.getEstado() == EstadoAsistencia.TARDANZA).count();
        return porcentaje(asistio, contables);
    }

    private String tonoAsistencia(int porcentaje) {
        return porcentaje >= 80 ? "ok" : porcentaje >= 60 ? "alerta" : "critico";
    }

    private String claseTono(String tono) {
        return switch (tono) {
            case "ok" -> "estado-activa";
            case "alerta" -> "estado-pendiente";
            default -> "estado-vencida";
        };
    }

    private Medidor medidorAsistencia(String etiqueta, Collection<Asistencia> registros) {
        Integer tasa = tasaAsistencia(registros);
        if (tasa == null) {
            return new Medidor(etiqueta, 0, "Sin registros de asistencia", "");
        }
        return new Medidor(etiqueta, tasa, registros.size() + " registro(s)", tonoAsistencia(tasa));
    }

    private Medidor medidorProgreso(String etiqueta, long parte, long total, String detalle) {
        if (total == 0) {
            return new Medidor(etiqueta, 0, "Sin datos todavia", "");
        }
        return new Medidor(etiqueta, porcentaje(parte, total), detalle, "");
    }

    private String nombreRol(RolNombre rol) {
        return switch (rol) {
            case ADMINISTRADOR -> "Administradores";
            case COORDINACION_ACADEMICA -> "Coordinacion academica";
            case INSTRUCTOR -> "Instructores";
            case APRENDIZ -> "Aprendices";
            case CONSULTA_AUDITOR -> "Auditores";
        };
    }

    private String legible(String enumNombre) {
        String texto = enumNombre.toLowerCase().replace('_', ' ');
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }
}
