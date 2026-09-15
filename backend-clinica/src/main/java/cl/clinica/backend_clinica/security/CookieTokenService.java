package cl.clinica.backend_clinica.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CookieTokenService {

    public static final String NOMBRE_COOKIE = "token";

    private final boolean segura;
    private final String sameSite;
    private final Duration duracion;

    public CookieTokenService(
            @Value("${app.cookie.secure:false}") boolean segura,
            @Value("${app.cookie.same-site:Lax}") String sameSite,
            @Value("${jwt.expiration-ms}") long expiracionMs) {
        this.segura = segura;
        this.sameSite = sameSite;
        this.duracion = Duration.ofMillis(expiracionMs);
    }

    public ResponseCookie crear(String token) {
        return base(token).maxAge(duracion).build();
    }

    public ResponseCookie borrar() {
        return base("").maxAge(0).build();
    }

    private ResponseCookie.ResponseCookieBuilder base(String valor) {
        return ResponseCookie.from(NOMBRE_COOKIE, valor)
                .httpOnly(true)
                .secure(segura)
                .sameSite(sameSite)
                .path("/");
    }

    public String leer(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (NOMBRE_COOKIE.equals(cookie.getName()) && !cookie.getValue().isBlank()) {
                return cookie.getValue();
            }
        }
        return null;
    }
}