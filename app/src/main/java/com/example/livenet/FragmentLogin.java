package com.example.livenet;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.livenet.REST.APIUtils;
import com.example.livenet.REST.UsuariosRest;
import com.example.livenet.model.Localizacion;
import com.example.livenet.model.LoginBody;
import com.example.livenet.model.Usuario;
import com.example.livenet.ui.home.HomeFragment;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentLogin extends Fragment implements View.OnClickListener {

    private View root;
    private EditText usuario;
    private EditText pass;
    private CardView btnEntrar;
    private CardView btnRegistrar;
    private UsuariosRest usuariosRest;

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


        if (Utilidades.hayConexion(getActivity())) {
            usuariosRest = APIUtils.getUsuService();
        } else {
            Toast.makeText(root.getContext(), "No hay internet", Toast.LENGTH_SHORT).show();
        }

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
                comprobarUsuario();
                break;
            case R.id.card_viewLoginRegistrar:
                //ventana registro

                irARegistro();

                break;


        }
    }

    private void irARegistro() {
        FragmentRegistro fr = new FragmentRegistro();
        FragmentManager fragmentManager = getFragmentManager();
        Objects.requireNonNull(fragmentManager)
                .beginTransaction()
                .setCustomAnimations(R.anim.bounce, 0, 0, 0)
                .replace(R.id.nav_host_fragment, fr)
                .commit();
    }

    private void comprobarUsuario() {
        //valores de los edit text
        String usrStr = usuario.getText().toString();
        String passStr = pass.getText().toString();
        //consulta si existe

        Call<Usuario> call = usuariosRest.login(new LoginBody(usrStr, passStr));
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    //hay respuesta
                    Toast.makeText(root.getContext(), String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                    if (response.code() == 200) {
                        //codigo correcto
                        Toast.makeText(root.getContext(), "Login correcto", Toast.LENGTH_SHORT).show();
                        irApp();
                    }
                }
            }


            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("ERROR: ", Objects.requireNonNull(t.getMessage()));
                Toast.makeText(root.getContext(), "No se puede iniciar Sesión", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void irApp() {
        HomeFragment fr = new HomeFragment();
        FragmentManager fragmentManager = getFragmentManager();
        Objects.requireNonNull(fragmentManager)
                .beginTransaction()
                .setCustomAnimations(R.anim.bounce, 0, 0, 0)
                .replace(R.id.nav_host_fragment, fr)
                .commit();
    }


}
