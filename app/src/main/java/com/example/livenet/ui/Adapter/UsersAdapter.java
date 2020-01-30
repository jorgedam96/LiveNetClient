package com.example.livenet.ui.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.livenet.MensajeActivity;
import com.example.livenet.R;
import com.example.livenet.REST.APIUtils;
import com.example.livenet.REST.AmigosRest;
import com.example.livenet.model.FireUser;
import com.example.livenet.model.Usuario;
import com.example.livenet.util.MyB64;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter  extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{

    private ArrayList<FireUser> list;
    private Context context;
    private FragmentManager fragmentManager;
    private String localuserid;
    private AmigosRest amigosRest;

    public UsersAdapter(ArrayList<FireUser> list, Context context, FragmentManager fragmentManager, String localuserid){
        this.list = list;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.localuserid = localuserid;
        this.amigosRest = APIUtils.getAmigosService();
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
        boolean pulsado = false;
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


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



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
