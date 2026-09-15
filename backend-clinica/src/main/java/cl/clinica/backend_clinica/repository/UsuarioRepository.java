package cl.clinica.backend_clinica.repository;

import java.util.Map;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import cl.clinica.backend_clinica.model.Usuario;

@Repository 
public interface UsuarioRepository extends ArangoRepository<Usuario, String>{

    @Query("""
        LET afiliacion = (
            FOR a IN afiliaciones_convenio
            FILTER @rut IN a.beneficiarios AND a.activo == true
            LIMIT 1
            RETURN a
        )[0]
        LET conv = afiliacion ? DOCUMENT("convenios", afiliacion.convenio_key) : null
        RETURN {
            tieneConvenio: conv != null,
            nombreConvenio: conv ? conv.nombre : "Sin Convenio",
            ahorro: conv ? conv.ahorro : 0
        }
    """)
    Map<String, Object> obtenerConvenioPorRut(@Param("rut") String rut);
    
}
