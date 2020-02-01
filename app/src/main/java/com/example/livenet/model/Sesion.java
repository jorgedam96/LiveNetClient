package com.example.livenet.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sesion {

    @SerializedName("id")
    @Expose
    private long id;

    @SerializedName("alias")
    @Expose
    private String alias;

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("loggedin")
    @Expose
    private long loggedin;

    @SerializedName("loggedout")
    @Expose
    private long loggedout;


    public Sesion(){}

    public Sesion(String alias, String token, long loggedin, long loggedout){
        this.alias = alias;
        this.token = token;
        this.loggedin = loggedin;
        this.loggedout = loggedout;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getLoggedin() {
        return loggedin;
    }

    public void setLoggedin(long loggedin) {
        this.loggedin = loggedin;
    }

    public long getLoggedout() {
        return loggedout;
    }

    public void setLoggedout(long loggedout) {
        this.loggedout = loggedout;
    }
}
