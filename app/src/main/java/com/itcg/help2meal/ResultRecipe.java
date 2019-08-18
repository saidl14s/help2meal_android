package com.itcg.help2meal;

public class ResultRecipe {
    private String url_image;
    private int id;
    private String nombre;
    private String descripcion;

    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ResultRecipe(int id, String url_image, String nombre, String descripcion) {
        this.url_image = url_image;
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }


}
