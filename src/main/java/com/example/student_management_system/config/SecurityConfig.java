package com.example.student_management_system.config;

import com.example.student_management_system.security.CustomUserDetailsService;
import com.example.student_management_system.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomUserDetailsService userDetailsService) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    // =========================
    // PASSWORD ENCODER
    // =========================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    // =========================
    // AUTHENTICATION PROVIDER
    // =========================

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    // =========================
    // AUTHENTICATION MANAGER
    // =========================

    @Bean
    public AuthenticationManager authenticationManager() {

        return new ProviderManager(
                authenticationProvider()
        );
    }

    // =========================
    // SECURITY FILTER CHAIN
    // =========================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                // =========================
                // CORS
                // =========================

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                // =========================
                // CSRF
                // =========================

                .csrf(csrf ->
                        csrf.disable()
                )

                // =========================
                // SESSION MANAGEMENT
                // =========================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // =========================
                // AUTHENTICATION PROVIDER
                // =========================

                .authenticationProvider(
                        authenticationProvider()
                )

                // =========================
                // AUTHORIZATION
                // =========================

                .authorizeHttpRequests(auth -> auth

                        // Public authentication endpoints
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/refresh"
                        ).permitAll()

                        // =========================
                        // STUDENT VIEW
                        // ADMIN + TEACHER + STUDENT
                        // =========================

                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/students",
                                "/api/students/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "TEACHER",
                                "STUDENT"
                        )

                        // =========================
                        // CREATE STUDENT
                        // ADMIN + TEACHER
                        // =========================

                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/students"
                        ).hasAnyRole(
                                "ADMIN",
                                "TEACHER"
                        )

                        // =========================
                        // UPDATE STUDENT
                        // ADMIN + TEACHER
                        // =========================

                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/api/students/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "TEACHER"
                        )

                        // =========================
                        // DELETE STUDENT
                        // ADMIN ONLY
                        // =========================

                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/api/students/**"
                        ).hasRole("ADMIN")

                        // =========================
                        // USER MANAGEMENT
                        // ADMIN ONLY
                        // =========================

                        .requestMatchers(
                                "/api/users/**"
                        ).hasRole("ADMIN")

                        // =========================
                        // LOGOUT
                        // AUTHENTICATED USERS
                        // =========================

                        .requestMatchers(
                                "/api/auth/logout"
                        ).authenticated()

                        // =========================
                        // EVERYTHING ELSE
                        // =========================

                        .anyRequest()
                        .authenticated()
                )

                // =========================
                // 401 AND 403 HANDLING
                // =========================

                .exceptionHandling(exception -> exception

                        // 401 - NOT AUTHENTICATED
                        .authenticationEntryPoint(
                                (request, response, authException) -> {

                                    response.setStatus(
                                            HttpServletResponse.SC_UNAUTHORIZED
                                    );

                                    response.setContentType(
                                            "application/json"
                                    );

                                    response.getWriter().write("""
                                            {
                                                "status": 401,
                                                "error": "Unauthorized",
                                                "message": "Authentication is required"
                                            }
                                            """);
                                }
                        )

                        // 403 - AUTHENTICATED BUT NOT AUTHORIZED
                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> {

                                    response.setStatus(
                                            HttpServletResponse.SC_FORBIDDEN
                                    );

                                    response.setContentType(
                                            "application/json"
                                    );

                                    response.getWriter().write("""
                                            {
                                                "status": 403,
                                                "error": "Forbidden",
                                                "message": "You do not have permission to access this resource"
                                            }
                                            """);
                                }
                        )
                )

                // =========================
                // JWT FILTER
                // =========================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // =========================
    // CORS CONFIGURATION
    // =========================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:3000",
                        "http://localhost:5173"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}
