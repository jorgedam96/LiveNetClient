package com.example.livenet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.livenet.REST.APIUtils;
import com.example.livenet.REST.UsuariosRest;
import com.example.livenet.model.LoginBody;
import com.example.livenet.model.Usuario;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentRegistro extends Fragment implements View.OnClickListener {

    private View root;
    private CardView btnVolverALogin;
    private CardView btnRegistrar;
    private EditText usuario;
    private EditText pass;
    private EditText email;
    private UsuariosRest usuariosRest;


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


        if (Utilidades.hayConexion(getActivity())) {
            usuariosRest = APIUtils.getUsuService();
        } else {
            Toast.makeText(root.getContext(), "No hay internet", Toast.LENGTH_SHORT).show();
        }
        return root;
    }


    private void asignarElementosLayout() {

        btnVolverALogin = root.findViewById(R.id.card_viewRegistroVolver);
        btnRegistrar = root.findViewById(R.id.card_viewRegistrarRegistro);
        usuario = root.findViewById(R.id.etUsuarioRegistro);
        pass = root.findViewById(R.id.etPassRegistro);
        email = root.findViewById(R.id.etEmailRegistro);

    }

    private void asignarListenerBotones() {

        btnVolverALogin.setOnClickListener(this);
        btnRegistrar.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_viewRegistrarRegistro: {

                comprobarSiExisteAlias();

                break;
            }
            case R.id.card_viewRegistroVolver: {

                irALogin();

                break;
            }
        }
    }

    private void irALogin() {
        FragmentLogin fl = new FragmentLogin();
        FragmentManager fragmentManager = getFragmentManager();
        Objects.requireNonNull(fragmentManager)
                .beginTransaction()
                .setCustomAnimations(R.anim.bounce, 0, 0, 0)
                .replace(R.id.nav_host_fragment, fl)
                .commit();
    }

    private void comprobarSiExisteAlias() {

        String usrStr = usuario.getText().toString();
        //consulta si existe

        Call<Usuario> loginCall = usuariosRest.findByAlias(usrStr);
        loginCall.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    //hay respuesta
                    Toast.makeText(root.getContext(), String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                    if (response.code() == 200) {
                        Toast.makeText(root.getContext(), "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show();
                    } else {
                        //comprobar si ha rellenado los campos y comprobar email
                        //despues insertar
                        registrarUsuario();
                    }
                } else {
                    Toast.makeText(root.getContext(), "No response", Toast.LENGTH_SHORT).show();

                }
            }


            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("ERROR: ", Objects.requireNonNull(t.getMessage()));
                Toast.makeText(root.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void registrarUsuario() {

        String usuarioStr = usuario.getText().toString();
        String passStr = pass.getText().toString();
        String emailStr = email.getText().toString();

        Call<Usuario> call = usuariosRest.create(new Usuario(0, usuarioStr, emailStr, passStr, ""));
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(root.getContext(), "Se ha registrado", Toast.LENGTH_SHORT).show();
                    irALogin();
                } else {
                    Toast.makeText(root.getContext(), "error", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

            }
        });
    }
}
