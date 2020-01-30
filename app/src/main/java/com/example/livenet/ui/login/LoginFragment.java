package com.example.livenet.ui.login;

import android.Manifest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.livenet.LoginActivity;
import com.example.livenet.MainActivity;
import com.example.livenet.R;
import com.example.livenet.REST.APIUtils;
import com.example.livenet.REST.UsuariosRest;
import com.example.livenet.Utilidades;
import com.example.livenet.model.FireUser;
import com.example.livenet.model.LoginBody;
import com.example.livenet.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Build.VERSION.SDK_INT;

public class LoginFragment extends Fragment implements View.OnClickListener, Serializable {


    private View root;
    private EditText usuario;
    private EditText pass;
    private CardView btnEntrar;
    private CardView btnRegistrar;
    private UsuariosRest usuariosRest;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private String fotoUsuario;
    //Chat
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;

    //Loading
    private RelativeLayout loadingLayout;
    private ImageView ivLoading;
    private TextView tvLoading;
    private Animation rotation, intermitente;


    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        checkAndRequestPermissions();

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

        loadingLayout = root.findViewById(R.id.layoutLoadingLogin);
        ivLoading = root.findViewById(R.id.ivLoadingLogin);
        tvLoading = root.findViewById(R.id.tvLogeando);

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
        //Loading
        loadingLayout.setVisibility(View.VISIBLE);
        rotation = AnimationUtils.loadAnimation(getContext(), R.anim.rotation);
        ivLoading.startAnimation(rotation);
        intermitente = AnimationUtils.loadAnimation(getContext(), R.anim.intermitente);
        tvLoading.startAnimation(intermitente);


        //valores de los edit text
        String usrStr = usuario.getText().toString();
        String passStr = pass.getText().toString();
        //consulta si existe

        try {


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


                        } else if (response.code() == 204) {

                            Toast.makeText(root.getContext(), "Contraseña o Usuario incorrecto", Toast.LENGTH_SHORT).show();
                            loadingLayout.setVisibility(View.INVISIBLE);
                        }
                    }

                }


                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    Log.e("ERROR: ", Objects.requireNonNull(t.getMessage()));
                    Toast.makeText(root.getContext(), "No se puede iniciar Sesión", Toast.LENGTH_SHORT).show();
                    loadingLayout.setVisibility(View.INVISIBLE);
                }
            });
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e("comprobarUsuario", e.getMessage());
            }
        }
    }

    private void irApp(Usuario user) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = new Intent((LoginActivity) getActivity(), MainActivity.class);
        Bundle datos = new Bundle();

        datos.putString("alias", user.getAlias());
        datos.putString("correo", user.getCorreo());
        //no poner foto real que revienta
        datos.putString("foto", "defaultphoto");
        fotoUsuario=user.getFoto();
        intent.putExtras(datos);
        startActivity(intent);
        getActivity().finish();

    }


    //Login en firebase con los datos del usuario
    private void loginFirebase(Usuario usuario) {
        auth.signInWithEmailAndPassword(usuario.getCorreo(), usuario.getPasswd()).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {


                        String uid = auth.getCurrentUser().getUid();

                        //Buscamos el usuario en Firebase
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                System.out.println(dataSnapshot.getChildrenCount());
                                FireUser fuser = dataSnapshot.getValue(FireUser.class);
                                try {
                                    //Comprobamos que no esté conectado desde otra localizacion

                                    irApp(usuario);

                                } catch (Exception ex) {
                                    try {
                                        reference.removeEventListener(this);
                                        //Reiniciamos la App para limpiar cualquier residuo en memoria
                                        Intent mStartActivity = new Intent(root.getContext(), LoginActivity.class);
                                        startActivity(mStartActivity);
                                        getActivity().finish();
                                    } catch (Exception e) {
                                        if (e.getMessage() != null) {
                                            Log.e("loginFirebase", e.getMessage());
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });

    }


    private boolean checkAndRequestPermissions() {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            int permissionReadPhoneState = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
            int permissionStorage = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
            int permissionRecord = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            int permissionWrite = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WAKE_LOCK);
            List<String> listPermissionsNeeded = new ArrayList<>();

            if (permissionReadPhoneState != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }

            if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WAKE_LOCK);
            }

            if (permissionRecord != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
                return false;
            } else {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        try {
            String TAG = "LOG_PERMISSION";
            Log.d(TAG, "Permission callback called-------");
            switch (requestCode) {
                case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                    Map<String, Integer> perms = new HashMap<>();
                    // Initialize the map with both permissions
                    perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                    perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                    perms.put(Manifest.permission.ACCESS_BACKGROUND_LOCATION, PackageManager.PERMISSION_GRANTED);
                    perms.put(Manifest.permission.WAKE_LOCK, PackageManager.PERMISSION_GRANTED);
                    // Fill with actual results from user
                    if (grantResults.length > 0) {
                        for (int i = 0; i < permissions.length; i++)
                            perms.put(permissions[i], grantResults[i]);
                        // Check for both permissions

                        if (perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED
                        ) {
                            Log.d(TAG, "Phone state and storage permissions granted");

                        } else {
                            Log.d(TAG, "Some permissions are not granted ask again ");
                            //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                      //shouldShowRequestPermissionRationale will return true
                            //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ||
                                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) ||
                                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) ||
                                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WAKE_LOCK)
                            ) {
                                showDialogOK("Phone state and storage permissions required for this app",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        checkAndRequestPermissions();
                                                        break;
                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        // proceed with logic by disabling the related features or quit the app.
                                                        break;
                                                }
                                            }
                                        });
                            }
                            //permission is denied (and never ask again is  checked)
                            //shouldShowRequestPermissionRationale will return false
                            else {
                                Toast.makeText(getContext(), "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                        .show();
                                //proceed with logic by disabling the related features or quit the app.
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e("requestPermissionLogi", e.getMessage());
            }
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(root.getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }
}