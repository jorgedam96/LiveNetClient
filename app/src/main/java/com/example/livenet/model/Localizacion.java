package com.example.livenet.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


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
    private String fecha_hora;

    @SerializedName("accuracy")
    @Expose
    private float accuracy;



    public Localizacion(String alias, double latitud, double longitud, String fecha_hora, float accuracy) {
        this.alias = alias;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fecha_hora = fecha_hora;
        this.accuracy = accuracy;
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

    public String getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(String fecha_hora) {
        this.fecha_hora = fecha_hora;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    @Override
    public String toString() {
        return "Localizacion{" +
                "alias='" + alias + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", fecha_hora=" + fecha_hora +
                ", accuracy=" + accuracy +
                '}';
    }
}