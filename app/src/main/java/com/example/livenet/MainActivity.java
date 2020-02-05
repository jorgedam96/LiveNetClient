package com.example.livenet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.livenet.BBDD.DBC;
import com.example.livenet.KernelUsuarios.FireBaseMain;
import com.example.livenet.REST.APIUtils;
import com.example.livenet.REST.AmigosRest;
import com.example.livenet.model.FireUser;
import com.example.livenet.model.Usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navView;
    private NavController navController;
    private Usuario logged;
    private DatabaseReference reference;



    //DB
    private DBC dbc;
    private AmigosRest amigoRest;

    //FireBase
    private FireBaseMain fireBaseMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(this.getSupportActionBar()).hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        dbc = new DBC(getApplicationContext(), "localCfgBD", null, 1);
        navView = findViewById(R.id.nav_view);
        //navView.animate().translationY(navView.getHeight());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_user, R.id.navigation_map, R.id.navigation_chat)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        logged = new Usuario();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        logged.setAlias(extras.getString("alias"));
        logged.setCorreo(extras.getString("correo"));
        logged.setToken(extras.getString("token"));


        fireBaseMain = new FireBaseMain(getApplicationContext(),logged, dbc);

        if (fireBaseMain.getUsuario() != null) {
            Toast.makeText(getApplicationContext(), "Logeado", Toast.LENGTH_SHORT).show();
        }

    }

    private void hideBottomNavigationView(BottomNavigationView view) {
        view.clearAnimation();
        view.animate().translationY(view.getHeight());
    }

    public void showBottomNavigationView(BottomNavigationView view) {
        view.clearAnimation();
        view.animate().translationY(0).setDuration(300);
    }

    public BottomNavigationView getNavView() {
        return navView;
    }

    public void setNavView(BottomNavigationView navView) {
        this.navView = navView;
    }

    @Override
    protected void onResume() {
        fireBaseMain.status("Conectado");
        super.onResume();
    }

    @Override
    protected void onPause() {
        fireBaseMain.status("Desconectado");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        fireBaseMain.cerrarSesion();
        super.onDestroy();
    }

    public Usuario getLogged() {
        return logged;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Escaneado: " + result.getContents(), Toast.LENGTH_LONG).show();
                insertarAmigo(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void actualizarListaAmigos(){
        fireBaseMain.getCheckingAmigos().comprobarAmigos();
        fireBaseMain.getCheckingAmigos().callFriends();
    }

    /**
     * Usando el alias del usuario, agregamos al nuevo usuario a nuestra lista de amigos
     * @param amigo alias
     */
    private void insertarAmigo(String amigo) {
        Log.e("amigo", "insertarAmigo");
        try {
            amigoRest = APIUtils.getAmigosService();

            Call call = amigoRest.agregarAmigo(new String[]{logged.getAlias(), amigo});

            call.enqueue(new Callback<String[]>() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.code() == 200) {
                        Toast.makeText(getApplicationContext(), "Se ha agregado a: " + amigo, Toast.LENGTH_SHORT).show();
                        actualizarListaAmigos();
                    } else if (response.code() == 204) {
                        Toast.makeText(getApplicationContext(), "No se puede agregar, ya sois amigos", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Parece que no es un usuario de la App", Toast.LENGTH_SHORT).show();
                    }
                    Log.e("amigo", "onresponse");


                }

                @Override
                public void onFailure(Call call, Throwable t) {

                }
            });
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e("Agregar amigo", e.getMessage());
            }
        }
    }

    public FireBaseMain getFireBaseMain() {
        return fireBaseMain;
    }
}
