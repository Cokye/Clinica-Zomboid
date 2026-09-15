package cl.clinica.backend_clinica.model;

import java.util.List;

import com.arangodb.serde.jackson.Id;
import com.arangodb.serde.jackson.Key;
import com.arangodb.springframework.annotation.ArangoId;
import com.arangodb.springframework.annotation.Document;

@Document ("afiliaciones_convenio")
public class Afiliacion {

    @ArangoId 
    private String arangoId;

    @Id 
    @Key 
    private String key;

    private String convenioKey;
    private String rutTitular;
    private List<String> beneficiarios;
    private Boolean activo;
    private String fechaInicio;
    private String fechaVencimiento;

    public Afiliacion(){}

    public Afiliacion(String convenioKey, String rutTitular, List<String> beneficiarios, Boolean activo, String fechaInicio, String fechaVencimiento) {
        this.convenioKey = convenioKey;
        this.rutTitular = rutTitular;
        this.beneficiarios = beneficiarios;
        this.activo = activo;
        this.fechaInicio = fechaInicio;
        this.fechaVencimiento = fechaVencimiento;
    }


    //GETTERS
    public String getArangoId() { return arangoId; }
    public String getKey() { return key; }
    public String getConvenioKey() { return convenioKey; }
    public String getRutTitular() { return rutTitular; }
    public List<String> getBeneficiarios() { return beneficiarios; }
    public Boolean getActivo() { return activo; }
    public String getFechaInicio() { return fechaInicio; }
    public String getFechaVencimiento() { return fechaVencimiento; }


    //SETTERS
    public void setArangoId(String arangoId) { this.arangoId = arangoId; }
    public void setKey(String key) { this.key = key; }
    public void setConvenioKey(String convenioKey) { this.convenioKey = convenioKey; }
    public void setRutTitular(String rutTitular) { this.rutTitular = rutTitular; }
    public void setBeneficiarios(List<String> beneficiarios) { this.beneficiarios = beneficiarios; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }
    public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
}
