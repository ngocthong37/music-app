package com.quocluan.kdmaylanh.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.quocluan.kdmaylanh.Enum.Role.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/api/v1/auth/**",
                        "api/v1/productArticle/**",
                        "api/v1/product/**",
                        "api/v1/brand/**",
                        "/v3/api-docs/**",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/swagger-ui.html"
                )
                .permitAll()
                .requestMatchers("/api/v1/management/**").hasRole(ADMIN.name())
                .requestMatchers("/api/v1/employee/**").hasAnyRole(EMPLOYEE.name(), ADMIN.name())
                .requestMatchers("/api/v1/cart/**").hasAnyRole(CUSTOMER.name())
                .requestMatchers("/api/v1/customer/findById/**").hasAnyRole(CUSTOMER.name(), ADMIN.name(), EMPLOYEE.name())
                .requestMatchers("/api/v1/customer/**").hasAnyRole(CUSTOMER.name())
                .requestMatchers("/api/v1/invoice/**").hasAnyRole(CUSTOMER.name())
                .requestMatchers("/api/v1/order/**").hasAnyRole(CUSTOMER.name())
                .requestMatchers("/api/v1/reviews/**").hasAnyRole(CUSTOMER.name())
                .requestMatchers("/api/v1/account/**").hasAnyRole(CUSTOMER.name(), ADMIN.name(), EMPLOYEE.name())
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout();
        return http.build();
    }
}
