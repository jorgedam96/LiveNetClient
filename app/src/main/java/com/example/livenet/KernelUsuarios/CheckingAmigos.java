package com.example.livenet.KernelUsuarios;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.livenet.BBDD.DBC;
import com.example.livenet.REST.APIUtils;
import com.example.livenet.REST.AmigosRest;
import com.example.livenet.model.FireUser;
import com.example.livenet.model.Usuario;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckingAmigos {

    //Amigos
    ArrayList<String[]> remota;
    private AmigosRest amigosRest;
    private ArrayList<String[]> mUsers;
    private Usuario logged;


    //DBC
    private Context context;
    private DBC dbc;

    //Timer & Loop
    private Timer timer;
    private Handler handler;
    private Runnable runnable;

    public CheckingAmigos(Context context, DBC dbc, Usuario logged){
        this.mUsers = new ArrayList<>();
        this.remota = new ArrayList<>();
        this.amigosRest = APIUtils.getAmigosService();
        this.logged = logged;
        this.context = context;
        this.dbc = dbc;
        this.handler = new Handler();
    }


    public void initComprobaciones(){
        try {
            timer = new Timer();
            TimerTask doAsyncTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                comprobarAmigos();
                                callFriends();
                            }catch(Exception ex){
                                Toast.makeText(context,"No hay conexion", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            };
            timer.schedule(doAsyncTask, 0, 5000);
        } catch (Exception e) {
            if (e.getMessage() != null)
                Log.e("timer", e.getMessage());
        }
    }

    /**
     * Comprobamos si la lista de amigos del server Rest se ha actualizado
     * de ser asi, borrara o agregara a los nuevos usuarios
     */
    public void comprobarAmigos() {
        dbc = new DBC(this.context, "localCfgBD", null, 1);
        ArrayList<FireUser> local = dbc.seleccionarData();

        AmigosRest amigosRest = APIUtils.getAmigosService();
        Call<ArrayList<String[]>> call = amigosRest.findAllByAlias(logged.getAlias());
        System.out.println(logged.getAlias());
        call.enqueue(new Callback<ArrayList<String[]>>() {
            @Override
            public void onResponse(Call<ArrayList<String[]>> call, Response<ArrayList<String[]>> response) {
                if (response.isSuccessful()) {
                    //Hay respuesta, cotejamos la bbdd local con la remota, si en la remota se ha borrado algun
                    //amigo, lo borrara en su local tb
                    remota = response.body();
                    for (FireUser localUser : local) {
                        boolean borrado = true;
                        for (int i = 0; i < remota.size(); i++) {
                            if (localUser.getUsername().equals(remota.get(i)[0])) {
                                borrado = false;
                                i = remota.size();
                            }
                        }
                        if (borrado) {
                            dbc.delete(localUser.getUsername());
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<ArrayList<String[]>> call, Throwable t) {
                dbc.close();
            }
        });


    }


    /**
     * Agregamos los nuevos amigos que encontremos en el servidor
     */
    public void callFriends() {
        Call<ArrayList<String[]>> call = amigosRest.findAllByAlias(logged.getAlias());
        Log.e("AMIGOS", logged.getAlias());
        call.enqueue(new Callback<ArrayList<String[]>>() {
            @Override
            public void onResponse(Call<ArrayList<String[]>> call, Response<ArrayList<String[]>> response) {
                if (response.isSuccessful()) {
                    //hay respuesta
                    mUsers = response.body();
                    for (String[] user : mUsers)
                    {
                        Log.e("AMIGOS","Respuesta: "+response.body());
                        dbc.insert(user);
                        dbc.close();
                    }

                }
            }

            @Override
            public void onFailure(Call<ArrayList<String[]>> call, Throwable t) {
                Toast.makeText(context, "No se ha podido cargar la lista de amigos", Toast.LENGTH_SHORT).show();
                dbc.close();
            }
        });

    }

    public void destroySelf(){
        handler.removeCallbacks(runnable);
        timer.cancel();
        timer = null;
    }

}
