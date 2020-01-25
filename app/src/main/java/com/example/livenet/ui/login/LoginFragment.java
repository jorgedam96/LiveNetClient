package com.example.livenet.ui.login;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.livenet.LoginActivity;
import com.example.livenet.MainActivity;
import com.example.livenet.R;
import com.example.livenet.REST.APIUtils;
import com.example.livenet.REST.UsuariosRest;
import com.example.livenet.Utilidades;
import com.example.livenet.model.LoginBody;
import com.example.livenet.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment implements View.OnClickListener{


    private View root;
    private EditText usuario;
    private EditText pass;
    private CardView btnEntrar;
    private CardView btnRegistrar;
    private UsuariosRest usuariosRest;

    //Chat
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;


    public static LoginFragment newInstance() {
        return new LoginFragment();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.login_fragment, container, false);

        asignarElementosLayout();
        asignarListenerBotones();
        auth = FirebaseAuth.getInstance();

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
                .replace(R.id.containerLogin, fr)
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

                    if (response.code() == 200) {
                        //codigo correcto
                        Toast.makeText(root.getContext(), "Login correcto", Toast.LENGTH_SHORT).show();
                        Usuario usr = response.body();
                        usr.setPasswd(passStr);
                        loginFirebase(usr);
                    }else if (response.code() == 204){

                        Toast.makeText(root.getContext(), "Contraseña o Usuario incorrecto", Toast.LENGTH_SHORT).show();
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

    private void irApp(Usuario user) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null) {

            Intent intent = new Intent((LoginActivity) getActivity(), MainActivity.class);
            Bundle datos = new Bundle();
            datos.putString("alias", user.getAlias());
            datos.putString("correo", user.getCorreo());
            datos.putString("foto", user.getFoto());
            intent.putExtras(datos);
            startActivity(intent);
            getActivity().finish();
        }
    }


    //Login en firebase con los datos del usuario
    private void loginFirebase(Usuario usuario){
        auth.signInWithEmailAndPassword(usuario.getCorreo(), usuario.getPasswd()).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        irApp(usuario);
                    }
                });

    }

}
