package com.example.livenet.BBDD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.livenet.model.FireUser;
import com.example.livenet.model.LocalSesion;
import com.example.livenet.model.Sesion;
import com.example.livenet.model.Usuario;

import java.util.ArrayList;

public class DBC extends SQLiteOpenHelper {
    private static final String livenetSql = "CREATE TABLE Sesion(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    ALIAS VARCHAR(255) NOT NULL UNIQUE," +
            "    PASSWD VARCHAR(255) NOT NULL," +
            "    CORREO VARCHAR(255) NOT NULL UNIQUE," +
            "    TOKEN VARCHAR(255) NOT NULL UNIQUE);";

    private static final String lnAmigosSql ="CREATE TABLE AMIGOS(" +
            "\n" +
            "    ALIAS VARCHAR(255) NOT NULL PRIMARY KEY,\n" +
            "    FOTO TEXT NOT NULL," +
            "    TOKEN TEXT NOT NULL);";


    public DBC(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(livenetSql);
        db.execSQL(lnAmigosSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Accion de seleccionar datos de la BBDD sin ningun filtro
     *
     */
    public ArrayList<FireUser> seleccionarData() {
        SQLiteDatabase bd = this.getReadableDatabase();

        ArrayList<FireUser> amigos = new ArrayList<>();
        //Si hemos abierto correctamente la base de datos
        if (bd != null) {
            //Seleccionamos todos
            Cursor c = bd.rawQuery("SELECT ALIAS, FOTO, TOKEN FROM AMIGOS", null);
            //Nos aseguramos de que existe al menos un registro
            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    amigos.add(new FireUser(c.getString(2),c.getString(1),c.getString(0)));
                } while (c.moveToNext());
            }
            //Cerramos la base de datos
            c.close();


        }
        bd.close();
        return amigos;
    }

    public FireUser selectByToken(String token) {
        SQLiteDatabase bd = this.getReadableDatabase();

        FireUser amigo = new FireUser();
        //Si hemos abierto correctamente la base de datos
        if (bd != null) {
            //Seleccionamos todos
            Cursor c = bd.rawQuery("SELECT ALIAS, FOTO, TOKEN FROM AMIGOS WHERE TOKEN='"+token+"'", null);
            //Nos aseguramos de que existe al menos un registro
            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    amigo = new FireUser(c.getString(2),c.getString(1),c.getString(0));
                } while (c.moveToNext());
            }
            //Cerramos la base de datos
            c.close();


        }
        bd.close();
        return amigo;
    }

    public String getTokenAmigo(String amigo){

        SQLiteDatabase bd = this.getReadableDatabase();

        String token = "";
        //Si hemos abierto correctamente la base de datos
        if (bd != null) {
            //Seleccionamos todos
            Cursor c = bd.rawQuery("SELECT TOKEN FROM AMIGOS WHERE ALIAS='"+amigo+"'", null);
            //Nos aseguramos de que existe al menos un registro
            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    token = c.getString(0);
                    } while (c.moveToNext());
            }
            //Cerramos la base de datos
            c.close();


        }
        bd.close();
        return token;
    }

    public String getFotoAmigo(String amigo){

        SQLiteDatabase bd = this.getReadableDatabase();

        String foto = "";
        //Si hemos abierto correctamente la base de datos
        if (bd != null) {
            //Seleccionamos todos
            Cursor c = bd.rawQuery("SELECT FOTO FROM AMIGOS WHERE ALIAS='"+amigo+"'", null);
            //Nos aseguramos de que existe al menos un registro
            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    foto = c.getString(0);
                } while (c.moveToNext());
            }
            //Cerramos la base de datos
            c.close();


        }
        bd.close();
        return foto;
    }

    public void delete(String alias){
        SQLiteDatabase bd = this.getWritableDatabase();

        bd.delete("AMIGOS", "ALIAS"+"=?", new String[]{alias});

        bd.close();
    }

    public void insert(String[] amigo){


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("alias", amigo[0]);
        values.put("foto", amigo[1]);
        values.put("token", amigo[2]);

        try {
            db.insert("Amigos", null, values);
        }catch(SQLiteException ex){
            

        }
        db.close();
    }

    public void insertToken(Usuario usuario){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM SESION");

        ContentValues values = new ContentValues();
        values.put("alias", usuario.getAlias());
        values.put("passwd", usuario.getPasswd());
        values.put("correo", usuario.getCorreo());
        values.put("token", usuario.getToken());

        db.insert("Sesion", null, values);
        db.close();
    }

    public LocalSesion recogerToken(){
        SQLiteDatabase db = this.getReadableDatabase();

        LocalSesion sesion = new LocalSesion();
        //Si hemos abierto correctamente la base de datos
        if (db != null) {
            //Seleccionamos todos
            Cursor c = db.rawQuery("SELECT ALIAS, PASSWD, CORREO, TOKEN FROM SESION", null);
            //Nos aseguramos de que existe al menos un registro
            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    sesion = new LocalSesion(c.getString(0), c.getString(2), c.getString(1), c.getString(3));
                } while (c.moveToNext());
            }
            //Cerramos la base de datos
            c.close();


        }
        db.close();


        return sesion;
    }

    public void cerrarSesion(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM SESION");
        db.close();
        this.close();
    }



}
