package cl.clinica.backend_clinica.service;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;

import cl.clinica.backend_clinica.model.Usuario;
import cl.clinica.backend_clinica.repository.UsuarioRepository;
import cl.clinica.backend_clinica.security.CookieTokenService;
import cl.clinica.backend_clinica.security.JwtService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private CookieTokenService cookieTokenService;

    @InjectMocks
    private AuthService authService;

    //Variables
    private String rut;
    private String password;
    private Usuario usuario;
    
    @BeforeEach 
    void before(){
        rut = "1111111-1";
        password = "1234";
        usuario = new Usuario(rut,"Jose","Arando",12,"jose.arando@email.cl",password);
    }

    @Test
    @DisplayName("Autenticar exitoso retorna cookie con JWT")
    void autenticarExitosoTest() {

        String tokenSimulado = "token-prueba-jwt";
        ResponseCookie cookieEsperada = ResponseCookie.from("jwt", tokenSimulado).build();
        
        when(usuarioRepository.findById(rut)).thenReturn(Optional.of(usuario));
        when(jwtService.generarToken(rut)).thenReturn(tokenSimulado);
        when(cookieTokenService.crear("token-prueba")).thenReturn(cookieEsperada);

        ResponseCookie resultado = authService.autenticar(rut, password);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getValue()).isEqualTo("token-prueba");
        verify(jwtService).generarToken(rut);
        verify(cookieTokenService).crear("token-prueba");
    }

    @Test
    @DisplayName("Autenticar lanza error si el paciente no existe")
    void autenticarUsuarioNoExisteTest() {
        when(usuarioRepository.findById(rut)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.autenticar(rut, password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Paciente no registrado en el sistema");

        verify(jwtService, never()).generarToken(any());
        verify(cookieTokenService, never()).crear(any());
    }

    @Test
    @DisplayName("Autenticar lanza error si la contraseña es incorrecta")
    void autenticarPasswordIncorrectaTest() {

        when(usuarioRepository.findById(rut)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> authService.autenticar(rut, "claveErronea"))
                .isInstanceOf(SecurityException.class)
                .hasMessage("RUT o contraseña incorrectos");

        verify(jwtService, never()).generarToken(any());
        verify(cookieTokenService, never()).crear(any());
    }

    @Test
    @DisplayName("Cerrar sesión llama a borrar cookie")
    void cerrarSesionTest() {
        ResponseCookie cookieBorrada = ResponseCookie.from("jwt", "").maxAge(0).build();
        when(cookieTokenService.borrar()).thenReturn(cookieBorrada);

        ResponseCookie resultado = authService.cerrarSesion();

        assertThat(resultado).isNotNull();
        assertThat(resultado.getMaxAge().getSeconds()).isZero();
        verify(cookieTokenService).borrar();
    }
}