package com.itcg.help2meal;

public class CategoriaSpinner {

    private int id;
    private String nombre;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public CategoriaSpinner(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}
