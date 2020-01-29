package com.example.livenet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.example.livenet.BBDD.DBC;
import com.example.livenet.REST.APIUtils;
import com.example.livenet.REST.AmigosRest;
import com.example.livenet.model.FireUser;
import com.example.livenet.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MensajeActivity extends AppCompatActivity {

    CircleImageView foto;
    FirebaseUser fuser;
    DatabaseReference reference;
    TextView username;
    ArrayList<String[]> remota;
    private DBC dbc;
    String usern;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensaje);


        Toolbar toolbar = findViewById(R.id.MensajeToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        foto = findViewById(R.id.mensaje_foto);
        username = findViewById(R.id.mensajes_username);
        intent = getIntent();
        usern = intent.getStringExtra("username");
        username.setText(usern);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        rellenarChat();
    }



    private void rellenarChat(){
        dbc = new DBC(getApplicationContext(),"localCfgBD", null, 1);


        reference = FirebaseDatabase.getInstance().getReference("Users").child(dbc.getTokenAmigo(usern));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getChildrenCount());
                FireUser fuser = dataSnapshot.getValue(FireUser.class);

                if (fuser.getImage().equals("defaultphoto")) {
                    foto.setImageResource(R.drawable.defaultphoto);
                } else {
                    System.out.println("Otra foto rara");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
