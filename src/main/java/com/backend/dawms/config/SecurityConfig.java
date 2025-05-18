package com.backend.dawms.config;

import com.backend.dawms.security.JwtAuthenticationFilter;
import com.backend.dawms.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Auth endpoints - no authentication required
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/auth/signup").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/verify-otp").permitAll()
                .requestMatchers("/api/auth/refresh-token").permitAll()
                .requestMatchers("/api/test/**").permitAll()
                // Swagger/OpenAPI endpoints
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Warranty endpoints
                .requestMatchers(HttpMethod.GET, "/api/warranties/**").hasAnyRole("EMPLOYEE", "ADMIN", "TECHNICIAN")
                .requestMatchers(HttpMethod.POST, "/api/warranties/**").hasAnyRole("ADMIN", "TECHNICIAN")
                .requestMatchers(HttpMethod.PUT, "/api/warranties/**").hasAnyRole("ADMIN", "TECHNICIAN")
                .requestMatchers(HttpMethod.DELETE, "/api/warranties/**").hasRole("ADMIN")
                // Asset endpoints
                .requestMatchers(HttpMethod.GET, "/api/assets/**").hasAnyRole("EMPLOYEE", "ADMIN", "TECHNICIAN")
                .requestMatchers(HttpMethod.POST, "/api/assets/**").hasAnyRole("ADMIN", "TECHNICIAN")
                .requestMatchers(HttpMethod.PUT, "/api/assets/**").hasAnyRole("ADMIN", "TECHNICIAN")
                .requestMatchers(HttpMethod.DELETE, "/api/assets/**").hasRole("ADMIN")
                // Maintenance endpoints
                .requestMatchers(HttpMethod.GET, "/api/maintenance/**").hasAnyRole("EMPLOYEE", "ADMIN", "TECHNICIAN")
                .requestMatchers(HttpMethod.POST, "/api/maintenance/**").hasAnyRole("EMPLOYEE", "ADMIN", "TECHNICIAN")
                .requestMatchers(HttpMethod.PUT, "/api/maintenance/**").hasAnyRole("ADMIN", "TECHNICIAN")
                .requestMatchers(HttpMethod.DELETE, "/api/maintenance/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            );
        
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173")); // Frontend URL
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}