package cl.clinica.backend_clinica.web.controller;

import cl.clinica.backend_clinica.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String rut = credenciales.get("rut");
        String password = credenciales.get("password");

        if (rut == null || rut.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El RUT es obligatorio"));
        }

        try {
            ResponseCookie cookie = authService.autenticar(rut, password);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(Map.of(
                            "mensaje", "Autenticación exitosa",
                            "rut", rut.trim()
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookieBorrada = authService.cerrarSesion();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieBorrada.toString())
                .body(Map.of("mensaje", "Sesión cerrada correctamente"));
    }
}