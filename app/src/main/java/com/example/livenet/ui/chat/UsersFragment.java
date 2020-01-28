package com.example.livenet.ui.chat;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.livenet.BBDD.DBC;
import com.example.livenet.R;
import com.example.livenet.model.Usuario;
import com.example.livenet.ui.Adapter.UsersAdapter;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {
    private RecyclerView recyclerView;
    private UsersAdapter adapter;
    private ArrayList<String[]> mUsers;
    private DBC dbc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        recyclerView = view.findViewById(R.id.UsersRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsers = new ArrayList<>();


        readUsers();



        return view;
    }

    private void readUsers() {
        DBC db = new DBC(getContext(), "localCfgBD", null, 1);
        mUsers = db.seleccionarData();
        adapter = new UsersAdapter(mUsers, getContext());
        recyclerView.setAdapter(adapter);
        
    }

}
