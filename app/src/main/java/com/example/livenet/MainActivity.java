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
import com.example.livenet.REST.APIUtils;
import com.example.livenet.REST.AmigosRest;
import com.example.livenet.model.FireUser;
import com.example.livenet.model.Usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navView;
    NavController navController;
    Usuario logged;
    FirebaseUser usuario;
    FirebaseAuth auth;
    DatabaseReference reference;

    //Amigos
    ArrayList<String[]> remota;
    private AmigosRest amigosRest;
    private ArrayList<String[]> mUsers;

    //DB
    private DBC dbc;
    private AmigosRest amigoRest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(this.getSupportActionBar()).hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        dbc = new DBC(getApplicationContext(),"localCfgBD", null,1);
        navView = findViewById(R.id.nav_view);
        //navView.animate().translationY(navView.getHeight());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_user, R.id.navigation_map, R.id.navigation_chat)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        pedirMultiplesPermisos();

        logged = new Usuario();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        logged.setAlias(extras.getString("alias"));
        logged.setCorreo(extras.getString("correo"));
        logged.setFoto(extras.getString("foto"));
        amigosRest = APIUtils.getAmigosService();
        auth = FirebaseAuth.getInstance();
        usuario = auth.getCurrentUser();

        if(usuario != null){
            Toast.makeText(getApplicationContext(),"Logeado", Toast.LENGTH_SHORT).show();
        }

    }


    private void pedirMultiplesPermisos() {
        // Indicamos el permisos y el manejador de eventos de los mismos
        Dexter.withActivity(this)
                .withPermissions(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // ccomprbamos si tenemos los permisos de todos ellos
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "¡Todos los permisos concedidos!", Toast.LENGTH_SHORT).show();
                        }


                        // comprobamos si hay un permiso que no tenemos concedido ya sea temporal o permanentemente
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // abrimos un diálogo a los permisos
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }


                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Existe errores! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
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
    protected void onDestroy() {
        auth.signOut();
        super.onDestroy();
    }

    public void comprobarAmigos() {
        dbc = new DBC(getApplicationContext(), "localCfgBD", null, 1);
        ArrayList<FireUser> local = dbc.seleccionarData();

        AmigosRest amigosRest = APIUtils.getAmigosService();
        Call<ArrayList<String[]>> call = amigosRest.findAllByAlias(this.getLogged().getAlias());
        System.out.println(this.getLogged().getAlias());
        call.enqueue(new Callback<ArrayList<String[]>>() {
            @Override
            public void onResponse(Call<ArrayList<String[]>> call, Response<ArrayList<String[]>> response) {
                if (response.isSuccessful()) {
                    //hay respuesta
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


    public void callFriends() {
        Call<ArrayList<String[]>> call = amigosRest.findAllByAlias(this.getLogged().getAlias());
        call.enqueue(new Callback<ArrayList<String[]>>() {
            @Override
            public void onResponse(Call<ArrayList<String[]>> call, Response<ArrayList<String[]>> response) {
                if (response.isSuccessful()) {
                    //hay respuesta
                    mUsers = response.body();
                    for (String[] user : mUsers) {
                        if (user[1].isEmpty() || user[1].equals("default")) {
                            user[1] = "defaultphoto";
                        }
                        dbc.insert(user);
                        dbc.close();
                    }

                }
            }

            @Override
            public void onFailure(Call<ArrayList<String[]>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "No se ha podido cargar la lista de amigos", Toast.LENGTH_SHORT).show();
                dbc.close();
            }
        });

    }

    public Usuario getLogged(){
        return logged;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Escaneado: " + result.getContents(), Toast.LENGTH_LONG).show();
                insertarAmigo(result.getContents());

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void insertarAmigo(String amigo) {
        Log.e("amigo","insertarAmigo");
        try {
            amigoRest = APIUtils.getAmigosService();

            Call call = amigoRest.agregarAmigo(new String[]{logged.getAlias(), amigo});

            call.enqueue(new Callback<String[]>() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.code() == 200) {
                        Toast.makeText(getApplicationContext(), "Se ha agregado a: " + amigo, Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 204) {
                        Toast.makeText(getApplicationContext(), "No se puede agregar, ya sois amigos", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Parece que no es un usuario de la App", Toast.LENGTH_SHORT).show();
                    }
                    Log.e("amigo","onresponse");


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


    public void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(usuario.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("Conectado");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("Desconectado");
    }
}
