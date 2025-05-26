package com.vanvan.musicapp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import static com.vanvan.musicapp.Enum.Role.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(
                                "/api/v1/genres/**",
                                "/api/v1/auth/**",
                                "/api/v1/musics/**",
                                "/api/v1/songs/**",
                                "/api/v1/recommendations/**",
                                "/api/v1/albums/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // ADMIN-only
                        .requestMatchers("/api/v1/admin/**").hasRole(ADMIN.name())
                        // CUSTOMER-only
                        .requestMatchers(
                                "/api/v1/favorite/add",
                                "/api/v1/favorite/remove",
                                "/api/v1/favorite/get-by-user-id/**",
                                "/api/v1/playlists/**",
                                "/api/v1/listening-counts/user/**"
                        ).hasRole(CUSTOMER.name())
                        // Shared (CUSTOMER + ADMIN)
                        .requestMatchers(
                                "/api/v1/auth/update-password",
                                "/api/v1/auth/log-out",
                                "/api/v1/listening-counts/**",
                                "/api/v1/users/**"
                        ).hasAnyRole(CUSTOMER.name(), ADMIN.name())
                        // All others require authentication
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
