package cl.clinica.backend_clinica.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.clinica.backend_clinica.model.Especialidad;
import cl.clinica.backend_clinica.model.Usuario;
import cl.clinica.backend_clinica.repository.EspecialidadRepository;
import cl.clinica.backend_clinica.repository.UsuarioRepository;
import cl.clinica.backend_clinica.web.request.NuevoPacienteRequest;
import cl.clinica.backend_clinica.web.response.CotizacionResponse;
import cl.clinica.backend_clinica.web.response.PacienteResponse;

@Service 
public class PacienteService {
    
    private final EspecialidadRepository especialidadRepository;
    private final UsuarioRepository usuarioRepository;


    //Necesita los dos repository ya que el paciente necesita generar la consula sobre que especilidad necesita
    @Autowired 
    public PacienteService( UsuarioRepository usuarioRepository, EspecialidadRepository especialidadRepository){
        this.usuarioRepository = usuarioRepository;
        this.especialidadRepository = especialidadRepository;
    }

    public Optional<PacienteResponse> buscarPorRut(String rut) {

        if (rut == null || rut.trim().isEmpty()){
            throw new IllegalArgumentException("El RUT no puede estar vacío");
        }

        String rutLimpio = rut.trim();

        return usuarioRepository.findById(rutLimpio).map(usuario -> {
            PacienteResponse response = new PacienteResponse();
            response.setRut(usuario.getRut() != null ? usuario.getRut() : rut);
            
            String nombre = usuario.getNombre() != null ? usuario.getNombre() : "";
            String apellido = usuario.getApellido() != null ? usuario.getApellido() : "";
            response.setNombreCompleto((nombre + " " + apellido).trim());
            
            response.setEdad(usuario.getEdad());
            response.setCorreo(usuario.getCorreo());
            return response;
        });
    }

    public CotizacionResponse cotizarAtencion(String rut, String especialidadKey) {
        Usuario usuario = usuarioRepository.findById(rut)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

        Especialidad esp = especialidadRepository.findById(especialidadKey).orElse(null);
        int precioBase = (esp != null) ? esp.getPrecioBase() : 30000;

        Map<String, Object> resultadoConvenio = usuarioRepository.obtenerConvenioPorRut(rut);

        int porcentajeAhorro = 0;
        String nombreConvenio = "Sin Convenio";

        if (resultadoConvenio != null) {
            if (resultadoConvenio.containsKey("ahorro") && resultadoConvenio.get("ahorro") != null) {
                porcentajeAhorro = ((Number) resultadoConvenio.get("ahorro")).intValue();
            }
            if (resultadoConvenio.containsKey("nombreConvenio") && resultadoConvenio.get("nombreConvenio") != null) {
                nombreConvenio = (String) resultadoConvenio.get("nombreConvenio");
            }
        }

        int montoDescuento = (precioBase * porcentajeAhorro) / 100;
        int totalAPagar = precioBase - montoDescuento;

        String nombre = usuario.getNombre() != null ? usuario.getNombre() : "";
        String apellido = usuario.getApellido() != null ? usuario.getApellido() : "";

        CotizacionResponse response = new CotizacionResponse();
        response.setRut(usuario.getRut() != null ? usuario.getRut() : rut);
        response.setNombrePaciente((nombre + " " + apellido).trim());
        response.setNombreConvenio(nombreConvenio);
        response.setPorcentajeDescuento(porcentajeAhorro);
        response.setPrecioBase(precioBase);
        response.setMontoDescuento(montoDescuento);
        response.setTotalAPagar(totalAPagar);

        return response;
    }

    public String registrarPaciente(NuevoPacienteRequest request) {
        String rutLimpio = request.getRut().trim();

        if (usuarioRepository.existsById(rutLimpio)) {
            throw new IllegalStateException("El usuario ya se encuentra registrado con este RUT");
        }

        Usuario nuevo = new Usuario();
        nuevo.setRut(rutLimpio);
        nuevo.setNombre(request.getNombre().trim());
        nuevo.setApellido(request.getApellido().trim());
        nuevo.setEdad(request.getEdad() != null ? request.getEdad() : 0);
        nuevo.setCorreo(request.getCorreo() != null ? request.getCorreo().trim() : "");
        nuevo.setPassword(request.getPassword().trim());

        this.usuarioRepository.save(nuevo);

        return nuevo.getNombre() + " " + nuevo.getApellido();
    }
}
