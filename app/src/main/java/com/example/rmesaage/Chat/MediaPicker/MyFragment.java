package com.example.rmesaage.Chat.MediaPicker;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Pair;
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
import com.example.rmesaage.utils.databaseUtils;
import com.example.rmesaage.utils.server_utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                Map<String, Md_adapter.ViewHolder> select = adapter.select;
                List<Map.Entry<String, Md_adapter.ViewHolder>> sel = new ArrayList<>(select.entrySet());
                ArrayList<byte[]> bytes = new ArrayList<>();
                ArrayList<String> path = new ArrayList<>();
                for(Map.Entry<String,Md_adapter.ViewHolder> pair : sel){
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    pair.getValue().map.compress(Bitmap.CompressFormat.JPEG,100,stream);
                    bytes.add(stream.toByteArray());
                    path.add(pair.getKey());
                }
                chat.insert(new Message(name,bytes));
                adapter.selected.clear();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < path.size(); i++) {
                    sb.append(path.get(i)).append("   ");
                }
                databaseUtils.insert(new Message(0,name,null,bytes,send),sb.toString());

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        server_utils.sendMessage(new Message(0,name,null,bytes,send),getContext());
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return view;
    }
}
