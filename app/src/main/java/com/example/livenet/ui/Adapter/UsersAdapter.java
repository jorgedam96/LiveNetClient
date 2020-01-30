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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.livenet.MensajeActivity;
import com.example.livenet.R;
import com.example.livenet.REST.APIUtils;
import com.example.livenet.REST.AmigosRest;
import com.example.livenet.model.FireUser;
import com.example.livenet.model.LoginBody;
import com.example.livenet.model.Usuario;
import com.example.livenet.util.MyB64;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersAdapter  extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{

    private ArrayList<FireUser> list;
    private Context context;
    private FragmentManager fragmentManager;
    private String localuserid;
    private String localusername;
    private AmigosRest amigosRest;
    private boolean usuarios;

    public UsersAdapter(ArrayList<FireUser> list, Context context, FragmentManager fragmentManager, String localuserid, boolean usuarios, String localusername){
        this.list = list;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.localuserid = localuserid;
        this.amigosRest = APIUtils.getAmigosService();
        this.usuarios = usuarios;
        this.localusername = localusername;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listitem = layoutInflater.inflate(R.layout.user_item, parent ,false);
        UsersAdapter.ViewHolder viewHolder = new UsersAdapter.ViewHolder(listitem);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final FireUser user = list.get(position);

        try {
            if (user.getImage().equals("defaultphoto")) {
                holder.photo.setImageDrawable(context.getDrawable(R.drawable.defaultphoto));
            } else {
                holder.photo.setImageBitmap(MyB64.base64ToBitmap(user.getImage()));
            }
        }catch(Exception ex){
            holder.photo.setImageDrawable(context.getDrawable(R.drawable.defaultphoto));
        }
        holder.nombre.setText(user.getUsername());
        try {
            holder.status.setText(user.getStatus());
        }catch(Exception ignored){
            holder.status.setText("");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MensajeActivity.class);
                intent.putExtra("username", user.getUsername());
                intent.putExtra("localuserid",localuserid);
                context.startActivity(intent);
            }
        });

        if(usuarios) {

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog(user.getUsername());
                }
            });

        }else{
            holder.delete.setVisibility(View.INVISIBLE);
        }


    }
    private void alertDialog(final String nombre) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage("Accion irreversible: ¿Quiere borrar a "+nombre+" de tu lista de amigos?");
        dialog.setTitle("Alerta");

        dialog.setPositiveButton("Confirmar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        borrarAmigo(localusername, nombre);

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


    private void borrarAmigo(String local, String amigo){
        System.out.println("Local "+local+" amigo: "+amigo);
        try {
            this.amigosRest = APIUtils.getAmigosService();

            Call call = this.amigosRest.borraramigo(new String[]{local,amigo});

            call.enqueue(new Callback<String[]>() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.code() == 200) {
                        Toast.makeText(context, "Se ha borrado a: " + amigo, Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 204) {
                        Toast.makeText(context, "Algo salio mal", Toast.LENGTH_SHORT).show();
                    }
                    Log.e("borraramigo", "onresponse");

                    
                }

                @Override
                public void onFailure(Call call, Throwable t) {

                }
            });
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e("Borrar amigo", e.getMessage());
            }
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView photo;
        public TextView nombre;
        public TextView status;
        public ImageButton delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.photo = itemView.findViewById(R.id.UsersPhoto);
            this.nombre = itemView.findViewById(R.id.UsersName);
            this.status = itemView.findViewById(R.id.UsersStatus);
            this.delete = itemView.findViewById(R.id.ibDeleteFriend);
        }
    }
}
