package cl.clinica.backend_clinica.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arangodb.springframework.core.ArangoOperations;

import cl.clinica.backend_clinica.model.Especialidad;
import cl.clinica.backend_clinica.repository.EspecialidadRepository;
import cl.clinica.backend_clinica.service.EspecialidadService;

@RestController
@RequestMapping("/api/especialidades")
@CrossOrigin(origins = "*")
public class EspecialidadController {

    private EspecialidadService especialidadService;

    @Autowired
    public EspecialidadController(EspecialidadService especialidadService){
        this.especialidadService = especialidadService;
    }

    //Lista todas las especialidades existentes con sus respectivos medicos
    @GetMapping
    public ResponseEntity<Iterable<Especialidad>> listarEspecialidades() {
        Iterable<Especialidad> lista = especialidadService.findAll();
        return ResponseEntity.ok(lista);
    }
}