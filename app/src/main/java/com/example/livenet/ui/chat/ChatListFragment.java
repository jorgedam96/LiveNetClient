package com.example.livenet.ui.chat;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.livenet.BBDD.DBC;
import com.example.livenet.MainActivity;
import com.example.livenet.R;
import com.example.livenet.model.Chat;
import com.example.livenet.model.FireUser;
import com.example.livenet.ui.Adapter.UsersAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    FirebaseUser fuser;
    DatabaseReference reference;

    private ArrayList<String> usersList;

    public static ChatListFragment newInstance() {
        return new ChatListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_list_fragment, container, false);
        ((MainActivity) getActivity()).comprobarAmigos();
        ((MainActivity) getActivity()).callFriends();

        recyclerView = view.findViewById(R.id.rvChatsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsers = new ArrayList<>();
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");

        ValueEventListener listado = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    try {
                        if (chat.getSender().equals(fuser.getUid())) {
                            if (!usersList.contains(chat.getReceiver())) {
                                usersList.add(chat.getReceiver());
                            }

                        }
                        if (chat.getReceiver().equals(fuser.getUid())) {
                            if (!usersList.contains(chat.getSender())) {
                                usersList.add(chat.getSender());
                            }
                        }
                    }catch(NullPointerException ex){

                    }

                }

                readChats();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.addValueEventListener(listado);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }

    private void readChats() {
        mUsers.clear();
        String[] tokensUser;
        usersList.toArray(tokensUser = new String[usersList.size()]);
        DBC dbc = new DBC(getContext(), "localCfgBD", null, 1);
        for(String token : tokensUser){
            FireUser user = dbc.selectByToken(token);
            mUsers.add(user);
        }
        dbc.close();
        adapter = new UsersAdapter(mUsers, getContext(), getFragmentManager(), FirebaseAuth.getInstance().getCurrentUser().getUid());
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
