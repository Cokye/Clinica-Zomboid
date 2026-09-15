package cl.clinica.backend_clinica.repository;

import org.springframework.stereotype.Repository;

import com.arangodb.springframework.repository.ArangoRepository;

import cl.clinica.backend_clinica.model.Convenio;

@Repository 
public interface ConvenioRepository extends ArangoRepository<Convenio, String> {
    
}
