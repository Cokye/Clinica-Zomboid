package cl.clinica.backend_clinica.model;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;

import com.arangodb.serde.jackson.Key;
import com.arangodb.springframework.annotation.ArangoId;
import com.arangodb.springframework.annotation.Document;




@Document("especialidades")
public class Especialidad {
    @ArangoId 
    private String arangoId;

    @Id
    @Key 
    private String id; // _key ("medicina_general", etc.)
    private String nombre;
    private Integer precioBase;
    private List<Map<String, String>> doctores;

    public Especialidad(){}

    public Especialidad(String id, String nombre, Integer precioBase, List<Map<String , String>> doctores){
        this.id = id;
        this.nombre = nombre;
        this.precioBase = precioBase;
        this.doctores = doctores;
    }


    //GETTERS
    public String getArangoId() {return arangoId;}
    public String getId() {return id;}
    public String getNombre() {return nombre;}
    public Integer getPrecioBase() {return precioBase;}
    public List<Map<String, String>> getDoctores() {return doctores;}

    //SETTERS
    public void setArangoId(String arangoId) {this.arangoId = arangoId;}
    public void setId(String id) {this.id = id;}
    public void setNombre(String nombre) {this.nombre = nombre;}
    public void setPrecioBase(Integer precioBase) {this.precioBase = precioBase;}
    public void setDoctores(List<Map<String, String>> doctores) {this.doctores = doctores;}
    

    
}