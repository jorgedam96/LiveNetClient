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

    public Usuario(String alias, String passwd) {
        this.alias = alias;
        this.passwd = passwd;
    }

    public Usuario(int id, String alias, String correo, String passwd, String foto) {
        this.id = id;
        this.alias = alias;
        this.correo = correo;
        this.passwd = passwd;
        this.foto = foto;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", alias='" + alias + '\'' +
                ", correo='" + correo + '\'' +
                ", passwd='" + passwd + '\'' +
                ", foto='" + foto + '\'' +
                '}';
    }
}