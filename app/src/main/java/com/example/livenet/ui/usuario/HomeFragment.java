package com.example.livenet.ui.usuario;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.livenet.LoginActivity;
import com.example.livenet.MainActivity;
import com.example.livenet.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private BottomNavigationView menuBottom;
    private ImageButton logout;
    private ImageButton ivFotoPerfil;
    private Button btnAgregarAmigo;
    private Button btnVerQR;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_usuario, container, false);

        Objects.requireNonNull(((MainActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();
        //Objects.requireNonNull(getActivity()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        menuBottom = Objects.requireNonNull(((MainActivity) getActivity())).getNavView();
        View photoHeader = root.findViewById(R.id.photoHeader);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /* For devices equal or higher than lollipop set the translation above everything else */
            photoHeader.setTranslationZ(6);
            /* Redraw the view to show the translation */
            photoHeader.invalidate();
        }
        logout = root.findViewById(R.id.btLogout);
        logout.setOnClickListener(this);

        ivFotoPerfil = root.findViewById(R.id.ivFotoPerfil);
        btnAgregarAmigo = root.findViewById(R.id.btnAgregarAmigo);
        btnVerQR = root.findViewById(R.id.btnVerQR);
        ivFotoPerfil.setOnClickListener(this);
        btnVerQR.setOnClickListener(this);
        btnAgregarAmigo.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btLogout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.from_left, R.anim.to_right);
                break;
            case R.id.btnAgregarAmigo:
                abrirCamara();
                break;
            case R.id.btnVerQR:
                mostarMiQR();
                break;
            case R.id.ivFotoPerfil:
                cambiarFoto();
                break;

        }
    }

    private void cambiarFoto() {

    }

    private void mostarMiQR() {

    }

    private void abrirCamara() {

    }
}