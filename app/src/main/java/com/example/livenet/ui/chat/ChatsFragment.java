package com.example.livenet.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.livenet.BBDD.DBC;
import com.example.livenet.R;

public class ChatsFragment extends Fragment {
    private DBC dbc;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_chat, container, false);
        getFragmentManager().beginTransaction().add(R.id.nav_host_fragment, new UsersFragment()).addToBackStack(null).commit();

        return root;
    }


}