package cl.clinica.backend_clinica.web.response;
import java.util.List;


public class AfiliacionResponse {
    private String nombreConvenio;
    private Integer ahorro;
    private String titular;
    private List<String> beneficiarios;

    public AfiliacionResponse(){}

    public AfiliacionResponse(String nombreConvenio, Integer ahorro, String titular, List<String> beneficiarios){
        this.nombreConvenio = nombreConvenio;
        this.ahorro = ahorro;
        this.titular = titular;
        this.beneficiarios = beneficiarios;
    }


    //GETTERS
    public String getNombreConvenio() {return nombreConvenio;}
    public Integer getAhorro() {return ahorro;}
    public String getTitular() {return titular;}
    public List<String> getBeneficiarios() {return beneficiarios;}

    //SETTERS
    public void setNombreConvenio(String nombreConvenio) {this.nombreConvenio = nombreConvenio;}
    public void setAhorro(Integer ahorro) {this.ahorro = ahorro;}
    public void setTitular(String titular) {this.titular = titular;}
    public void setBeneficiarios(List<String> beneficiarios) {this.beneficiarios = beneficiarios;}
    

    
}