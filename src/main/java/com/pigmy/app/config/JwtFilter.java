package com.pigmy.app.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.Collections;


@Component
public class JwtFilter extends OncePerRequestFilter {

  //  private static final Key secureSigningKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private static final String SECRET_KEY = "13f760869577f85a5dceb39e69d83e1998898cbe662f27bc9689a5b61e8a061b2a8329af";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");



        String path = request.getRequestURI();
        if (path.startsWith("/pigmy/v1/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader != null) {
            String token = authHeader;

            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(SECRET_KEY)
                        .parseClaimsJws(token)
                        .getBody();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(claims.getSubject(), null, Collections.emptyList());

                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("Path: " + request.getRequestURI());
                System.out.println("Auth header: " + request.getHeader("Authorization"));
                System.out.println("Authentication: " + SecurityContextHolder.getContext().getAuthentication());

            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                return;
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
