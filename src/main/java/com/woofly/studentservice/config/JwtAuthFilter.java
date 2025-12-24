package com.woofly.studentservice.config;

import com.woofly.studentservice.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("JwtAuthFilter: Processing request for {}", request.getRequestURI());


        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("JwtAuthFilter: No JWT token found in request headers or header does not start with Bearer. URI: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        log.debug("JwtAuthFilter: Extracted JWT token: {}", token);


        try {
            String username = jwtService.extractUsername(token);
            log.info("JwtAuthFilter: Extracted username from token: {}", username);


            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.info("JwtAuthFilter: Security context is null, attempting to set authentication.");

                if (jwtService.validateToken(token)) {
                    log.info("JwtAuthFilter: JWT token is valid.");
                    setAuthentication(request, token, username);
                    log.info("JwtAuthFilter: Authentication set for user: {}", username);
                } else {
                    log.warn("JwtAuthFilter: JWT token is not valid for user: {}", username);
                }
            } else {
                log.warn("JwtAuthFilter: Username is null or authentication is already set. Username: {}, Auth: {}", username, SecurityContextHolder.getContext().getAuthentication());
            }

        } catch (Exception ex) {
            log.error("JwtAuthFilter: JWT authentication failed", ex);
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(
            HttpServletRequest request,
            String token,
            String username
    ) {
        Claims claims = jwtService.extractAllClaims(token);
        String role = claims.get("role", String.class);
        log.info("JwtAuthFilter: Extracted role from token: {}", role);


        List<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority(role));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("JwtAuthFilter: Successfully set authentication in security context for user '{}' with role '{}'", username, role);
    }
}
