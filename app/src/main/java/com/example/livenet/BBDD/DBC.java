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

import java.util.ArrayList;

public class DBC extends SQLiteOpenHelper {
    private static final String livenetSql = "CREATE TABLE Usuario(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    ALIAS VARCHAR(255) NOT NULL UNIQUE," +
            "    TOKEN VARCHAR(255) NOT NULL UNIQUE," +
            "    LOGGEDIN DATETIME NOT NULL," +
            "    CADUCIDAD DATETIME NOT NULL);";

    private static final String lnAmigosSql ="CREATE TABLE AMIGOS(\n" +
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
            db.update("Amigos", values, "alias=" + amigo[0], null);

        }
        db.close();
    }

    public void update(String[] amigo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        db.update("Amigos", values, "alias=" + amigo[0], null);

    }
}
