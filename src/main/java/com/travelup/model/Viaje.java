package com.travelup.model;

public class Viaje {

    private int id;
    private String nombre;
    private String destino;
    private String fechaInicio;
    private String fechaFin;
    private String fotoPortada;
    private String estado;
    private String modo;
    private String ciudadOrigen;
    private int idCreador;

    public Viaje() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }

    public String getFotoPortada() { return fotoPortada; }
    public void setFotoPortada(String fotoPortada) { this.fotoPortada = fotoPortada; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getModo() { return modo; }
    public void setModo(String modo) { this.modo = modo; }

    public String getCiudadOrigen() { return ciudadOrigen; }
    public void setCiudadOrigen(String ciudadOrigen) { this.ciudadOrigen = ciudadOrigen; }

    public int getIdCreador() { return idCreador; }
    public void setIdCreador(int idCreador) { this.idCreador = idCreador; }
}