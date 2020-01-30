package com.example.livenet.ui.usuario;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
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

import com.example.livenet.LoginActivity;
import com.example.livenet.MainActivity;
import com.example.livenet.R;
import com.example.livenet.REST.APIUtils;
import com.example.livenet.REST.AmigosRest;
import com.example.livenet.model.Localizacion;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private BottomNavigationView menuBottom;
    private ImageButton logout;
    private CircleImageView ivFotoPerfil;
    private Button btnAgregarAmigo;
    private Button btnVerQR;
    private TextView tvNombre;
    private AmigosRest amigoRest;
    private String usuarioLogeado;
    private View root;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_usuario, container, false);

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

        usuarioLogeado = ((MainActivity) getActivity()).getLogged().getAlias();

        logout = root.findViewById(R.id.btLogout);
        logout.setOnClickListener(this);

        ivFotoPerfil = root.findViewById(R.id.ivFotoPerfil);
        btnAgregarAmigo = root.findViewById(R.id.btnAgregarAmigo);
        btnVerQR = root.findViewById(R.id.btnVerQR);
        tvNombre = root.findViewById(R.id.tvName);
        tvNombre.setText(usuarioLogeado);
        ivFotoPerfil.setOnClickListener(this);
        btnVerQR.setOnClickListener(this);
        btnAgregarAmigo.setOnClickListener(this);


        ((MainActivity) getActivity()).comprobarAmigos();
        ((MainActivity) getActivity()).callFriends();


        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btLogout:
                ((MainActivity)getActivity()).status("Desconectado");
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();

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

    private void reiniciarApp(){

    }

    private void cambiarFoto() {

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



}