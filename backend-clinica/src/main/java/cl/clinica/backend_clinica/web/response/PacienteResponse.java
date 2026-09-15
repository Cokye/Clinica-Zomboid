package cl.clinica.backend_clinica.web.response;

public class PacienteResponse {
    private String rut;
    private String nombreCompleto;
    private Integer edad;
    private String correo;

    public PacienteResponse() {}

    public PacienteResponse(String rut, String nombreCompleto, Integer edad, String correo) {
        this.rut = rut;
        this.nombreCompleto = nombreCompleto;
        this.edad = edad;
        this.correo = correo;
    }

    //GETTERS
    public String getRut() {return rut;}
    public String getNombreCompleto() {return nombreCompleto;}
    public Integer getEdad() {return edad;}
    public String getCorreo() {return correo;}


    //SETTERS
    public void setRut(String rut) {this.rut = rut;}
    public void setNombreCompleto(String nombreCompleto) {this.nombreCompleto = nombreCompleto;}
    public void setEdad(Integer edad) {this.edad = edad;}
    public void setCorreo(String correo) {this.correo = correo;}
}