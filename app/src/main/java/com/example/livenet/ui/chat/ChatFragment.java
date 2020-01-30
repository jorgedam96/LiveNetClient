package com.example.livenet.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.livenet.BBDD.DBC;
import com.example.livenet.MainActivity;
import com.example.livenet.R;
import com.example.livenet.model.Chat;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChatFragment extends Fragment implements View.OnClickListener {
    private DBC dbc;
    private ChatListFragment chatslist;
    private UsersFragment userslist;
    private ImageButton chats, userlist;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        chats = root.findViewById(R.id.chatIBMessages);
        userlist = root.findViewById(R.id.chatIBUsers);
        chats.setOnClickListener(this);
        userlist.setOnClickListener(this);
        chats.setImageDrawable(getContext().getDrawable(R.drawable.ic_message_blue_pulsed));

        //getFragmentManager().beginTransaction().add(R.id.nav_host_fragment, new UsersFragment()).addToBackStack(null).commit();
        chatslist = new ChatListFragment();
        userslist = new UsersFragment();
        return root;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.chatIBMessages:
                getFragmentManager().popBackStack();
                getFragmentManager().beginTransaction().setCustomAnimations(R.anim.from_left,R.anim.to_right, R.anim.from_right,R.anim.to_left)
                        .replace(R.id.nav_chat_fragment, chatslist).commit();
                chats.setImageDrawable(getContext().getDrawable(R.drawable.ic_message_blue_pulsed));
                userlist.setImageDrawable(getContext().getDrawable(R.drawable.ic_group_black_24dp));
                getFragmentManager().popBackStack();
                break;
            case R.id.chatIBUsers:
                getFragmentManager().popBackStack();
                getFragmentManager().beginTransaction().setCustomAnimations(R.anim.from_right,R.anim.to_left, R.anim.from_left,R.anim.to_right)
                        .replace(R.id.nav_chat_fragment, userslist).commit();
                chats.setImageDrawable(getContext().getDrawable(R.drawable.ic_message_black_24dp));
                userlist.setImageDrawable(getContext().getDrawable(R.drawable.ic_group_blue_pulsed));
                break;

        }
    }
}