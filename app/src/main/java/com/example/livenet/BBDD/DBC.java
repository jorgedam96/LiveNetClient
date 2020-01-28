package com.example.livenet.BBDD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBC extends SQLiteOpenHelper {
    private static final String livenetSql = "CREATE TABLE Usuario(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    ALIAS VARCHAR(255) NOT NULL UNIQUE," +
            "    TOKEN VARCHAR(255) NOT NULL UNIQUE," +
            "    LOGGEDIN DATETIME NOT NULL," +
            "    CADUCIDAD DATETIME NOT NULL);";

    private static final String lnAmigosSql ="CREATE TABLE AMIGOS(\n" +
            "    ALIAS VARCHAR(255) NOT NULL,\n" +
            "    FOTO TEXT NOT NULL);";


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
    public ArrayList<String[]> seleccionarData() {
        SQLiteDatabase bd = this.getReadableDatabase();

        ArrayList<String[]> amigos = new ArrayList<>();
        //Si hemos abierto correctamente la base de datos
        if (bd != null) {
            //Seleccionamos todos
            Cursor c = bd.rawQuery(" SELECT ALIAS, FOTO FROM AMIGOS", null);
            //Nos aseguramos de que existe al menos un registro
            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    amigos.add(new String[]{c.getString(1),c.getString(2)});
                } while (c.moveToNext());
            }
            //Cerramos la base de datos
            c.close();


        }
        bd.close();
        return amigos;
    }

    public void delete(int id){
        SQLiteDatabase bd = this.getReadableDatabase();

        bd.delete("Juego", "id=" + id, null);

        bd.close();
    }

    public void insert(String[] amigo){


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        db.insert("Amigos", null, values);

    }

    public void update(String[] amigo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        db.update("Amigos", values, "alias=" + amigo[0], null);

    }
}
