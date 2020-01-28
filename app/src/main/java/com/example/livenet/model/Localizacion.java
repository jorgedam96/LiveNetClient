package com.example.livenet.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;


public class Localizacion {


    @SerializedName("alias")
    @Expose
    private String alias;

    @SerializedName("latitud")
    @Expose
    private double latitud;

    @SerializedName("longitud")
    @Expose
    private double longitud;

    @SerializedName("fecha_hora")
    @Expose
    private Date fecha_hora;



    public Localizacion(String alias, double latitud, double longitud, Date fecha_hora) {
        this.alias = alias;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fecha_hora = fecha_hora;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public Date getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(Date fecha_hora) {
        this.fecha_hora = fecha_hora;
    }


    @Override
    public String toString() {
        return "Localizacion{" +
                "alias='" + alias + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", fecha_hora=" + fecha_hora +
                '}';
    }
}