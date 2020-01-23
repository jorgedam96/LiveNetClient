package com.example.livenet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;


public class FragmentLogin extends Fragment implements View.OnClickListener {

    private View root;
    private EditText usuario;
    private EditText pass;
    private CardView btnEntrar;
    private CardView btnRegistrar;


    public FragmentLogin() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_login, container, false);

        asignarElementosLayout();
        asignarListenerBotones();

        return root;
    }


    private void asignarListenerBotones() {

        btnEntrar.setOnClickListener(this);
        btnRegistrar.setOnClickListener(this);

    }

    private void asignarElementosLayout() {
        usuario = root.findViewById(R.id.etUsuarioLogin);
        pass = root.findViewById(R.id.etPassLogin);
        btnEntrar = root.findViewById(R.id.card_viewLoginEntrar);
        btnRegistrar = root.findViewById(R.id.card_viewLoginRegistrar);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.etUsuarioLogin:

                break;
            case R.id.etPassLogin:

                break;
            case R.id.card_viewLoginEntrar:

                break;
            case R.id.card_viewLoginRegistrar:
                //ventana registro

                FragmentRegistro fr = new FragmentRegistro();
                FragmentManager fragmentManager = getFragmentManager();
                Objects.requireNonNull(fragmentManager)
                        .beginTransaction()
                        .setCustomAnimations(R.anim.bounce,0 , 0,0)
                        .replace(R.id.nav_host_fragment, fr)
                        .commit();

                break;


        }
    }
}
