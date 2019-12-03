package com.example.proyectofinalieee.model;

import java.util.HashMap;

public class    Comentario {

    private String id;
    private String contenido;
    private HashMap<String,String> likes;

    public Comentario(){

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public HashMap<String, String> getLikes() {
        return likes;
    }

    public void setLikes(HashMap<String, String> likes) {
        this.likes = likes;
    }
}
