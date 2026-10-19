package com.sena.cipa.dashboard;

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

import com.sena.cipa.aprendiz.Aprendiz;
import com.sena.cipa.aprendiz.AprendizRepository;
import com.sena.cipa.aprendiz.EstadoActividad;
import com.sena.cipa.aprendiz.EstadoFormativo;
import com.sena.cipa.asistencia.Asistencia;
import com.sena.cipa.asistencia.AsistenciaRepository;
import com.sena.cipa.asistencia.EstadoAsistencia;
import com.sena.cipa.asistencia.SesionFormacion;
import com.sena.cipa.asistencia.SesionFormacionRepository;
import com.sena.cipa.common.EstadoGeneral;
import com.sena.cipa.common.Fechas;
import com.sena.cipa.dashboard.Dashboard.Item;
import com.sena.cipa.dashboard.Dashboard.Lista;
import com.sena.cipa.dashboard.Dashboard.Medidor;
import com.sena.cipa.dashboard.Dashboard.Metrica;
import com.sena.cipa.entrega.Entrega;
import com.sena.cipa.entrega.EntregaRepository;
import com.sena.cipa.evaluacion.ActividadEvaluativa;
import com.sena.cipa.evaluacion.ActividadEvaluativaRepository;
import com.sena.cipa.evaluacion.Evaluacion;
import com.sena.cipa.evaluacion.EvaluacionRepository;
import com.sena.cipa.evaluacion.JuicioEvaluacion;
import com.sena.cipa.ficha.EstadoFicha;
import com.sena.cipa.ficha.Ficha;
import com.sena.cipa.ficha.FichaRepository;
import com.sena.cipa.instructor.AsignacionInstructorRepository;
import com.sena.cipa.instructor.Instructor;
import com.sena.cipa.instructor.InstructorRepository;
import com.sena.cipa.matricula.EstadoMatricula;
import com.sena.cipa.matricula.Matricula;
import com.sena.cipa.matricula.MatriculaRepository;
import com.sena.cipa.programa.ProgramaRepository;
import com.sena.cipa.seguridad.AccesoFichaService;
import com.sena.cipa.seguridad.RolNombre;
import com.sena.cipa.seguridad.Usuario;
import com.sena.cipa.seguridad.UsuarioRepository;

@Service
public class DashboardService {

    private static final int MAX_ITEMS = 5;
    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter DIA_HORA = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private final UsuarioRepository usuarioRepository;
    private final ProgramaRepository programaRepository;
    private final FichaRepository fichaRepository;
    private final AprendizRepository aprendizRepository;
    private final InstructorRepository instructorRepository;
    private final AsignacionInstructorRepository asignacionRepository;
    private final MatriculaRepository matriculaRepository;
    private final SesionFormacionRepository sesionRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final ActividadEvaluativaRepository actividadRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final EntregaRepository entregaRepository;
    private final AccesoFichaService acceso;

