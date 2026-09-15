package cl.clinica.backend_clinica.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.clinica.backend_clinica.model.Convenio;
import cl.clinica.backend_clinica.model.Usuario;
import cl.clinica.backend_clinica.repository.AfiliacionRepository;
import cl.clinica.backend_clinica.repository.ConvenioRepository;
import cl.clinica.backend_clinica.repository.UsuarioRepository;
import cl.clinica.backend_clinica.web.request.AfiliacionRequest;
import cl.clinica.backend_clinica.web.response.AfiliacionResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConvenioService Tests")
public class ConvenioServiceTest {

    @Mock
    private ConvenioRepository convenioRepository;

    @Mock
    private AfiliacionRepository afiliacionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ConvenioService convenioService;

    private String primer_rut;
    private String segundo_rut;
    private String tercer_rut;
    private String cuarto_rut;

    @BeforeEach 
    void before(){
        primer_rut= "1111111-1";
        segundo_rut= "2222222-2";
        tercer_rut= "3333333-3";
        cuarto_rut= "4444444-4";
    }

    @Test
    @DisplayName("listarPlanes retorna la lista de convenios")
    void listarPlanesTest() {
        Convenio c1 = new Convenio();
        when(convenioRepository.findAll()).thenReturn(List.of(c1));

        Iterable<Convenio> resultado = convenioService.listarPlanes();

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("consultarAfiliacion y el rut esta afiliado a algun convenio")
    void consultarAfiliacionTest() {
        AfiliacionResponse response = new AfiliacionResponse();
        when(afiliacionRepository.consultarAfiliacion(primer_rut)).thenReturn(response);

        Optional<AfiliacionResponse> opt = convenioService.consultarAfiliacion(primer_rut);

        assertThat(opt).isPresent();
        assertThat(opt.get()).isEqualTo(response);
    }

    @Test
    @DisplayName("afiliarConvenio lanza error si el titular ya es carga activa de otro")
    void afiliarConvenioConflictoTitularTest() {
        AfiliacionRequest request = new AfiliacionRequest();
        request.setRutTitular(primer_rut);

        when(afiliacionRepository.buscarConflictoTitularComoCarga(primer_rut)).thenReturn(List.of(segundo_rut));

        assertThatThrownBy(() -> convenioService.afiliarConvenio(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ya cuenta con cobertura activa como carga familiar del titular: 2222222-2");

        verify(usuarioRepository, never()).save(any());
        verify(afiliacionRepository, never()).insertarAfiliacion(any(), any(), any());
    }

    @Test
    @DisplayName("afiliarConvenio lanza error si una de las cargas pertenece a otro plan activo")
    void afiliarConvenioConflictoCargaTest() {
        AfiliacionRequest request = new AfiliacionRequest();
        request.setRutTitular(primer_rut);
        request.setBeneficiarios(List.of(tercer_rut));

        when(afiliacionRepository.buscarConflictoTitularComoCarga(primer_rut)).thenReturn(Collections.emptyList());
        when(afiliacionRepository.buscarConflictoCargaEnOtroPlan(primer_rut, tercer_rut)).thenReturn(List.of(Map.of("titular", cuarto_rut)));

        assertThatThrownBy(() -> convenioService.afiliarConvenio(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La carga 3333333-3 ya se encuentra inscrita en un convenio activo a nombre de: 4444444-4");

        verify(afiliacionRepository, never()).desactivarConveniosPrevios(any());
        verify(afiliacionRepository, never()).insertarAfiliacion(any(), any(), any());
    }

    @Test
    @DisplayName("afiliarConvenio camino normal, añade nueva afiliacion y desactiva la anterior")
    void afiliarConvenioExitosoNuevoTitularTest() {
        AfiliacionRequest request = new AfiliacionRequest();
        request.setRutTitular(primer_rut);
        request.setNombre("Carlos");
        request.setApellido("Soto");
        request.setConvenioKey("CONV-PREMIUM");
        request.setBeneficiarios(List.of(primer_rut));

        when(afiliacionRepository.buscarConflictoTitularComoCarga(primer_rut)).thenReturn(Collections.emptyList());
        when(usuarioRepository.existsById(primer_rut)).thenReturn(false);
        when(afiliacionRepository.insertarAfiliacion(eq(primer_rut), eq("CONV-PREMIUM"), any())).thenReturn(Map.of("status", "ok"));

        Map resultado = convenioService.afiliarConvenio(request);

        assertThat(resultado).containsEntry("status", "ok");
        verify(usuarioRepository).save(any(Usuario.class));
        verify(afiliacionRepository).desactivarConveniosPrevios(primer_rut);
        verify(afiliacionRepository).insertarAfiliacion(primer_rut, "CONV-PREMIUM", List.of(primer_rut));
    }

    @Test
    @DisplayName("removerCarga camino exitoso borra la carga correspondiente")
    void removerCargaExitosoTest() {
        when(afiliacionRepository.desvincularCarga(tercer_rut)).thenReturn(List.of(Map.of("modificado", true)));

        boolean res = convenioService.removerCarga(tercer_rut);

        assertThat(res).isTrue();
        verify(afiliacionRepository).desvincularCarga(tercer_rut);
    }

    @Test
    @DisplayName("removerCarga no existe una carga ligada y regresa false")
    void removerCargaFallaTest() {
        when(afiliacionRepository.desvincularCarga(tercer_rut)).thenReturn(Collections.emptyList());

        boolean res = convenioService.removerCarga(tercer_rut);

        assertThat(res).isFalse();
    }
}