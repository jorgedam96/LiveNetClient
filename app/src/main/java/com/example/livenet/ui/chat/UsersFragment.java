package com.example.livenet.ui.chat;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.example.livenet.BBDD.DBC;
import com.example.livenet.MainActivity;
import com.example.livenet.R;
import com.example.livenet.REST.APIUtils;
import com.example.livenet.REST.AmigosRest;
import com.example.livenet.model.FireUser;
import com.example.livenet.model.Usuario;
import com.example.livenet.ui.Adapter.UsersAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersFragment extends Fragment {
    private RecyclerView recyclerView;
    private UsersAdapter adapter;
    private ArrayList<FireUser> mUsers;
    private ArrayList<FireUser> mUsersFirebase;
    private DBC dbc;
    private View root;
    private String fuser;
    private String fuserid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_users, container, false);
        recyclerView = root.findViewById(R.id.UsersRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsers = new ArrayList<>();
        readUsers();
        dbc.close();
        fuser = ((MainActivity)getActivity()).getFireBaseMain().getLogged().getAlias();
        fuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return root;
    }

    private void readUsers() {
        dbc = new DBC(getActivity(), "localCfgBD", null, 1);
        mUsers = dbc.seleccionarData();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsersFirebase = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    FireUser fireUser = snapshot.getValue(FireUser.class);
                    for (FireUser user : mUsers){
                        if(fireUser.getId().equals(user.getId())){
                            mUsersFirebase.add(fireUser);
                        }
                    }
                }
                adapter = new UsersAdapter(mUsersFirebase, root.getContext(),
                        (MainActivity)getActivity(),
                        fuserid,
                        true,
                        fuser,
                        getFragmentManager());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
