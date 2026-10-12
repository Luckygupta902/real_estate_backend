
package com.realestate.app.configuration;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // No token: let Spring Security handle authorization.
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract token
            String token = authHeader.substring(7);

            // Validate token and extract claims
            Claims claims = jwtService.extractClaims(token);

            // Get user ID from JWT subject
            String userId = claims.getSubject();

            if (userId == null || userId.isBlank()) {
                throw new IllegalArgumentException(
                        "Missing user ID in JWT");
            }

            // Get role ID from JWT
            Object roleClaim = claims.get("roleId");

            if (roleClaim == null) {
                throw new IllegalArgumentException(
                        "Missing roleId in JWT");
            }

            Long roleId = Long.valueOf(roleClaim.toString());

            // Map role ID to role name
            String role = getRoleName(roleId);

            // Create authentication with the correct authority
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of(
                                    new SimpleGrantedAuthority(
                                            "ROLE_" + role)
                            )
                    );

            // Store authentication in security context
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

        } catch (Exception e) {

            // Do not retain authentication after token failure
            SecurityContextHolder.clearContext();

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired JWT");

            return;
        }

        filterChain.doFilter(request, response);
    }

    // ROLE ID MAPPING
    private String getRoleName(Long roleId) {

        if (roleId == 1L) {
            return "USER";
        }

        if (roleId == 2L) {
            return "ADMIN";
        }

        if (roleId == 3L) {
            return "AGENT";
        }
        if (roleId == 4L) {
            return "BUILDER";
        }
        if (roleId == 5L) {
            return "BROKER";
        }
         
        throw new IllegalArgumentException(
                "Invalid role ID: " + roleId);
    }
}