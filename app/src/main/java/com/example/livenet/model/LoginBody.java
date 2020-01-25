package com.example.livenet.model;

import com.google.gson.annotations.SerializedName;

public class LoginBody {
    @SerializedName("alias")
    private String alias;
    private String pass;

    public LoginBody(String alias, String pass) {
        this.alias = alias;
        this.pass = pass;
    }

    public String getalias() {
        return alias;
    }

    public void setalias(String alias) {
        this.alias = alias;
    }

    public String getpass() {
        return pass;
    }

    public void setpass(String pass) {
        this.pass = pass;
    }
}