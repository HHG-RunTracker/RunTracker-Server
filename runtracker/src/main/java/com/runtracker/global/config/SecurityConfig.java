package com.runtracker.global.config;

import com.runtracker.global.jwt.JwtAuthenticationFilter;
import com.runtracker.global.jwt.JwtUtil;
import com.runtracker.domain.auth.eventHandler.OAuth2EventHandler;
import com.runtracker.domain.auth.service.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.core.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final OAuth2EventHandler oAuth2SuccessHandler;

    @Bean
    @Order(1)
    public SecurityFilterChain staticResourceFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/static/**")
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth2 -> oauth2.disable());
        
        return http.build();
    }
    
    @Bean
    @Order(2)
    public SecurityFilterChain mainFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/login/oauth2/**", "/oauth2/**").permitAll()
                        .requestMatchers("/actuator/**", "/health").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .successHandler(oAuth2SuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService())
                        )
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(customAuthenticationEntryPoint())
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        List<String> excludePaths = Arrays.asList(
                "/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**",
                "/static/**", "/api/**", "/login/oauth2/**", "/oauth2/**",
                "/actuator/**", "/health", "/error", "/favicon.ico"
        );
        return new JwtAuthenticationFilter(jwtUtil, excludePaths);
    }

    @Bean
    public OAuth2UserService customOAuth2UserService() {
        return new OAuth2UserService();
    }
    
    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                "{\"status\":{\"statusCode\":\"C401\",\"message\":\"Unauthorized\",\"description\":\"" + authException.getMessage() + "\"}}"
            );
        };
    }
}