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
    private ValueEventListener listado;

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

        listenerChat();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }

    private void listenerChat() {
        reference = FirebaseDatabase.getInstance().getReference("Chats");

        listado = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    try {
                        //Comprobamos si el mensaje viene de nuestra parte
                        if (chat.getSender().equals(((MainActivity) getActivity()).getFireBaseMain().getUsuario().getUid())) {
                            if (!usersList.contains(chat.getReceiver())) {
                                usersList.add(chat.getReceiver());
                            }
                        }
                        //Comprobamos si viene de otro usuario
                        if (chat.getReceiver().equals(((MainActivity) getActivity()).getFireBaseMain().getUsuario().getUid())) {
                            if (!usersList.contains(chat.getSender())) {
                                usersList.add(chat.getSender());
                            }
                        }
                    } catch (NullPointerException ex) {
                    }

                }
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.addValueEventListener(listado);
    }

    /**
     * Leemos los usuarios de la BBDD y mostramos en la lista de chats unicamente con los
     * que hemos entablado conversacion, revisando que solo muestre 1 usuario por cada conversacion
     */
    private void readChats() {
        try {
            mUsers.clear();
            String[] tokensUser;
            usersList.toArray(tokensUser = new String[usersList.size()]);
            DBC dbc = new DBC(root.getContext(), "localCfgBD", null, 1);
            for (String token : tokensUser) {
                FireUser user = dbc.selectByToken(token);
                mUsers.add(user);
            }
            for (FireUser user : mUsers) {
                if (user.getUsername().isEmpty()) {
                    mUsers.remove(user);
                }
            }

            dbc.close();
            adapter = new UsersAdapter(mUsers,
                    root.getContext(),
                    (MainActivity)getActivity(),
                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    false,
                    ((MainActivity) getActivity()).getLogged().getAlias(),
                    getFragmentManager());
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            if (e.getMessage() != null) {
                System.out.println(" read chats: " + e.getMessage());
            }
        }
    }


    @Override
    public void onDestroy() {
        reference.removeEventListener(listado);
        super.onDestroy();
    }
}
