package cl.clinica.backend_clinica.service;

import java.util.Map;
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

import cl.clinica.backend_clinica.model.Especialidad;
import cl.clinica.backend_clinica.model.Usuario;
import cl.clinica.backend_clinica.repository.EspecialidadRepository;
import cl.clinica.backend_clinica.repository.UsuarioRepository;
import cl.clinica.backend_clinica.web.request.NuevoPacienteRequest;
import cl.clinica.backend_clinica.web.response.CotizacionResponse;
import cl.clinica.backend_clinica.web.response.PacienteResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("PacienteService")
public class PacienteServiceTest {
    
    @Mock 
    private  EspecialidadRepository especialidadRepository;
    @Mock 
    private UsuarioRepository usuarioRepository;
    @InjectMocks 
    private PacienteService pacienteService;

    private String rut;
    private String rut_vacio;
    private Usuario usuario;
    private Usuario usuario_vacio;
    private Especialidad esp;
    private String especialidad;
    private NuevoPacienteRequest nuevoPacienteRequest;

    @BeforeEach 
    void before(){
        rut = "1111111-1";
        rut_vacio = "";
        especialidad = "Oftalmologia";
        usuario = new Usuario("1111111-1","Jose","Arando",12,"jose.arando@email.cl","1234");
        usuario_vacio= new Usuario();
        nuevoPacienteRequest = new NuevoPacienteRequest("1111111-1","Jose","Arando",12,"jose.arando@email.cl","1234");
    }

    @Test 
    @DisplayName("Buscar por rut, con rut valido y respuesta de la bd")
    void buscarPorRutTest(){
        when(usuarioRepository.findById(rut)).thenReturn(Optional.of(usuario));
        Optional<PacienteResponse> pacienteResponse = pacienteService.buscarPorRut(rut);
        assertThat(pacienteResponse).isPresent();
        PacienteResponse response = pacienteResponse.get();
        assertThat(response.getRut()).isEqualTo(rut);
    }

    @Test 
    @DisplayName("Buscar por rut, el rut es valido pero el usuario no existe en la BD")
    void buscarPorRutNoExisteTest(){
        when(usuarioRepository.findById(rut)).thenReturn(Optional.empty());
        Optional<PacienteResponse> pacienteResponse = pacienteService.buscarPorRut(rut);
        assertThat(pacienteResponse).isEmpty();
    }

    @Test 
    @DisplayName("Buscar por rut, si es vacio o con espacios")
    void buscarPorRutVacioTest(){
        assertThatThrownBy(() -> pacienteService.buscarPorRut(rut_vacio))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("El RUT no puede estar vacío");

        verify(usuarioRepository, never()).findById(any());

    }

    @Test
    @DisplayName("Cotizar atencion pero el paciente no existe") 
    void cotizarAtencionPacienteNotTest(){
        when(usuarioRepository.findById(rut)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pacienteService.cotizarAtencion(rut, especialidad))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Paciente no encontrado");

        verify(especialidadRepository, never()).findById(any());
    }


    @Test 
    @DisplayName ("Cotizar atencion con paciente existente y convenio existente")
    void cotizarAtencionCorrectaTest(){
        when(usuarioRepository.findById(rut)).thenReturn(Optional.of(usuario));

        //Preguntar al profesor
        esp = new Especialidad();
        esp.setPrecioBase(30000);
        when(especialidadRepository.findById("Diamante")).thenReturn(Optional.of(esp));

        Map<String, Object> convenio = Map.of("nombreConvenio", "Diamante","ahorro", 30);
        
        when(usuarioRepository.obtenerConvenioPorRut(rut)).thenReturn(convenio);

        CotizacionResponse res = pacienteService.cotizarAtencion(rut, "Diamante");

        assertThat(res.getPrecioBase()).isEqualTo(30000);
        assertThat(res.getMontoDescuento()).isEqualTo(9000); // 30% de 30000
        assertThat(res.getTotalAPagar()).isEqualTo(21000);
        assertThat(res.getNombreConvenio()).isEqualTo("Diamante");
    }

    @Test
    @DisplayName ("Cotizar atencion, el rut existe pero el convenio no existe")
    void cotizarAtencionSinConvenioTest(){
        when(usuarioRepository.findById(rut)).thenReturn(Optional.of(usuario));
        when(especialidadRepository.findById("No-existe")).thenReturn(Optional.empty());
        when(usuarioRepository.obtenerConvenioPorRut(rut)).thenReturn(null);

        CotizacionResponse res = pacienteService.cotizarAtencion(rut, "No-existe");

        assertThat(res.getPrecioBase()).isEqualTo(30000);
        assertThat(res.getMontoDescuento()).isEqualTo(0);
        assertThat(res.getNombreConvenio()).isEqualTo("Sin Convenio");

    }



    @Test 
    void registrarPacienteTest(){
        when(usuarioRepository.existsById(nuevoPacienteRequest.getRut())).thenReturn(false);
        String respuesta = pacienteService.registrarPaciente(nuevoPacienteRequest);
        assertThat(respuesta).isEqualTo("Jose Arando");
        
    }

    @Test
    @DisplayName("Registrar Paciente retorna error")
    void registrarPacienteFallaSiElRutYaExiste() {
    when(usuarioRepository.existsById(rut)).thenReturn(true);

    assertThatThrownBy(() -> pacienteService.registrarPaciente(nuevoPacienteRequest))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("El usuario ya se encuentra registrado con este RUT");

    verify(usuarioRepository, never()).save(any());
}

}
