package com.example.rmesaage.Chat.MediaPicker;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rmesaage.Chat.ChatAdapter;
import com.example.rmesaage.Chat.Message;
import com.example.rmesaage.R;
import com.example.rmesaage.utils.server_utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MyFragment extends Fragment {


    private Md_adapter adapter;
    private static FrameLayout frame;
    private static ChatAdapter chat;
    private static String name;
    private static String send;
    public MyFragment() {
        // Пустой конструктор обязателен
    }

    public static MyFragment newInstance(Md_adapter adapter, FrameLayout frameLayout, ChatAdapter chatAdapter, String username, String sendTo) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putSerializable("adapter", adapter);
        fragment.setArguments(args);
        frame = frameLayout;
        chat = chatAdapter;
        name = username;
        send = sendTo;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            adapter = (Md_adapter) getArguments().getSerializable("adapter");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_media_choose, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerView.setAdapter(adapter);
        ImageButton button = view.findViewById(R.id.button_cancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (frame.getVisibility()==View.VISIBLE) frame.setVisibility(View.GONE);
            }
        });
        ImageButton apply = view.findViewById(R.id.button_select);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Md_adapter.ViewHolder> arrayList = adapter.selected;
                ArrayList<byte[]> bytes = new ArrayList<>();
                for (int i = 0; i < arrayList.size(); i++){
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    arrayList.get(i).map.compress(Bitmap.CompressFormat.JPEG,100,stream);
                    bytes.add(stream.toByteArray());
                }
                chat.insert(new Message(name,bytes));
                //server_utils.sendMessage(name,send,bytes);
            }
        });
        return view;
    }
}
