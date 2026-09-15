package cl.clinica.backend_clinica.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.clinica.backend_clinica.model.Convenio;
import cl.clinica.backend_clinica.model.Usuario;
import cl.clinica.backend_clinica.repository.AfiliacionRepository;
import cl.clinica.backend_clinica.repository.ConvenioRepository;
import cl.clinica.backend_clinica.repository.UsuarioRepository;
import cl.clinica.backend_clinica.web.request.AfiliacionRequest;
import cl.clinica.backend_clinica.web.response.AfiliacionResponse;

@Service
public class ConvenioService {

    private final ConvenioRepository convenioRepository;
    private final AfiliacionRepository afiliacionRepository;
    private final UsuarioRepository usuarioRepository;

    

    @Autowired
    public ConvenioService(
            ConvenioRepository convenioRepository,
            AfiliacionRepository afiliacionRepository,
            UsuarioRepository usuarioRepository) {
        this.convenioRepository = convenioRepository;
        this.afiliacionRepository = afiliacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    

    public Iterable<Convenio> listarPlanes() {
        return convenioRepository.findAll();
    }

    public Optional<AfiliacionResponse> consultarAfiliacion(String rut) {
    AfiliacionResponse response = afiliacionRepository.consultarAfiliacion(rut);
    return Optional.ofNullable(response);
    }

    public Map afiliarConvenio(AfiliacionRequest request) {
        String titularRut = request.getRutTitular().trim();

        // Validar que el titular no sea carga activa de otro
        List<String> conflictosTitular = afiliacionRepository.buscarConflictoTitularComoCarga(titularRut);
        if (!conflictosTitular.isEmpty()) {
            throw new IllegalArgumentException("El paciente " + titularRut + " ya cuenta con cobertura activa como carga familiar del titular: " + conflictosTitular.get(0) + ". Debe desvincularse de dicho plan antes de contratar uno propio.");
        }

        // Validar que las cargas no pertenezcan a otro plan activo
        if (request.getBeneficiarios() != null) {
            for (String beneficiario : request.getBeneficiarios()) {
                if (beneficiario.equals(titularRut)) continue;

                List<Map> conflictosCarga = afiliacionRepository.buscarConflictoCargaEnOtroPlan(titularRut, beneficiario);
                if (!conflictosCarga.isEmpty()) {
                    throw new IllegalArgumentException("La carga " + beneficiario + " ya se encuentra inscrita en un convenio activo a nombre de: " + conflictosCarga.get(0).get("titular"));
                }
            }
        }

        // Crear titular si no existe
        if (!usuarioRepository.existsById(titularRut)) {
            Usuario titular = new Usuario();
            titular.setRut(titularRut);
            titular.setNombre(request.getNombre() != null ? request.getNombre().trim() : "");
            titular.setApellido(request.getApellido() != null ? request.getApellido().trim() : "");
            titular.setEdad(request.getEdad() != null ? request.getEdad() : 0);
            titular.setCorreo(request.getCorreo() != null ? request.getCorreo().trim() : "");
            usuarioRepository.save(titular);
        }

        // Crear o actualizar cargas
        if (request.getNuevasCargas() != null) {
            for (AfiliacionRequest.NuevoUsuarioDTO cargaDto : request.getNuevasCargas()) {
                if (cargaDto.getRut() != null && !cargaDto.getRut().isBlank()) {
                    String rutCarga = cargaDto.getRut().trim();
                    Usuario carga = usuarioRepository.findById(rutCarga).orElse(new Usuario());
                    carga.setRut(rutCarga);
                    carga.setNombre(cargaDto.getNombre() != null ? cargaDto.getNombre().trim() : "");
                    carga.setApellido(cargaDto.getApellido() != null ? cargaDto.getApellido().trim() : "");
                    carga.setEdad(cargaDto.getEdad() != null ? cargaDto.getEdad() : 0);
                    carga.setCorreo(cargaDto.getCorreo() != null ? cargaDto.getCorreo().trim() : "");
                    carga.setPassword(cargaDto.getPassword() != null ? cargaDto.getPassword().trim() : "");
                    usuarioRepository.save(carga);
                }
            }
        }

        // Desactivar planes anteriores
        afiliacionRepository.desactivarConveniosPrevios(titularRut);

        // Guardar nueva afiliación
        return afiliacionRepository.insertarAfiliacion(
                titularRut,
                request.getConvenioKey(),
                request.getBeneficiarios()
        );
    }

    public boolean removerCarga(String rutCarga) {
        List<Map> modificados = afiliacionRepository.desvincularCarga(rutCarga.trim());
        return !modificados.isEmpty();
    }
}