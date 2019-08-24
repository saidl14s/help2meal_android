package com.itcg.help2meal;

public class Ingrediente {

    public String getUrl_imagen() {
        return url_imagen;
    }

    public void setUrl_imagen(String url_imagen) {
        this.url_imagen = url_imagen;
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

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public int getCaducidad() {
        return caducidad;
    }

    public void setCaducidad(int caducidad) {
        this.caducidad = caducidad;
    }

    public int getClasificacion_id() {
        return clasificacion_id;
    }

    public void setClasificacion_id(int clasificacion_id) {
        this.clasificacion_id = clasificacion_id;
    }

    public Ingrediente(int id, String nombre, String unidad, int caducidad, int clasificacion_id, String url_imagen, int cantidad){
        this.id = id;
        this.nombre = nombre;
        this.unidad = unidad;
        this.caducidad = caducidad;
        this.clasificacion_id = clasificacion_id;
        this.url_imagen = url_imagen;
        this.cantidad = cantidad;
    }

    public int getImageResourceId(){
        return 0;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    private int id;
    private String nombre;
    private String unidad;
    private String url_imagen;
    private int caducidad;
    private int clasificacion_id;
    private int cantidad;
}
