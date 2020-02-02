package com.example.livenet.ui.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.livenet.BBDD.DBC;
import com.example.livenet.MainActivity;
import com.example.livenet.MensajeActivity;
import com.example.livenet.R;
import com.example.livenet.REST.APIUtils;
import com.example.livenet.REST.AmigosRest;
import com.example.livenet.model.Chat;
import com.example.livenet.model.FireUser;
import com.example.livenet.model.LoginBody;
import com.example.livenet.model.Usuario;
import com.example.livenet.ui.chat.ChatFragment;
import com.example.livenet.ui.chat.UsersFragment;
import com.example.livenet.util.MyB64;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private ArrayList<FireUser> list;
    private Context context;
    private MainActivity mainActivity;
    private String localuserid;
    private String localusername;
    private AmigosRest amigosRest;
    private FragmentManager fragmentManager;
    private boolean usuarios;

    //Ultimo mensaje recibido o enviado
    public String UltimoMensaje;

    public UsersAdapter(ArrayList<FireUser> list, Context context, MainActivity mainActivity, String localuserid, boolean usuarios, String localusername, FragmentManager fragmentManager) {
        this.list = list;
        this.context = context;
        this.mainActivity = mainActivity;
        this.localuserid = localuserid;
        this.amigosRest = APIUtils.getAmigosService();
        this.usuarios = usuarios;
        this.fragmentManager = fragmentManager;
        this.localusername = localusername;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listitem = layoutInflater.inflate(R.layout.user_item, parent, false);
        UsersAdapter.ViewHolder viewHolder = new UsersAdapter.ViewHolder(listitem);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final FireUser user = list.get(position);

        //Nombre del usuario
        holder.nombre.setText(user.getUsername());

        //Foto del usuario
        try {
            if (user.getImage().equals("defaultphoto")) {
                holder.photo.setImageDrawable(context.getDrawable(R.drawable.defaultphoto));
            } else {
                holder.photo.setImageBitmap(MyB64.base64ToBitmap(user.getImage()));
            }
        } catch (Exception ex) {
            holder.photo.setImageDrawable(context.getDrawable(R.drawable.defaultphoto));
        }

        //Estado del usuario
        try {

            if(user.getStatus().equals("Conectado")){
                holder.status.setImageResource(R.drawable.ic_connected);
            }else{
                holder.status.setImageResource(R.drawable.ic_disconnected);
            }
        } catch (Exception ignored) {

        }

        if(!usuarios){
            lastMessage(user.getId(), holder.last_msg);
        }else{
            holder.last_msg.setVisibility(View.GONE);
        }


        //Item listener para ir al chat
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MensajeActivity.class);
                intent.putExtra("username", user.getUsername());
                intent.putExtra("localuserid", localuserid);
                context.startActivity(intent);
            }
        });

        //Boton borrar usuario
        if (usuarios) {
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog(user.getUsername(), position);
                }
            });

        } else {
            holder.delete.setVisibility(View.INVISIBLE);
        }


    }

    private void alertDialog(final String nombre, int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context,R.style.AlertDialogStyle);
        dialog.setMessage("Accion irreversible: ¿Quiere borrar a " + nombre + " de tu lista de amigos?");
        dialog.setTitle("Alerta");

        dialog.setPositiveButton("Confirmar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        removeItem(position);
                        borrarAmigo(localusername, nombre, position);

                    }
                });

        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }


    private void borrarAmigo(String local, String amigo, int position) {
        System.out.println("Local " + local + " amigo: " + amigo);
        try {
            this.amigosRest = APIUtils.getAmigosService();

            Call<String> call = this.amigosRest.borraramigo(new String[]{local, amigo});

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.code() == 200) {
                        DBC dbc = new DBC(context,"localCfgBD", null, 1);
                        dbc.delete(amigo);
                        dbc.close();
                        fragmentManager.popBackStackImmediate();
                        mainActivity.actualizarListaAmigos();
                        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.nav_host_fragment)).remove(fragmentManager.findFragmentById(R.id.nav_chat_fragment)).replace(R.id.nav_host_fragment, new ChatFragment("usuarios")).commit();
                        Toast.makeText(context, "Borrando a " + amigo+" , puede tardar unos segundos", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Algo salio mal" + String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                    }
                    Log.e("borraramigo", "onresponse");

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e("Borrar amigo", e.getMessage());
            }
        }
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView photo;
        public TextView nombre;
        public ImageView status;
        public ImageButton delete;
        public TextView last_msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.photo = itemView.findViewById(R.id.UsersPhoto);
            this.nombre = itemView.findViewById(R.id.UsersName);
            this.status = itemView.findViewById(R.id.user_status_img);
            this.delete = itemView.findViewById(R.id.ibDeleteFriend);
            this.last_msg = itemView.findViewById(R.id.last_message);
        }
    }


    private void lastMessage(String userid, TextView last_msg){
        UltimoMensaje = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){
                        UltimoMensaje = chat.getMessage();
                    }

                }

                switch(UltimoMensaje){
                    case "default":
                        last_msg.setText("Sin mensajes");
                        break;
                    default:
                        last_msg.setText(UltimoMensaje);
                        break;

                }

                UltimoMensaje = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