    public DashboardService(UsuarioRepository usuarioRepository, ProgramaRepository programaRepository,
                             FichaRepository fichaRepository, AprendizRepository aprendizRepository,
                             InstructorRepository instructorRepository, AsignacionInstructorRepository asignacionRepository,
                             MatriculaRepository matriculaRepository, SesionFormacionRepository sesionRepository,
                             AsistenciaRepository asistenciaRepository, ActividadEvaluativaRepository actividadRepository,
                             EvaluacionRepository evaluacionRepository, EntregaRepository entregaRepository,
                             AccesoFichaService acceso) {
        this.usuarioRepository = usuarioRepository;
        this.programaRepository = programaRepository;
        this.fichaRepository = fichaRepository;
        this.aprendizRepository = aprendizRepository;
        this.instructorRepository = instructorRepository;
        this.asignacionRepository = asignacionRepository;
        this.matriculaRepository = matriculaRepository;
        this.sesionRepository = sesionRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.actividadRepository = actividadRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.entregaRepository = entregaRepository;
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

    // ---------------------------------------------------------------- administrador

    private Dashboard administrador() {
        long cuentas = usuarioRepository.count();
        List<Metrica> metricas = List.of(
                new Metrica(String.valueOf(usuarioRepository.countByEstado(EstadoGeneral.ACTIVO)), "Usuarios activos",
                        "de " + cuentas + " cuentas", ""),
                new Metrica(String.valueOf(fichaRepository.countByEstado(EstadoFicha.EN_EJECUCION)), "Fichas en ejecucion",
                        "de " + fichaRepository.count() + " fichas", ""),
                new Metrica(String.valueOf(aprendizRepository.countByEstadoFormativo(EstadoFormativo.EN_FORMACION)),
                        "Aprendices en formacion", "de " + aprendizRepository.count() + " registrados", ""),
                new Metrica(String.valueOf(instructorRepository.countByEstado(EstadoGeneral.ACTIVO)), "Instructores activos",
                        "", ""),
                new Metrica(String.valueOf(matriculaRepository.countByEstado(EstadoMatricula.ACTIVA)), "Matriculas activas",
                        "", ""),
                new Metrica(String.valueOf(programaRepository.countByEstado(EstadoGeneral.ACTIVO)), "Programas activos",
                        "", ""));

        List<Medidor> medidores = new ArrayList<>();
        for (RolNombre rol : RolNombre.values()) {
            long total = usuarioRepository.countByRol(rol);
            medidores.add(new Medidor(nombreRol(rol), porcentaje(total, cuentas), total + " cuenta(s)", ""));
        }

        return new Dashboard("Resumen general", "Estado actual del sistema academico.", null, metricas, medidores,
                List.of(listaFichasSinInstructor(), listaCuentasSinPerfil(), listaActividadesPorVencer(null)));
    }

    // ---------------------------------------------------------------- coordinacion

    private Dashboard coordinacion() {
        List<Ficha> fichas = fichaRepository.findAll();
        long sinInstructor = fichasSinInstructor().size();
        List<Metrica> metricas = List.of(
                new Metrica(String.valueOf(programaRepository.countByEstado(EstadoGeneral.ACTIVO)), "Programas activos", "", ""),
                new Metrica(String.valueOf(fichaRepository.countByEstado(EstadoFicha.EN_EJECUCION)), "Fichas en ejecucion",
                        "de " + fichas.size() + " fichas", ""),
                new Metrica(String.valueOf(aprendizRepository.countByEstadoFormativo(EstadoFormativo.EN_FORMACION)),
                        "Aprendices en formacion", "", ""),
                new Metrica(String.valueOf(matriculaRepository.countByEstado(EstadoMatricula.ACTIVA)), "Matriculas activas", "", ""),
                new Metrica(String.valueOf(instructorRepository.countByEstado(EstadoGeneral.ACTIVO)), "Instructores activos", "", ""),
                new Metrica(String.valueOf(sinInstructor), "Fichas sin instructor",
                        sinInstructor == 0 ? "Todas cubiertas" : "En ejecucion y sin asignar", sinInstructor == 0 ? "ok" : "alerta"));

        List<Medidor> medidores = new ArrayList<>();
        for (EstadoFicha estado : EstadoFicha.values()) {
            long total = fichaRepository.countByEstado(estado);
            medidores.add(new Medidor("Fichas " + legible(estado.name()), porcentaje(total, fichas.size()), total + " ficha(s)", ""));
        }
        long registrados = aprendizRepository.count();
        for (EstadoFormativo estado : EstadoFormativo.values()) {
            long total = aprendizRepository.countByEstadoFormativo(estado);
            medidores.add(new Medidor("Aprendices " + legible(estado.name()), porcentaje(total, registrados),
                    total + " aprendiz(ces)", ""));
        }

        LocalDate hoy = Fechas.hoy();
        List<Item> terminando = fichaRepository.findByEstado(EstadoFicha.EN_EJECUCION).stream()
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

    // ---------------------------------------------------------------- instructor

    private Dashboard instructor(Usuario usuario) {
        Optional<Instructor> perfil = instructorRepository.findByUsuarioId(usuario.getId());
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
        List<Matricula> matriculas = matriculaRepository.findByFichaIdInAndEstado(fichaIds, EstadoMatricula.ACTIVA);
        Map<Long, Long> matriculadosPorFicha = matriculas.stream()
                .collect(Collectors.groupingBy(m -> m.getFicha().getId(), Collectors.counting()));
        List<ActividadEvaluativa> actividades = actividadRepository.findByFichaIdIn(fichaIds);
        List<Entrega> entregas = entregaRepository.findByActividadFichaIdIn(fichaIds);
        List<Evaluacion> evaluaciones = evaluacionRepository.findByActividadFichaIdIn(fichaIds);

        Set<String> calificadas = evaluaciones.stream().filter(e -> e.getCalificacion() != null)
                .map(e -> clave(e.getActividad().getId(), e.getMatricula().getId())).collect(Collectors.toSet());
        List<Entrega> porCalificar = entregas.stream()
                .filter(e -> !calificadas.contains(clave(e.getActividad().getId(), e.getMatricula().getId())))
                .sorted(Comparator.comparing(Entrega::getFechaEntrega))
                .toList();

        List<ActividadEvaluativa> vigentes = actividades.stream().filter(a -> !a.getFechaLimite().isBefore(hoy)).toList();
        List<SesionFormacion> proximas = sesionRepository
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
                asistenciaRepository.findBySesionFichaIdIn(fichaIds)));

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

    // ---------------------------------------------------------------- aprendiz

    private Dashboard aprendiz(Usuario usuario) {
        Optional<Aprendiz> perfil = aprendizRepository.findByUsuarioId(usuario.getId());
        if (perfil.isEmpty()) {
            return sinContenido("Tu resumen", "Tu cuenta todavia no tiene un perfil de aprendiz asociado. "
                    + "Contacta al administrador o a coordinacion academica para que lo cree.");
        }
        List<Matricula> activas = matriculaRepository.findByAprendizId(perfil.get().getId()).stream()
                .filter(m -> m.getEstado() == EstadoMatricula.ACTIVA).toList();
        if (activas.isEmpty()) {
            return sinContenido("Tu resumen", "No tienes una matricula activa en este momento. "
                    + "Cuando coordinacion te matricule en una ficha veras aqui tu avance.");
        }

        LocalDate hoy = Fechas.hoy();
        List<Long> matriculaIds = activas.stream().map(Matricula::getId).toList();
        Set<Long> fichaIds = activas.stream().map(m -> m.getFicha().getId()).collect(Collectors.toSet());
        Map<String, Entrega> entregas = entregaRepository.findByMatriculaIdIn(matriculaIds).stream()
                .collect(Collectors.toMap(e -> clave(e.getActividad().getId(), e.getMatricula().getId()), Function.identity()));
        List<Evaluacion> evaluacionesLista = evaluacionRepository.findByMatriculaIdIn(matriculaIds);
        Map<String, Evaluacion> evaluaciones = evaluacionesLista.stream()
                .collect(Collectors.toMap(e -> clave(e.getActividad().getId(), e.getMatricula().getId()), Function.identity()));

        record Fila(ActividadEvaluativa actividad, EstadoActividad estado) {
        }
        List<Fila> filas = new ArrayList<>();
        for (Matricula matricula : activas) {
            for (ActividadEvaluativa actividad : actividadRepository.findByFichaId(matricula.getFicha().getId())) {
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

        List<Asistencia> asistencias = asistenciaRepository.findByMatriculaIdIn(matriculaIds);
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

        List<Item> sesiones = sesionRepository
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

    // ---------------------------------------------------------------- auditor

    private Dashboard auditor() {
        List<Asistencia> asistencias = asistenciaRepository.findAll();
        List<Evaluacion> evaluaciones = evaluacionRepository.findAll();
        Medidor global = medidorAsistencia("Asistencia global", asistencias);
        long aprobadas = evaluaciones.stream().filter(e -> e.getJuicio() == JuicioEvaluacion.APROBADO).count();

        List<Metrica> metricas = List.of(
                new Metrica(String.valueOf(programaRepository.count()), "Programas", "", ""),
                new Metrica(String.valueOf(fichaRepository.count()), "Fichas", fichaRepository.countByEstado(EstadoFicha.EN_EJECUCION) + " en ejecucion", ""),
                new Metrica(String.valueOf(aprendizRepository.count()), "Aprendices", "", ""),
                new Metrica(String.valueOf(instructorRepository.count()), "Instructores", "", ""),
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

    // ---------------------------------------------------------------- listas compartidas

    private List<Ficha> fichasSinInstructor() {
        return fichaRepository.findByEstado(EstadoFicha.EN_EJECUCION).stream()
                .filter(f -> asignacionRepository.findByFichaId(f.getId()).isEmpty())
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
        for (Usuario u : usuarioRepository.findAll()) {
            if (u.getEstado() != EstadoGeneral.ACTIVO) {
                continue;
            }
            if (u.getRol() == RolNombre.APRENDIZ && !aprendizRepository.existsByUsuarioId(u.getId())) {
                items.add(new Item(u.getNombreCompleto(), u.getCorreo(), "Sin perfil de aprendiz", "estado-pendiente",
                        "/aprendices/nuevo"));
            } else if (u.getRol() == RolNombre.INSTRUCTOR && !instructorRepository.existsByUsuarioId(u.getId())) {
                items.add(new Item(u.getNombreCompleto(), u.getCorreo(), "Sin perfil de instructor", "estado-pendiente",
                        "/instructores/nuevo"));
            }
        }
        return new Lista("Cuentas sin perfil", "Todas las cuentas de aprendiz e instructor tienen perfil.",
                items.stream().limit(MAX_ITEMS).toList());
    }

    private Lista listaActividadesPorVencer(Collection<Long> fichaIds) {
        LocalDate hoy = Fechas.hoy();
        List<Item> items = actividadRepository.findByFechaLimiteBetweenOrderByFechaLimiteAsc(hoy, hoy.plusDays(7)).stream()
                .filter(a -> fichaIds == null || fichaIds.contains(a.getFicha().getId()))
                .limit(MAX_ITEMS)
                .map(a -> new Item(a.getNombre(), "Ficha " + a.getFicha().getNumero(), "Vence " + DIA.format(a.getFechaLimite()),
                        "estado-pendiente", "/actividades"))
                .toList();
        return new Lista("Actividades por vencer (7 dias)", "No hay actividades que venzan esta semana.", items);
    }

    // ---------------------------------------------------------------- utilidades

    private Dashboard sinContenido(String titulo, String aviso) {
        return new Dashboard(titulo, "", aviso, List.of(), List.of(), List.of());
    }

    private String clave(Long actividadId, Long matriculaId) {
        return actividadId + ":" + matriculaId;
    }

    private int porcentaje(long parte, long total) {
        return total == 0 ? 0 : (int) Math.round(100.0 * parte / total);
    }

    /** (presente + tardanza) / (registros - excusados); null si no hay registros que cuenten. */
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
