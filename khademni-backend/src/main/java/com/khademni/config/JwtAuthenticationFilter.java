package com.khademni.config;

import com.khademni.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        logger.info("=== JWT Filter Processing: {} {}", method, requestURI);

        try {
            String authHeader = request.getHeader("Authorization");
            logger.info("Authorization header: {}", authHeader != null ? "Present (Bearer...)" : "Missing");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                logger.info("Token extracted, length: {}", token.length());

                try {
                    if (jwtUtil.validateToken(token)) {
                        logger.info("✅ Token is valid");

                        String username = jwtUtil.getUsernameFromToken(token);
                        logger.info("Username from token: {}", username);

                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        logger.info("UserDetails loaded for: {}", userDetails.getUsername());
                        logger.info("Authorities: {}", userDetails.getAuthorities());

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        logger.info("✅ Authentication set in SecurityContext");
                    } else {
                        logger.warn("❌ Token validation failed");
                    }
                } catch (Exception e) {
                    logger.error("❌ Error processing JWT token: ", e);
                }
            } else {
                logger.info("No Bearer token found in Authorization header");
            }
        } catch (Exception e) {
            logger.error("❌ Unexpected error in JWT filter: ", e);
        }

        logger.info("=== Continuing filter chain");
        filterChain.doFilter(request, response);
    }
}