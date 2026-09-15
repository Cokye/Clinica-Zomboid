package cl.clinica.backend_clinica.web.controller;

import cl.clinica.backend_clinica.service.PacienteService;
import cl.clinica.backend_clinica.web.request.NuevoPacienteRequest;
import cl.clinica.backend_clinica.web.response.CotizacionResponse;
import cl.clinica.backend_clinica.web.response.PacienteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    @Autowired
    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping("/{rut}")
    public ResponseEntity<?> buscarPorRut(@PathVariable String rut) {
        return pacienteService.buscarPorRut(rut)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body((PacienteResponse) null));
    }

    @GetMapping("/{rut}/cotizacion")
    public ResponseEntity<?> cotizarAtencion(
            @PathVariable String rut,
            @RequestParam(name = "especialidad", defaultValue = "medicina_general") String especialidadKey) {
        try {
            CotizacionResponse response = pacienteService.cotizarAtencion(rut, especialidadKey);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarPaciente(@RequestBody NuevoPacienteRequest request) {
        if (request.getRut() == null || request.getRut().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El RUT es obligatorio"));
        }
        if (request.getNombre() == null || request.getApellido() == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Nombre y apellido son obligatorios"));
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "La contraseña es obligatoria"));
        }

        try {
            String nombreCompleto = pacienteService.registrarPaciente(request);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Paciente registrado exitosamente",
                    "rut", request.getRut().trim(),
                    "nombreCompleto", nombreCompleto
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }
}