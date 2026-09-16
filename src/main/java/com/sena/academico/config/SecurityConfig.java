package com.sena.academico.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**", "/webjars/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
                        // Regla especifica ANTES de /coordinacion/** (RF-13): la autorizacion fina
                        // por evidencia (dueno, instructor asignado, staff) la valida EvidenciaService;
                        // aqui solo se exige estar autenticado con alguno de los roles relevantes.
                        .requestMatchers("/coordinacion/evidencias/*/descargar")
                        .hasAnyRole("ADMINISTRADOR", "COORDINACION_ACADEMICA", "CONSULTA_AUDITOR", "INSTRUCTOR", "APRENDIZ")

                        // Autogestion de instructor (actor "Instructor": gestiona sesiones, asistencia,
                        // actividades, calificaciones, retroalimentacion y evidencias de fichas ASIGNADAS).
                        // FichaController filtra/valida que solo vea sus propias fichas; estas reglas de
                        // ruta solo cubren "esta autenticado con el rol correcto", van ANTES de /coordinacion/**
                        // porque si no, la regla general (solo ADMINISTRADOR/COORDINACION_ACADEMICA) gana.
                        .requestMatchers(HttpMethod.GET, "/coordinacion/fichas", "/coordinacion/fichas/*")
                        .hasAnyRole("ADMINISTRADOR", "COORDINACION_ACADEMICA", "INSTRUCTOR")
                        .requestMatchers("/coordinacion/fichas/*/sesiones", "/coordinacion/fichas/*/actividades")
                        .hasAnyRole("ADMINISTRADOR", "COORDINACION_ACADEMICA", "INSTRUCTOR")
                        .requestMatchers("/coordinacion/sesiones/**", "/coordinacion/actividades/**")
                        .hasAnyRole("ADMINISTRADOR", "COORDINACION_ACADEMICA", "INSTRUCTOR")
                        .requestMatchers("/coordinacion/evidencias/*/estado")
                        .hasAnyRole("ADMINISTRADOR", "COORDINACION_ACADEMICA", "INSTRUCTOR")

                        .requestMatchers("/coordinacion/**").hasAnyRole("ADMINISTRADOR", "COORDINACION_ACADEMICA")
                        .requestMatchers("/instructor/**").hasAnyRole("ADMINISTRADOR", "INSTRUCTOR")
                        .requestMatchers("/aprendiz/**").hasAnyRole("ADMINISTRADOR", "APRENDIZ")
                        .requestMatchers("/reportes/**").hasAnyRole("ADMINISTRADOR", "COORDINACION_ACADEMICA", "CONSULTA_AUDITOR")
                        .requestMatchers("/auditoria/**").hasAnyRole("ADMINISTRADOR", "CONSULTA_AUDITOR")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/inicio", true)
                        .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }
}
