package com.example.livenet.ui.chat;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersFragment extends Fragment {
    private RecyclerView recyclerView;
    private UsersAdapter adapter;
    private ArrayList<FireUser> mUsers;
    private DBC dbc;
    private View root;

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
        return root;
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation);

        recyclerView.setLayoutAnimation(controller);

        recyclerView.scheduleLayoutAnimation();
    }

    private void readUsers() {
        dbc = new DBC(getActivity(), "localCfgBD", null, 1);
        mUsers = dbc.seleccionarData();
        adapter = new UsersAdapter(mUsers, root.getContext(),
                (MainActivity)getActivity(),
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                true,
                ((MainActivity)getActivity()).getLogged().getAlias(),
                getFragmentManager());

        recyclerView.setAdapter(adapter);
        runLayoutAnimation(recyclerView);
    }



}
