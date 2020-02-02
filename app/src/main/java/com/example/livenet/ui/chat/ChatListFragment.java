package com.example.livenet.ui.chat;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.example.livenet.BBDD.DBC;
import com.example.livenet.MainActivity;
import com.example.livenet.R;
import com.example.livenet.model.Chat;
import com.example.livenet.model.FireUser;
import com.example.livenet.ui.Adapter.UsersAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<FireUser> mUsers;
    private UsersAdapter adapter;
    DatabaseReference reference;
    private View root;
    private DBC dbc;
    private String fuserid;
    private String fusername;

    private ArrayList<String> usersList;

    public static ChatListFragment newInstance() {
        return new ChatListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.chat_list_fragment, container, false);

        recyclerView = root.findViewById(R.id.rvChatsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsers = new ArrayList<>();
        usersList = new ArrayList<>();
        fuserid = ((MainActivity)getActivity()).getFireBaseMain().getLogged().getToken();
        fusername = ((MainActivity)getActivity()).getFireBaseMain().getLogged().getAlias();
        usersList = new ArrayList<>();
        dbc = new DBC(root.getContext(), "localCfgBD", null, 1);
        reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    if(chat.getSender().equals(fuserid)){
                        usersList.add(chat.getReceiver());
                    }
                    if(chat.getReceiver().equals(fuserid)){
                        usersList.add(chat.getSender());
                    }

                }

                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return root;
    }

    private void readChats(){
        mUsers = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    FireUser user  = snapshot.getValue(FireUser.class);

                    for (String id: usersList){
                        if(user.getId().equals(id)){
                            if(mUsers.size() != 0){
                                for (FireUser user1 : mUsers){
                                    if(!user.getId().equals(user1.getId())){
                                        user.setImage(dbc.getFotoAmigo(user.getUsername()));
                                        mUsers.add(user);
                                    }
                                }
                            }else{
                                user.setImage(dbc.getFotoAmigo(user.getUsername()));
                                mUsers.add(user);
                            }
                        }
                    }
                }
                adapter = new UsersAdapter(mUsers,root.getContext(), ((MainActivity)getActivity()), fuserid, false, fusername, getFragmentManager());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }



    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
