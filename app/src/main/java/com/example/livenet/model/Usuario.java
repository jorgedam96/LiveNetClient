package com.example.livenet.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Usuario {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("alias")
    @Expose
    private String alias;


    @SerializedName("correo")
    @Expose
    private String correo;

    @SerializedName("passwd")
    @Expose
    private String passwd;

    @SerializedName("foto")
    @Expose
    private String foto;

    public Usuario() {
    }


}