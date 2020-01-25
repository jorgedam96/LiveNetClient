package com.example.livenet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.example.livenet.ui.login.LoginFragment;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerLogin, LoginFragment.newInstance())
                    .commitNow();
        }

        irALogin();
    }

    private void irALogin() {

        LoginFragment fl = new LoginFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim, R.anim.nav_default_pop_enter_anim, R.anim.nav_default_pop_exit_anim)
                .replace(R.id.containerLogin, fl)
                .commit();

    }
}
