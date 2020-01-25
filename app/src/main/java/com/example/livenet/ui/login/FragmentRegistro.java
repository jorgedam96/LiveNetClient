package com.example.livenet.ui.login;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.livenet.R;
import com.example.livenet.REST.APIUtils;
import com.example.livenet.REST.UsuariosRest;
import com.example.livenet.Utilidades;
import com.example.livenet.model.LoginBody;
import com.example.livenet.model.Usuario;
import com.example.livenet.ui.login.LoginFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
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


    //Chat
    private FirebaseAuth auth;
    private DatabaseReference reference;


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
        auth = FirebaseAuth.getInstance();

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
        LoginFragment fl = new LoginFragment();
        FragmentManager fragmentManager = getFragmentManager();
        Objects.requireNonNull(fragmentManager)
                .beginTransaction()
                .setCustomAnimations(R.anim.bounce, 0, 0, 0)
                .replace(R.id.containerLogin, fl)
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

        final Usuario user = new Usuario(0, usuarioStr, emailStr, passStr, "");

        Call<Usuario> call = usuariosRest.create(user);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(root.getContext(), "Se ha registrado", Toast.LENGTH_SHORT).show();

                    registrarFirebase(user);
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


    //Registro del usuario en firebase
    private void registrarFirebase(Usuario usuario) {
        auth.createUserWithEmailAndPassword(usuario.getCorreo(), usuario.getPasswd()).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        String userid = firebaseUser.getUid();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                        HashMap<String, String> hashMap = new HashMap<>();

                        hashMap.put("id", userid);
                        hashMap.put("username", usuario.getAlias());
                        hashMap.put("image", usuario.getFoto());


                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                }
                            }
                        });
                    }
                });

    }
}


