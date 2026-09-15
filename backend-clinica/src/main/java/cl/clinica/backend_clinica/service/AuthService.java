package cl.clinica.backend_clinica.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import cl.clinica.backend_clinica.model.Usuario;
import cl.clinica.backend_clinica.repository.UsuarioRepository;
import cl.clinica.backend_clinica.security.CookieTokenService;
import cl.clinica.backend_clinica.security.JwtService;

@Service 
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final CookieTokenService cookieTokenService;


    @Autowired 
    public AuthService( UsuarioRepository usuarioRepository, JwtService jwtService, CookieTokenService cookieTokenService){
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.cookieTokenService = cookieTokenService;
    }

    public ResponseCookie autenticar(String rut, String password) {
        String rutLimpio = rut.trim();

        Optional<Usuario> optUsuario = usuarioRepository.findById(rutLimpio);
        if (optUsuario.isEmpty()) {
            throw new IllegalArgumentException("Paciente no registrado en el sistema");
        }

        Usuario usuario = optUsuario.get();

        if (password != null && !password.isBlank()) {
            if (usuario.getPassword() == null || !usuario.getPassword().equals(password.trim())) {
                throw new SecurityException("RUT o contraseña incorrectos");
            }
        }

        // Generar cookie JWT
        String token = jwtService.generarToken(rutLimpio);
        return cookieTokenService.crear(token);
    }

    public ResponseCookie cerrarSesion() {
        return cookieTokenService.borrar();
    }
}
