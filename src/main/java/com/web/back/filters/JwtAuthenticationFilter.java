package com.web.back.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.back.model.entities.User;
import com.web.back.model.responses.CustomResponse;
import com.web.back.services.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String token = getTokenFromRequest(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            try {
                final String username = jwtService.getUsernameFromToken(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    User userDetails = (User) userDetailsService.loadUserByUsername(username);

                    if (jwtService.isTokenValid(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, null);

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }catch (Exception e){
                processAuthException(response);
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            processExpiredJwtException(response);
        } catch (Exception e) {
            processException(response, e);
        }
    }

    private void processExpiredJwtException(HttpServletResponse response) throws IOException {
        CustomResponse<String> customResponse = new CustomResponse<>();
        customResponse.setError(true);
        customResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        customResponse.setMessage("Token expirado");

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), customResponse);
    }

    private void processAuthException(HttpServletResponse response) throws IOException {
        CustomResponse<String> customResponse = new CustomResponse<>();
        customResponse.setError(true);
        customResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        customResponse.setMessage("Error desconocido de autenticación");

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), customResponse);
    }

    private void processException(HttpServletResponse response, Exception exception) throws IOException {
        CustomResponse<String> customResponse = new CustomResponse<>();
        customResponse.setError(true);
        customResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        customResponse.setMessage(exception.getMessage());

        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), customResponse);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            var tempHeader = authHeader.substring(7);

            return tempHeader.equals("null") ? null : tempHeader;
        }

        return null;
    }
}

