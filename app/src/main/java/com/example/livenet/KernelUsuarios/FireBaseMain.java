package com.example.livenet.KernelUsuarios;

import android.content.Context;

import com.example.livenet.BBDD.DBC;

import com.example.livenet.model.FireUser;
import com.example.livenet.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;


public class FireBaseMain {

    private Context context;
    //Amigos
    private CheckingAmigos checkingAmigos;
    private DBC dbc;

    //FireBase
    private FirebaseUser usuario;
    private FirebaseAuth auth;
    private Usuario logged;
    private DatabaseReference reference;



    public FireBaseMain(Context context, Usuario usuario, DBC dbc) {
        this.context = context;
        this.logged = usuario;
        this.auth = FirebaseAuth.getInstance();
        this.usuario = auth.getCurrentUser();
        this.dbc = dbc;
        this.checkingAmigos = new CheckingAmigos(context, dbc, logged);
        this.checkingAmigos.initComprobaciones();
        status("Conectado");
    }

    public FireBaseMain(Context context){
        this.context = context;
        this.auth = FirebaseAuth.getInstance();
        this.usuario = auth.getCurrentUser();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Usuario getLogged() {
        return logged;
    }

    public void setLogged(Usuario logged) {
        this.logged = logged;
    }

    public DatabaseReference getReference() {
        return reference;
    }

    public void setReference(DatabaseReference reference) {
        this.reference = reference;
    }

    public FirebaseUser getUsuario() {
        return usuario;
    }

    public void setUsuario(FirebaseUser usuario) {
        this.usuario = usuario;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public void status(String status) {
        try {
            reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", status);
            reference.updateChildren(hashMap);
        }catch(Exception ex){
            reference = FirebaseDatabase.getInstance().getReference("Users").child(getLogged().getToken());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", status);
            reference.updateChildren(hashMap);

        }
    }

    public CheckingAmigos getCheckingAmigos() {
        return checkingAmigos;
    }

    public void cerrarSesion(){
        this.checkingAmigos.destroySelf();
        status("Desconectado");
        FirebaseAuth.getInstance().signOut();

    }



}
