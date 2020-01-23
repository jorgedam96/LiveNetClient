package com.example.livenet.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;




public class Localizacion {


    @SerializedName("alias")
    @Expose
    private String alias;

    @SerializedName("latitud")
    @Expose
    private float latitud;

    @SerializedName("longitud")
    @Expose
    private float longitud;

    @SerializedName("fecha_hora")
    @Expose
    private float fecha_hora;

    public Localizacion() {
    }

    public Localizacion(String alias, float latitud, float longitud, float fecha_hora) {
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

    public float getLatitud() {
        return latitud;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }

    public float getLongitud() {
        return longitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }

    public float getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(float fecha_hora) {
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