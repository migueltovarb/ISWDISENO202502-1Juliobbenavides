package com.ejemplo.vehiculos.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// Esta clase representa el documento que se va a guardar en MongoDB.
@Document(collection = "vehiculos")
public class Vehiculo {

    @Id
    private String id;

    // Atributos básicos del vehículo
    private String marca;
    private String modelo;
    private Integer anio;
    private Double precio;

    // Constructor vacío (obligatorio para Spring)
    public Vehiculo() {
    }

    // Constructor con parámetros (por si se quiere usar)
    public Vehiculo(String id, String marca, String modelo, Integer anio, Double precio) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.precio = precio;
    }

    // Getters y setters de todos los campos.
    // Nada raro, código normalito de estudiante.

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}
