package com.ecommerce.orderservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("➡️ REQUEST PATH: " + request.getServletPath());

        if (request.getServletPath().startsWith("/auth")) {
            System.out.println("⏭️ Skipping JWT filter for /auth");
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        System.out.println(" JWT FILTER HIT");
        System.out.println("Header: " + header);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                String email = JwtUtil.extractEmail(token);
                String role = JwtUtil.extractRole(token);

                System.out.println(" Email: " + email);
                System.out.println(" Role: " + role);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                System.out.println(" JWT ERROR: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}