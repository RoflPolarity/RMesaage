package com.example.rmesaage.ChatChoose;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rmesaage.Chat.Message;
import com.example.rmesaage.Chat.UserChat;
import com.example.rmesaage.R;
import com.example.rmesaage.utils.databaseUtils;
import com.example.rmesaage.utils.server_utils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;


public class Chatlst extends AppCompatActivity {
    private ChatAdapter chatAdapter;
    private boolean update = true;
    Timer timer = new Timer();

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        Intent intent = getIntent();
        server_utils.getNewMessageThread(getApplicationContext());
        username = intent.getStringExtra("author");
        chatAdapter = new ChatAdapter(new ArrayList<>());
        RecyclerView view = findViewById(R.id.recyclerview_chats);
        view.setAdapter(chatAdapter);
        SearchView searchView = findViewById(R.id.search_view);
        synch();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                AtomicBoolean bool = new AtomicBoolean();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        update = false;
                        bool.set(server_utils.searchByUsername(query));
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (bool.get()) {
                    chatAdapter.bind(query, "");
                    return true;
                }
            return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        chatAdapter.setOnItemClickListener(new ChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent IntentToChat = new Intent(Chatlst.this, UserChat.class);
                IntentToChat.putExtra("username",username);
                IntentToChat.putExtra("SendTo",chatAdapter.getChatList().get(position).getName());
                startActivity(IntentToChat);
            }
        });

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (searchView.getQuery().toString().equals("")) update = true;
                        if (update) {
                            ArrayList<ChatLstItem> messages = databaseUtils.getChats(username, getApplicationContext());
                            chatAdapter.chatList.clear();
                            chatAdapter.chatList.addAll(messages);
                            chatAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        },0,1500);
    }


    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }



    public void synch(){
        ImageButton btnSynch = findViewById(R.id.sychronize);
        btnSynch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Message> res = server_utils.sync(username);
                        if (res != null){
                        for (int i = 0; i < res.size(); i++) {
                            databaseUtils.insert(res.get(i),getApplicationContext());

                            }
                        }
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
    }
}