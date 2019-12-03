package com.example.proyectofinalieee.model;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String uid;
    private String usuario;
    private String correo;
    private String nombre;
    private String area;
    private String tipoUsuario;
    private String foto;
    private Boolean isAdmin;


    public Usuario() {
    }

    public Usuario(String uid, String usuario, String correo, String nombre, String area, String tipoUsuario, String foto, Boolean isAdmin) {
        this.uid = uid;
        this.usuario = usuario;
        this.correo = correo;
        this.nombre = nombre;
        this.area = area;
        this.tipoUsuario = tipoUsuario;
        this.foto = foto;
        this.isAdmin = isAdmin;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }
}
