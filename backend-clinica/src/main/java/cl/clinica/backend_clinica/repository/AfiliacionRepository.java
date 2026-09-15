package cl.clinica.backend_clinica.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;

import cl.clinica.backend_clinica.model.Afiliacion;
import cl.clinica.backend_clinica.web.response.AfiliacionResponse;

@Repository
public interface AfiliacionRepository extends ArangoRepository<Afiliacion, String> {

    @Query("""
        FOR a IN afiliaciones_convenio
            FILTER a.activo == true AND @rut IN a.beneficiarios
            FOR conv IN convenios
                FILTER conv._key == a.convenio_key
                LIMIT 1
                RETURN {
                    nombreConvenio: conv.nombre,
                    ahorro: conv.ahorro,
                    titular: a.rut_titular != null ? a.rut_titular : a.titular_rut,
                    beneficiarios: a.beneficiarios
                }
    """)
    AfiliacionResponse consultarAfiliacion(@Param("rut") String rut);

    @Query("""
        FOR a IN afiliaciones_convenio
        FILTER a.activo == true
        LET dueno = a.rut_titular != null ? a.rut_titular : a.titular_rut
        FILTER dueno != null AND dueno != @titular AND @titular IN a.beneficiarios
        RETURN dueno
    """)
    List<String> buscarConflictoTitularComoCarga(@Param("titular") String titular);

    @Query("""
        FOR a IN afiliaciones_convenio
        LET dueno = a.rut_titular != null ? a.rut_titular : a.titular_rut
        FILTER a.activo == true AND dueno != @titular AND @rut IN a.beneficiarios
        RETURN { titular: dueno, plan: a.convenio_key }
    """)
    List<Map> buscarConflictoCargaEnOtroPlan(@Param("titular") String titular, @Param("rut") String rut);

    @Query("""
        FOR a IN afiliaciones_convenio
        LET dueno = a.rut_titular != null ? a.rut_titular : a.titular_rut
        FILTER dueno == @titular AND a.activo == true
        UPDATE a WITH { activo: false } IN afiliaciones_convenio
    """)
    void desactivarConveniosPrevios(@Param("titular") String titular);

    @Query("""
        LET ahora = DATE_NOW()
        LET unAnoDespues = DATE_ADD(ahora, 1, "year")
        INSERT {
            convenio_key: @convenioKey,
            rut_titular: @titular,
            beneficiarios: @beneficiarios,
            activo: true,
            fecha_inicio: DATE_ISO8601(ahora),
            fecha_vencimiento: DATE_ISO8601(unAnoDespues)
        } IN afiliaciones_convenio
        RETURN NEW
    """)
    Map insertarAfiliacion(
        @Param("titular") String titular, 
        @Param("convenioKey") String convenioKey, 
        @Param("beneficiarios") List<String> beneficiarios
    );

    @Query("""
        FOR a IN afiliaciones_convenio
        FILTER a.activo == true AND @rut IN a.beneficiarios
        LET nuevosBeneficiarios = (FOR b IN a.beneficiarios FILTER b != @rut RETURN b)
        UPDATE a WITH { beneficiarios: nuevosBeneficiarios } IN afiliaciones_convenio
        RETURN NEW
    """)
    List<Map> desvincularCarga(@Param("rut") String rut);
}