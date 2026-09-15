package cl.clinica.backend_clinica.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String PREFIJO_BEARER = "Bearer ";
    private final JwtService jwtService;
    private final CookieTokenService cookieTokenService;

    public JwtAuthFilter(JwtService jwtService, CookieTokenService cookieTokenService) {
        this.jwtService = jwtService;
        this.cookieTokenService = cookieTokenService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = extraerToken(request);

        if (token != null && jwtService.esValido(token)) {
            String rut = jwtService.extraerRut(token);
            String rol = jwtService.extraerRol(token);

            var autenticacion = new UsernamePasswordAuthenticationToken(
                    rut, 
                    null, 
                    List.of(new SimpleGrantedAuthority("ROLE_" + rol))
            );
            SecurityContextHolder.getContext().setAuthentication(autenticacion);
        }

        filterChain.doFilter(request, response);
    }

    private String extraerToken(HttpServletRequest request) {
        String desdeCookie = cookieTokenService.leer(request);
        if (desdeCookie != null) return desdeCookie;

        String cabecera = request.getHeader("Authorization");
        if (cabecera != null && cabecera.startsWith(PREFIJO_BEARER)) {
            return cabecera.substring(PREFIJO_BEARER.length());
        }
        return null;
    }
}