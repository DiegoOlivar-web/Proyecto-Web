package com.example.Proyecto.model;

public class CartItem {
    // 1. El atributo ahora es String
    private String id; 
    private String nombre;
    private double precio;
    private int cantidad;

    public CartItem() {
    }

    // 2. El constructor ahora recibe un String como primer parámetro
    public CartItem(String id, String nombre, double precio, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    // 3. El Getter devuelve un String
    public String getId() {
        return id;
    }

    // 4. El Setter recibe un String
    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getTotal() {
        return this.precio * this.cantidad;
    }
}