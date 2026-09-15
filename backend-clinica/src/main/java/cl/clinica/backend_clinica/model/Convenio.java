package cl.clinica.backend_clinica.model;

import com.arangodb.serde.jackson.Id;
import com.arangodb.serde.jackson.Key;
import com.arangodb.springframework.annotation.ArangoId;
import com.arangodb.springframework.annotation.Document;

@Document("convenios")
public class Convenio {

    @ArangoId
    private String arangoId;

    @Id 
    @Key
    private String key; 

    private String nombre;
    private String descripcion;
    private Integer ahorro;

    public Convenio() {}

    public Convenio(String key, String nombre, String descripcion, Integer ahorro) {
        this.key = key;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ahorro = ahorro;
    }


    //GETTERS
    public String getArangoId() {return arangoId;}
    public String getKey() {
        if (key != null && !key.isBlank()) {
            return key;
        }
        if (arangoId != null && arangoId.contains("/")) {
            return arangoId.substring(arangoId.lastIndexOf("/") + 1);
        }
        return key;
    }
    public String getNombre() {return nombre;}
    public String getDescripcion() { return descripcion;}
    public Integer getAhorro() {return ahorro;}

    //SETTERS
    public void setArangoId(String arangoId) {this.arangoId = arangoId;}
    public void setKey(String key) {this.key = key;}
    public void setNombre(String nombre) {this.nombre = nombre;}
    public void setDescripcion(String descripcion) { this.descripcion = descripcion;}
    public void setAhorro(Integer ahorro) {this.ahorro = ahorro;}
}
