package cl.clinica.backend_clinica.web.request;

public class NuevoPacienteRequest {
    private String rut;
    private String nombre;
    private String apellido;
    private Integer edad;
    private String correo;
    private String password;

    public NuevoPacienteRequest() {}

    public NuevoPacienteRequest(String rut, String nombre, String apellido, Integer edad, String correo, String password){
        this.rut = rut;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.correo = correo;
        this.password = password;
    }

    //GETTERS
    public String getRut() { return rut; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public Integer getEdad() { return edad; }
    public String getCorreo() { return correo; }
    public String getPassword() { return password;}

    //SETTERS
    public void setRut(String rut) { this.rut = rut; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setEdad(Integer edad) { this.edad = edad; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setPassword(String password) {this.password = password;}
    

    
}
