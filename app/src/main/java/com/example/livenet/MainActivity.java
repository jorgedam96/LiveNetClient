package com.example.livenet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.livenet.model.Usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navView;
    NavController navController;
    Usuario logged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(this.getSupportActionBar()).hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        navView = findViewById(R.id.nav_view);
        //navView.animate().translationY(navView.getHeight());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
         navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        logged = new Usuario();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        logged.setAlias(extras.getString("alias"));
        logged.setCorreo(extras.getString("correo"));
        logged.setFoto(extras.getString("foto"));

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
}
