package cl.clinica.backend_clinica.repository;

import org.springframework.stereotype.Repository;

import com.arangodb.springframework.repository.ArangoRepository;

import cl.clinica.backend_clinica.model.Especialidad;

@Repository 
public interface EspecialidadRepository extends ArangoRepository<Especialidad, String>{
    
}
