package cl.clinica.backend_clinica.service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.clinica.backend_clinica.model.Especialidad;
import cl.clinica.backend_clinica.repository.EspecialidadRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("EspecialidadService Tests")
public class EspecialidadServiceTest {

    @Mock
    private EspecialidadRepository especialidadRepository;

    @InjectMocks
    private EspecialidadService especialidadService;

    @Test
    @DisplayName("findAll, regresa algun resultado")
    void findAllTest() {
        Especialidad esp = new Especialidad();
        when(especialidadRepository.findAll()).thenReturn(List.of(esp));

        Iterable<Especialidad> resultado = especialidadService.findAll();

        assertThat(resultado).hasSize(1);
    }
}