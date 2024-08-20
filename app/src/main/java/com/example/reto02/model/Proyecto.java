package com.example.reto02.model;

import java.util.HashMap;
import java.util.Map;

public class Proyecto
{ // Atributos de la clase Proyecto
  private String id; // Identificador único del proyecto
  private String name; // Nombre del proyecto
  private String descripcion; // Descripcion del proyecto
  private String fechaini; // Fecha inicial del proyecto
  private String fechafin; // Fecha final del proyecto
  private boolean deleted; // Indica si el proyecto ha sido eliminado

  // Constructor vacío necesario para ciertas integraciones como Firebase
  public Proyecto()
  {
  }
  // Constructor con parámetros para inicializar un producto con un ID, nombre y precio
  public Proyecto(String id, String name, String descripcion, String fechaini, String fechafin)
  { this.id = id; // Asigna el ID proporcionado
    this.name = name; // Asigna el nombre proporcionado
    this.descripcion = descripcion; // Asigna la descripcion proporcionada
    this.fechaini = fechaini; // Asigna la fecha inicial proporcionada
    this.fechafin = fechafin; // Asigna la fecha inicial proporcionada
  }
  // Constructor sin ID, para inicializar un producto solo con nombre y precio
  public Proyecto(String name,  String descripcion, String fechaini, String fechafin)
  { this.name = name; // Asigna el nombre proporcionado
    this.descripcion = descripcion; // Asigna la descripcion proporcionada
    this.fechaini = fechaini; // Asigna la fecha inicial proporcionada
    this.fechafin = fechafin; // Asigna la fecha inicial proporcionada
  }
  // Getters y setters para los atributos
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getDescripcion() {
    return descripcion;
  }
  public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

  public String getFechaIni() {
    return fechaini;
  }
  public void setFechaIni(String fechaini) {this.fechaini = fechaini;}

  public String getFechaFin() {
    return fechafin;
  }
  public void setFechaFin(String fechafin) {
    this.fechafin = fechafin;
  }

  public boolean isDeleted() {
    return deleted;
  } // Establece el estado de eliminación del producto
  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  // Conviertir las propiedades del producto en un mapa para almacenamiento o envío
  public Map<String, Object> toMap()
  { Map<String, Object> result = new HashMap<>();
    result.put("id", id); // Agrega el ID al mapa
    result.put("name", name); // Agrega el nombre al mapa
    result.put("descripcion", descripcion); // Agrega el nombre al mapa
    result.put("fechaini", fechaini); // Agrega el nombre al mapa
    result.put("fechafin", fechafin); // Agrega el nombre al mapa
    return result; // Devuelve el mapa con las propiedades del producto
  }
}