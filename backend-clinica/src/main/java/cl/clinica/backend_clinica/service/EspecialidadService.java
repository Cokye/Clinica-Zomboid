package cl.clinica.backend_clinica.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.clinica.backend_clinica.model.Especialidad;
import cl.clinica.backend_clinica.repository.EspecialidadRepository;

@Service 
public class EspecialidadService {
    private final EspecialidadRepository especialidadRepository;

    @Autowired 
    public EspecialidadService(EspecialidadRepository especialidadRepository){
        this.especialidadRepository = especialidadRepository;
    }

    public Iterable<Especialidad> findAll(){
        return this.especialidadRepository.findAll();
    }
    
}
