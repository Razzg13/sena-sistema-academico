package com.sena.cipa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
                        .requestMatchers("/login", "/css/**", "/img/**", "/webjars/**", "/favicon.ico").permitAll()
                        .requestMatchers("/usuarios/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/programas/**", "/fichas/**", "/instructores/**",
                                "/aprendices/**", "/matriculas/**")
                        .hasAnyRole("ADMINISTRADOR", "COORDINACION_ACADEMICA")
                        .requestMatchers("/sesiones/**", "/asistencias/**",
                                "/actividades/**", "/evaluaciones/**")
                        .hasAnyRole("ADMINISTRADOR", "COORDINACION_ACADEMICA", "INSTRUCTOR")
                        .requestMatchers("/mis-datos/**").hasRole("APRENDIZ")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/inicio", true)
                        .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll());

        return http.build();
    }
}
