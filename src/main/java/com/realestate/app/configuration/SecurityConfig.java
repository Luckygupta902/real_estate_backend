package com.realestate.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        public SecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter) {

                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        // BCrypt password encoder
        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        // CORS: allow the frontend to call the API from any origin (development setting;
        // restrict to your real frontend domain before deploying)
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOriginPatterns(List.of("*"));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return source;
        }

        // Spring Security configuration
        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http
                                // Enable CORS using the bean above
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // Disable CSRF for REST API
                                .csrf(csrf -> csrf.disable())

                                .authorizeHttpRequests(auth -> auth
                                                // Public APIs - no authentication required
                                                .requestMatchers("/api/auth/user").permitAll()
                                                .requestMatchers("/api/auth/login").permitAll()
                                                .requestMatchers("/api/auth/activate").permitAll()

                                                // Spring's error page: without this, every backend error shows up as 403
                                                .requestMatchers("/error").permitAll()

                                                // CORS preflight requests
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                                // ADMIN USER APIs - ALL METHODS AND PATHS
                                               /*  .requestMatchers("/api/admin/users", "/api/admin/users/**")
                                                .hasRole("ADMIN")*/

                                                // OTHER USER APIs
                                                .requestMatchers(HttpMethod.POST, "/api/admin/users")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.GET, "/api/admin/users/all")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/admin/users**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/admin/users/")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.GET, "/api/admin/users{id}")
                                                .hasRole("ADMIN")

                                                // =========================
                                                // PROPERTY
                                                // ==============

                                                // CREATE PROPERTY: ADMIN and AGENT
                                                .requestMatchers(HttpMethod.POST, "/api/properties/create")
                                                .hasAnyRole("ADMIN", "AGENT", "BUILDER", "BROKER")

                                                // READ PROPERTY: ADMIN, AGENT and USER
                                                .requestMatchers(HttpMethod.GET, "/api/properties/my")
                                                .hasAnyRole("ADMIN", "AGENT", "USER", "BUILDER", "BROKER")

                                                // READ PROPERTY: ADMIN, AGENT and USER
                                                .requestMatchers(HttpMethod.GET, "/api/properties/**")
                                                .hasAnyRole("ADMIN", "AGENT", "USER", "BUILDER", "BROKER")

                                                // UPDATE PROPERTY: ADMIN and AGENT
                                                .requestMatchers(HttpMethod.PUT, "/api/properties/**")
                                                .hasAnyRole("ADMIN", "AGENT", "BUILDER", "BROKER")

                                                // DELETE PROPERTY: ADMIN and AGENT
                                                .requestMatchers(HttpMethod.DELETE, "/api/properties/**")
                                                .hasAnyRole("ADMIN", "AGENT", "BUILDER", "BROKER")

                                                // ADD MEDIA: ADMIN and AGENT
                                                .requestMatchers(HttpMethod.POST, "/api/properties/*/media")
                                                .hasAnyRole("ADMIN", "AGENT", "BUILDER", "BROKER")

                                                // ADD MEDIA: ADMIN and AGENT
                                                .requestMatchers(HttpMethod.GET, "/api/properties/*/media")
                                                .hasAnyRole("ADMIN", "AGENT", "USER", "BUILDER", "BROKER")

                                                // All remaining property endpoints
                                                .requestMatchers("/api/properties/**")
                                                .authenticated()

                                                // 1. ADMIN: Get all conversations
                                                .requestMatchers(HttpMethod.GET, "/api/conversations/admin/all")
                                                .hasRole("ADMIN")

                                                // 2. ADMIN: Delete a conversation
                                                .requestMatchers(HttpMethod.DELETE, "/api/conversations/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/conversations/*/messages")
                                                .hasAnyRole("ADMIN", "AGENT", "USER", "BUILDER", "BROKER")

                                                // CONVERSATIONS: ADMIN, AGENT AND USER
                                                .requestMatchers("/api/conversations/**")
                                                .hasAnyRole("ADMIN", "AGENT", "USER")


                                                

                                                // All other APIs require JWT
                                                .anyRequest()
                                                .authenticated())

                                // Add JWT filter
                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}