package com.example.livenet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;


public class FragmentRegistro extends Fragment implements View.OnClickListener {

    private View root;
    private CardView btnVolverALogin;


    public FragmentRegistro() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_registro, container, false);

        asignarElementosLayout();
        asignarListenerBotones();

        return root;
    }

    private void asignarListenerBotones() {

        btnVolverALogin.setOnClickListener(this);

    }

    private void asignarElementosLayout() {

        btnVolverALogin = root.findViewById(R.id.card_viewRegistroVolver);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.card_viewRegistroVolver:{
                FragmentLogin fl = new FragmentLogin();
                FragmentManager fragmentManager = getFragmentManager();
                Objects.requireNonNull(fragmentManager)
                        .beginTransaction()
                        .setCustomAnimations(R.anim.bounce,0 , 0,0)
                        .replace(R.id.nav_host_fragment, fl)
                        .commit();
                break;
            }
        }
    }
}
