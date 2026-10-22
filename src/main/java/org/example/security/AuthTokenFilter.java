package org.example.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import io.jsonwebtoken.JwtException;
import java.util.Locale;

public class AuthTokenFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserRepository users;

    public AuthTokenFilter(JwtUtils jwtUtils, UserRepository users) {
        this.jwtUtils = jwtUtils;
        this.users = users;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = header.substring(7);
            String email = null;
            try { email = jwtUtils.getUserNameFromJwtToken(token); }
            catch (JwtException | IllegalArgumentException ignored) { /* Remain unauthenticated. */ }
            if (email != null && !email.isBlank()) {
                users.findByEmail(email)
                        .filter(user -> user.isActive())
                        .ifPresent(user -> {
                            var principal = org.springframework.security.core.userdetails.User
                                    .withUsername(user.getEmail()).password("")
                                    .roles(user.getRole().toUpperCase(Locale.ROOT)).build();
                            var authentication = new UsernamePasswordAuthenticationToken(
                                    principal, null, principal.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        });
            }
        }
        filterChain.doFilter(request, response);
    }
}
