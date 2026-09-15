package cl.clinica.backend_clinica.web.controller;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.clinica.backend_clinica.model.Convenio;
import cl.clinica.backend_clinica.service.ConvenioService;
import cl.clinica.backend_clinica.web.request.AfiliacionRequest;

@RestController
@RequestMapping("/api/convenios")
@CrossOrigin(origins = "*")
public class ConvenioController {

    private final ConvenioService convenioService;

    @Autowired
    public ConvenioController(ConvenioService convenioService) {
        this.convenioService = convenioService;
    }

    @GetMapping
    public ResponseEntity<Iterable<Convenio>> listarPlanes() {
        return ResponseEntity.ok(convenioService.listarPlanes());
    }

    @GetMapping("/afiliacion/{rut}")
    public ResponseEntity<?> obtenerAfiliacion(@PathVariable String rut) {
        return convenioService.consultarAfiliacion(rut)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", "No se encontró convenio activo para el RUT ingresado")));
    }

    @PostMapping("/afiliar")
    public ResponseEntity<?> afiliarConvenio(@RequestBody AfiliacionRequest request) {
        if (request.getRutTitular() == null || request.getConvenioKey() == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "RUT titular y convenio son obligatorios"));
        }

        try {
            Map resultado = convenioService.afiliarConvenio(request);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @DeleteMapping("/afiliacion/carga")
    public ResponseEntity<?> removerCarga(@RequestParam String rutCarga) {
        boolean eliminada = convenioService.removerCarga(rutCarga);
        if (!eliminada) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El RUT no se encuentra como carga activa en ningún plan."));
        }
        return ResponseEntity.ok(Map.of("mensaje", "Carga desvinculada exitosamente del convenio."));
    }
}