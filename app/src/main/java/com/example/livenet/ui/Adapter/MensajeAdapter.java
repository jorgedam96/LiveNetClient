package com.example.livenet.ui.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.livenet.BBDD.DBC;
import com.example.livenet.R;
import com.example.livenet.model.Chat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;


public class MensajeAdapter  extends RecyclerView.Adapter<MensajeAdapter.ViewHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private ArrayList<Chat> list;
    private Context context;
    private String amigo;
    FirebaseUser fuser;

    public MensajeAdapter(ArrayList<Chat> list, Context context, String amigo){
        this.list = list;
        this.context = context;
        this.amigo = amigo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listitem;
        if(viewType == MSG_TYPE_RIGHT) {
             listitem= layoutInflater.inflate(R.layout.chat_env_right, parent, false);
        }else{
             listitem = layoutInflater.inflate(R.layout.chat_rec_left, parent, false);
        }
        MensajeAdapter.ViewHolder viewHolder = new MensajeAdapter.ViewHolder(listitem);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Chat msg = list.get(position);
        holder.show_message.setText(msg.getMessage());

        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(msg.getSender())){
            holder.sender.setText(R.string.sender_msg);
        }else{
            holder.sender.setText(amigo);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView sender;
        public TextView show_message;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.sender = itemView.findViewById(R.id.tvUserChat);
            this.show_message = itemView.findViewById(R.id.mensaje_chat);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(list.get(position).getSender().equals(fuser.getUid())){

            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }

    }
}
