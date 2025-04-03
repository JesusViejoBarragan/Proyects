package com.app.crudapp.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "personaRegistro")
public class PersonaRegistro {

    @Id
    private String id;
    private String usuario;
    private String nombre;
    private String apellidos;
    private String contraseña;
    private String rol;

    // Constructores, getters y setters

    public PersonaRegistro() {}

    public PersonaRegistro(String id, String usuario, String nombre, String apellidos, String contraseña, String rol) {
        this.id = id;
        this.usuario = usuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.contraseña = contraseña;
        this.rol = rol;
    }

    // Getters y Setters

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }
}
