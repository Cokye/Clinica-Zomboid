package cl.clinica.backend_clinica.web.request;

import java.util.List;

public class AfiliacionRequest {
    private String rutTitular;
    private String convenioKey;
    private List<String> beneficiarios;

    private String nombre;
    private String apellido;
    private Integer edad;
    private String correo;
    private String password;

    // Lista de cargas familiares 
    private List<NuevoUsuarioDTO> nuevasCargas;

    public AfiliacionRequest() {}

    public AfiliacionRequest(String rutTitular, String convenioKey, List<String> beneficiarios, 
                             String nombre, String apellido, Integer edad, String correo, 
                             String password, List<NuevoUsuarioDTO> nuevasCargas) {
        this.rutTitular = rutTitular;
        this.convenioKey = convenioKey;
        this.beneficiarios = beneficiarios;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.correo = correo;
        this.password = password;
        this.nuevasCargas = nuevasCargas;
    }

    //GETTERS
    public String getRutTitular() {return rutTitular;}
    public String getConvenioKey() {return convenioKey;}
    public List<String> getBeneficiarios() {return beneficiarios;}
    public String getNombre() {return nombre;}
    public String getApellido() {return apellido;}
    public Integer getEdad() {return edad;}
    public String getCorreo() {return correo;}
    public String getPassword() {return password;}
    public List<NuevoUsuarioDTO> getNuevasCargas() {return nuevasCargas;}


    //SETTERS
    public void setRutTitular(String rutTitular) {this.rutTitular = rutTitular;}
    public void setConvenioKey(String convenioKey) {this.convenioKey = convenioKey;}
    public void setBeneficiarios(List<String> beneficiarios) {this.beneficiarios = beneficiarios;}
    public void setNombre(String nombre) {this.nombre = nombre;}
    public void setApellido(String apellido) {this.apellido = apellido;}
    public void setEdad(Integer edad) {this.edad = edad;}
    public void setCorreo(String correo) {this.correo = correo;}
    public void setPassword(String password) {this.password = password;}
    public void setNuevasCargas(List<NuevoUsuarioDTO> nuevasCargas) {this.nuevasCargas = nuevasCargas;}



    public static class NuevoUsuarioDTO {
        private String rut;
        private String nombre;
        private String apellido;
        private Integer edad;
        private String correo;
        private String password;

        public NuevoUsuarioDTO() {}

        public NuevoUsuarioDTO(String rut, String nombre, String apellido, Integer edad, String correo, String password) {
            this.rut = rut;
            this.nombre = nombre;
            this.apellido = apellido;
            this.edad = edad;
            this.correo = correo;
            this.password = password;
        }

        //GETTERS
        public String getRut() {return rut;}
        public String getNombre() {return nombre;}
        public String getApellido() {return apellido;}
        public Integer getEdad() {return edad;}
        public String getCorreo() {return correo;}
        public String getPassword() {return password;}

        //SETTERS
        public void setRut(String rut) {this.rut = rut;}
        public void setNombre(String nombre) {this.nombre = nombre;}
        public void setApellido(String apellido) {this.apellido = apellido;}
        public void setEdad(Integer edad) {this.edad = edad;}
        public void setCorreo(String correo) {this.correo = correo;}
        public void setPassword(String password) {this.password = password;}
    }
}