package com.example.livenet.model;

public class LocalSesion {
    private String alias, correo, passwd, token;


    public LocalSesion() {
    }

    public LocalSesion(String alias, String correo, String passwd, String token) {
        this.alias = alias;
        this.correo = correo;
        this.passwd = passwd;
        this.token = token;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

