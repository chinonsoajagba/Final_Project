package com.chinonso.university_scheduling.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {
                }) // enable CORS with our CorsConfig bean
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ── PUBLIC ─────────────────────────────────────────
                        .requestMatchers("/api/auth/**", "/api/students/active", "/api/teachers/active").permitAll()
                        .requestMatchers(
                                "/",
                                "/*.html",
                                "/*.css",
                                "/*.js",
                                "/favicon.ico")
                        .permitAll()

                        // ── ROOMS ──────────────────────────────────────────
                        .requestMatchers("/api/rooms/**")
                        .hasAnyRole("ADMIN", "CLASS_HANDLER", "ENROLLMENT_OFFICER")

                        // ── TEACHERS ───────────────────────────────────────
                        .requestMatchers("/api/teachers/**")
                        .hasAnyRole("ADMIN", "CLASS_HANDLER", "ENROLLMENT_OFFICER")

                        // ── STUDENTS ───────────────────────────────────────
                        .requestMatchers("/api/students/**")
                        .hasAnyRole("ADMIN", "ENROLLMENT_OFFICER")

                        // ── COURSES ────────────────────────────────────────
                        .requestMatchers("/api/courses/**")
                        .hasAnyRole("ADMIN", "CLASS_HANDLER", "ENROLLMENT_OFFICER")

                        // ── CLASSES ────────────────────────────────────────
                        .requestMatchers("/api/classes/**")
                        .hasAnyRole("ADMIN", "CLASS_HANDLER", "ENROLLMENT_OFFICER")

                        // ── SCHEDULES ──────────────────────────────────────
                        .requestMatchers("/api/schedules/**")
                        .hasAnyRole("ADMIN", "CLASS_HANDLER")

                        // ── ENROLMENTS ─────────────────────────────────────
                        .requestMatchers("/api/enrolments/**")
                        .hasAnyRole("ADMIN", "ENROLLMENT_OFFICER")

                        // ── CLASS TEACHERS ─────────────────────────────────
                        .requestMatchers("/api/class-teachers/**")
                        .hasAnyRole("ADMIN", "CLASS_HANDLER")

                        // ── USER MANAGEMENT ────────────────────────────────
                        .requestMatchers("/api/users/*/reset-password")
                        .authenticated()
                        .requestMatchers("/api/users/**")
                        .hasRole("ADMIN")

                        // ── STUDENT PORTAL ─────────────────────────────────
                        .requestMatchers("/api/student-portal/**")
                        .hasRole("STUDENT")

                        // ── TEACHER PORTAL ─────────────────────────────────
                        .requestMatchers("/api/teacher-portal/**")
                        .hasRole("TEACHER")

                        // everything else requires authentication
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}