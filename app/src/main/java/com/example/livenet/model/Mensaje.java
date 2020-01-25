package com.example.livenet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mensaje {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("remitente")
    @Expose
    private String remitente;

    @SerializedName("destino")
    @Expose
    private String destino;

    @SerializedName("mensaje")
    @Expose
    private String contenido;

    @SerializedName("fecha_hora")
    @Expose
    private String fecha_hora;

    public Mensaje() {
    }

    public Mensaje(String remitente, String destino, String contenido, String fecha_hora){
        this.remitente = remitente;
        this.destino = destino;
        this.contenido = contenido;
        this.fecha_hora = fecha_hora;
    }


    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(String fecha_hora) {
        this.fecha_hora = fecha_hora;
    }

    @Override
    public String toString() {
        return "Mensaje{" +
                "remitente='" + remitente + '\'' +
                ", destino='" + destino + '\'' +
                ", contenido='" + contenido + '\'' +
                ", fecha_hora='" + fecha_hora + '\'' +
                '}';
    }
}
