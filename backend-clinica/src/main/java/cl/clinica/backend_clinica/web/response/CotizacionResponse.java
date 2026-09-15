package cl.clinica.backend_clinica.web.response;


public class CotizacionResponse {
    private String rut;
    private String nombrePaciente;
    private String nombreConvenio;
    private Integer porcentajeDescuento;
    private Integer precioBase;
    private Integer montoDescuento;
    private Integer totalAPagar;

    public CotizacionResponse(){}

    public CotizacionResponse(String rut, String nombrePaciente, String nombreConvenio, Integer porcentajeDescuento, Integer precioBase, Integer montoDescuento, Integer totalAPagar){
        this.rut = rut;
        this.nombrePaciente = nombrePaciente;
        this.nombreConvenio = nombreConvenio;
        this.porcentajeDescuento = porcentajeDescuento;
        this.precioBase = precioBase;
        this.montoDescuento = montoDescuento;
        this.totalAPagar = totalAPagar;
    }

    //GETTERS
    public String getRut() {return rut;}
    public String getNombrePaciente() {return nombrePaciente;}
    public String getNombreConvenio() { return nombreConvenio;}
    public Integer getPorcentajeDescuento() {return porcentajeDescuento;}
    public Integer getPrecioBase() {return precioBase;}
    public Integer getMontoDescuento() {return montoDescuento;}
    public Integer getTotalAPagar() {return totalAPagar;}

    //SETTERS
    public void setRut(String rut) {this.rut = rut;}
    public void setNombrePaciente(String nombrePaciente) {this.nombrePaciente = nombrePaciente;}
    public void setNombreConvenio(String nombreConvenio) {this.nombreConvenio = nombreConvenio;}
    public void setPorcentajeDescuento(Integer porcentajeDescuento) {this.porcentajeDescuento = porcentajeDescuento;}
    public void setPrecioBase(Integer precioBase) {this.precioBase = precioBase;}
    public void setMontoDescuento(Integer montoDescuento) {this.montoDescuento = montoDescuento;}
    public void setTotalAPagar(Integer totalAPagar) {this.totalAPagar = totalAPagar;}
    
}