package com.web.back.config;

import com.web.back.filters.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationManager authenticationManager;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationManager authenticationManager) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationManager = authenticationManager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors((cors) -> cors
                        .configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authRequest ->
                        authRequest
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**", "/swagger-resources").permitAll()
                                .requestMatchers("/webjars/**").permitAll()
                                .requestMatchers("/auth/changePassword").permitAll()
                                .requestMatchers("/auth/login").permitAll()
                                .requestMatchers("/impersonate/**").permitAll()
                                .requestMatchers("/api/su/updateRole").permitAll()
                                .requestMatchers("/api/su/**").permitAll()
                                .requestMatchers("/registros/**").permitAll()
                                .requestMatchers("/empleado/**").permitAll()
                                .requestMatchers("/log/**").permitAll()
                                .requestMatchers("/perfil/**").permitAll()
                                .requestMatchers("/evaluation/**").permitAll()
                                .requestMatchers("/filters/**").permitAll()
                                .requestMatchers("/permission/**").permitAll()
                                .requestMatchers("/perfil/**").permitAll()
                                .requestMatchers("/timesheet/**").permitAll()
                                .anyRequest().permitAll()
                )
                .sessionManagement(sessionManager ->
                        sessionManager
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationManager(authenticationManager)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.applyPermitDefaultValues();
        configuration.addAllowedOrigin("*");
        Arrays.stream(HttpMethod.values()).forEach(configuration::addAllowedMethod);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
