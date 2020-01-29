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

    @SerializedName("token")
    @Expose
    private String token;

    public Usuario() {
    }

    public Usuario(String alias, String passwd) {
        this.alias = alias;
        this.passwd = passwd;
    }

    public Usuario(int id, String alias, String correo, String passwd, String foto, String token) {
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
                ", token='" + token + '\'' +
                '}';
    }

    public String getAlias() {
        return alias;
    }

    public String getCorreo() {
        return correo;
    }

    public String getPasswd() {
        return passwd;
    }

    public String getFoto() {
        return foto;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}