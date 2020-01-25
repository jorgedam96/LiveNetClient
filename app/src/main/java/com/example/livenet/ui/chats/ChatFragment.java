package com.example.livenet.ui.chats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.livenet.R;

public class ChatFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        return root;
    }

    /*
    MD5: 32:46:1F:EE:84:0C:D8:45:F5:97:69:E5:F9:A6:C3:D7
SHA1: FC:B3:2B:BB:C3:23:DD:D4:56:97:4C:4C:F0:61:63:AB:70:45:0E:FA
SHA-256: 47:94:9C:CA:19:6C:62:A2:3F:EF:4E:0F:AE:E0:3C:EA:7E:04:25:CE:AA:23:F0:0B:C8:DD:2E:E5:8A:17:BE:CD

     */
}