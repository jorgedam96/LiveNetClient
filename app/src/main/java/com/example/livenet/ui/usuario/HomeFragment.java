package com.example.livenet.ui.usuario;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.livenet.BBDD.DBC;
import com.example.livenet.LoginActivity;
import com.example.livenet.MainActivity;
import com.example.livenet.R;
import com.example.livenet.REST.APIUtils;
import com.example.livenet.REST.AmigosRest;
import com.example.livenet.REST.SesionesRest;
import com.example.livenet.REST.UsuariosRest;
import com.example.livenet.model.Sesion;
import com.example.livenet.model.Usuario;
import com.example.livenet.util.MyB64;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final int GALERIA = 1;
    private BottomNavigationView menuBottom;
    private ImageButton logout;
    private CircleImageView ivFotoPerfil;
    private Button btnAgregarAmigo;
    private Button btnVerQR;
    private TextView tvNombre;
    private Usuario usuarioLogeado;
    private View root;
    private UsuariosRest usuRest;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_usuario, container, false);
        usuRest = APIUtils.getUsuService();

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

        usuarioLogeado = ((MainActivity) getActivity()).getLogged();

        logout = root.findViewById(R.id.btLogout);
        logout.setOnClickListener(this);
        ivFotoPerfil = root.findViewById(R.id.ivFotoPerfil);
        btnAgregarAmigo = root.findViewById(R.id.btnAgregarAmigo);
        btnVerQR = root.findViewById(R.id.btnVerQR);
        tvNombre = root.findViewById(R.id.tvName);
        tvNombre.setText(usuarioLogeado.getAlias());
        ivFotoPerfil.setOnClickListener(this);
        btnVerQR.setOnClickListener(this);
        btnAgregarAmigo.setOnClickListener(this);

        cargarFoto();
        return root;
    }

    private void cargarFoto() {
        try {


            Call<Usuario> call = usuRest.findByAlias(usuarioLogeado.getAlias());

            call.enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    try {
                        if (response.body().getFoto().equals("defaultphoto")) {
                            ivFotoPerfil.setImageResource(R.drawable.defaultphoto);
                        } else{
                            ivFotoPerfil.setImageBitmap(MyB64.base64ToBitmap(response.body().getFoto()));
                    }
                    } catch (Exception e) {
                        if (e.getMessage() != null) {
                            Log.e("cargarFoto", e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            if (e.getMessage() != null)
                Log.e("cargarFoto", e.getMessage());
        }
    }

    private void alertDialog() {
        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(root.getContext(),R.style.AlertDialogStyle);
        dialog.setMessage("Quieres cerrar sesion?");
        dialog.setTitle("Informacion");

        dialog.setPositiveButton("Confirmar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        DBC dbc = new DBC(getContext(),"localCfgBD", null, 1);
                        cerrarSesionRest(dbc.recogerToken().getToken(), dbc);




                    }
                });

        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        androidx.appcompat.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btLogout:
                    alertDialog();
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

    private void cerrarSesionRest(String token, DBC dbc) {
        System.out.println("TOKEN"+token);
        Call<String> call = APIUtils.getSesionesService().borrarSesion(token);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dbc.cerrarSesion();
                Toast.makeText(root.getContext(),"Sesion cerrada", Toast.LENGTH_SHORT).show();
                getActivity().finish();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void cambiarFoto() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALERIA);

    }

    private void mostarMiQR() {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = null;
        try {
            bitmap = barcodeEncoder.encodeBitmap(tvNombre.getText().toString(), BarcodeFormat.QR_CODE, 800, 800);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        //mostrar alert
        incrustarQRAlert(bitmap);
    }

    private void incrustarQRAlert(Bitmap bitmap) {

        LayoutInflater inflater = (LayoutInflater) root.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
        builder.setTitle("Este es tu código QR");
        builder.setMessage("Haz que lo lea un amigo desde el perfil de su App con el botón Agregar Amigo!");
        View v = inflater.inflate(R.layout.img_alert_qr, null);
        ImageView fotoAlert = v.findViewById(R.id.ivAlertQR);
        fotoAlert.setImageBitmap(bitmap);
        builder.setView(v);
        builder.setIcon(R.drawable.ic_exit);
        builder.show();
    }

    private void abrirCamara() {


        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Enfoca un QR para agregar a un amigo!\n\n\n");
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.RESULT_CANCELED) {
            return;
        }

        if (requestCode == GALERIA) {
            if (data != null) {
                // Obtenemos su URI con su dirección temporal
                Uri contentURI = data.getData();
                try {
                    // Obtenemos el bitmap de su almacenamiento externo
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), contentURI);
                    //setea la imagen en el juego y en el image view
                    ivFotoPerfil.setImageBitmap(MyB64.comprimirImagen(bitmap, root.getContext()));
                    //insert en rest
                    usuarioLogeado.setFoto(MyB64.bitmapToBase64(MyB64.comprimirImagen(bitmap, root.getContext())));


                    Call<Usuario> call = usuRest.update(usuarioLogeado.getAlias(), usuarioLogeado);

                    call.enqueue(new Callback<Usuario>() {
                        @Override
                        public void onResponse(Call<Usuario> call, Response<Usuario> response) {

                        }

                        @Override
                        public void onFailure(Call<Usuario> call, Throwable t) {

                        }
                    });

                } catch (IOException e) {
                    if (e.getMessage() != null)
                        Log.e("Foto Galeria", e.getMessage());

                    Toast.makeText(getContext(), "¡Fallo Galeria!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}