package com.example.livenet.ui.login;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.livenet.R;
import com.example.livenet.REST.APIUtils;
import com.example.livenet.REST.UsuariosRest;
import com.example.livenet.Utilidades;

import com.example.livenet.model.Usuario;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

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
    private EditText passcheck;
    private UsuariosRest usuariosRest;

    //Check datos
    private boolean pwd6; //Tiene minimo 6 caracteres
    private boolean pwdequals; //Tanto el checking como la principal coinciden
    private boolean isEmail; //Email valido
    private boolean nombreok; //Nombre ok
    private TextInputLayout passlayout;
    private TextInputLayout passchecklayout;
    private TextInputLayout emaillayout;
    private TextInputLayout nombrelayout;
    private CardView cvRegistrar;

    //Loading
    private RelativeLayout layoutLoading;
    private ImageView ivLoading;
    private Animation rotation;
    private Animation intermitente;
    private TextView registrando;

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
        passcheck = root.findViewById(R.id.etRepPassRegistro);

        nombrelayout = root.findViewById(R.id.tilUsuarioRegistro);
        passlayout = root.findViewById(R.id.tilPassRegistro);
        passchecklayout = root.findViewById(R.id.tilRepPassRegistro);
        emaillayout = root.findViewById(R.id.tilEmailRegistro);

        //Loading
        layoutLoading = root.findViewById(R.id.layoutLoading);
        ivLoading = root.findViewById(R.id.ivLoading);
        registrando = root.findViewById(R.id.tvRegistrando);

        btnRegistrar.setEnabled(false);
        btnRegistrar.setCardBackgroundColor(getResources().getColor(R.color.disabled));
        inputCheck();
    }

    private void asignarListenerBotones() {

        btnVolverALogin.setOnClickListener(this);
        btnRegistrar.setOnClickListener(this);

    }

    private void inputCheck(){
        usuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nickname = usuario.getText().toString();

                if(nickname.length() >= 4){
                    nombreok = true;
                    nombrelayout.setBoxStrokeColor(getResources().getColor(R.color.ok));
                    usuario.setError(null);
                }else{
                    nombreok = false;
                    nombrelayout.setBoxStrokeColor(getResources().getColor(R.color.error));
                    usuario.setError("El nombre debe tener minimo 4 caracteres!");
                }
                checkData();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String mail = email.getText().toString();
                if (mail.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")){
                    isEmail = true;
                    emaillayout.setBoxStrokeColor(getResources().getColor(R.color.ok));
                    email.setError(null);
                }else{
                    isEmail = false;
                    emaillayout.setBoxStrokeColor(getResources().getColor(R.color.error));
                    email.setError("Debe ser un email valido!");
                }
                checkData();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pwd = pass.getText().toString();

                if(pwd.length() >= 6){
                    pwd6 = true;
                    passlayout.setBoxStrokeColor(getResources().getColor(R.color.ok));
                    pass.setError(null);
                }else{
                    pwd6 = false;
                    passlayout.setBoxStrokeColor(getResources().getColor(R.color.error));
                    pass.setError("La contraseña debe tener minimo 6 caracteres");
                }

                checkData();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });

        passcheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pwd = passcheck.getText().toString();

                if(pwd.equals(pass.getText().toString())){
                    pwdequals = true;
                    passchecklayout.setBoxStrokeColor(getResources().getColor(R.color.ok));
                    passcheck.setError(null);
                }else{
                    pwdequals = false;

                    passchecklayout.setBoxStrokeColor(getResources().getColor(R.color.error));
                    passcheck.setError("Las contraseñas deben ser iguales");
                }

                checkData();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void checkData(){
        if(nombreok && isEmail && pwd6 && pwdequals){

            btnRegistrar.setEnabled(true);
            btnRegistrar.setCardBackgroundColor(getResources().getColor(R.color.colorAzulClaro));
        }else{
            btnRegistrar.setEnabled(false);
            btnRegistrar.setCardBackgroundColor(getResources().getColor(R.color.disabled));
        }

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
        layoutLoading.setVisibility(View.INVISIBLE);
        auth.signOut();
        LoginFragment fl = new LoginFragment();
        FragmentManager fragmentManager = getFragmentManager();
        Objects.requireNonNull(fragmentManager)
                .beginTransaction()
                .setCustomAnimations(R.anim.bounce, 0, 0, 0)
                .replace(R.id.containerLogin, fl)
                .commit();
    }

    private void comprobarSiExisteAlias() {
        layoutLoading.setVisibility(View.VISIBLE);
        rotation = AnimationUtils.loadAnimation(getContext(), R.anim.rotation);
        ivLoading.startAnimation(rotation);
        intermitente = AnimationUtils.loadAnimation(getContext(), R.anim.intermitente);
        registrando.startAnimation(intermitente);

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
                        layoutLoading.setVisibility(View.INVISIBLE);
                    } else {
                        //comprobar si ha rellenado los campos y comprobar email
                        //despues insertar
                        registrarFirebase();
                    }
                } else {
                    Toast.makeText(root.getContext(), "No response", Toast.LENGTH_SHORT).show();
                    layoutLoading.setVisibility(View.INVISIBLE);
                }
            }


            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("ERROR: ", Objects.requireNonNull(t.getMessage()));
                Toast.makeText(root.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                layoutLoading.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void registrarUsuario(String token) {

        String usuarioStr = usuario.getText().toString();
        String passStr = pass.getText().toString();
        String emailStr = email.getText().toString();

        final Usuario user = new Usuario(0, usuarioStr, emailStr, passStr, "defaultphoto",token);
        user.setToken(token);
        System.out.println(user.getToken());
        Call<Usuario> call = usuariosRest.create(user);
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
                layoutLoading.setVisibility(View.INVISIBLE);
            }
        });
    }


    //Registro del usuario en firebase
    private void registrarFirebase() {
        auth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString()).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        String userid = firebaseUser.getUid();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", userid);
                        hashMap.put("username", usuario.getText().toString());
                        hashMap.put("image", "defaultphoto");
                        hashMap.put("status","Desconectado");


                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    registrarUsuario(userid);
                                }else{
                                    layoutLoading.setVisibility(View.INVISIBLE);
                                }
                            }


                        });
                    }
                });

    }
}


