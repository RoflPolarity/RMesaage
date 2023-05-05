package com.example.rmesaage.ChatChoose;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rmesaage.Chat.Message;
import com.example.rmesaage.Chat.UserChat;
import com.example.rmesaage.R;
import com.example.rmesaage.User;
import com.example.rmesaage.interfaces.MessageListener;
import com.example.rmesaage.utils.databaseUtils;
import com.example.rmesaage.utils.server_utils;


import java.util.Timer;
import java.util.TimerTask;

public class Chatlst extends AppCompatActivity {
    private ChatAdapter chatAdapter;

    private MessageListener messageListener = new MessageListener() {
        @Override
        public void onMessageReceived(Message message) {
            chatAdapter.findAndUpdate(message);
        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        String username = intent.getStringExtra("author");
        String password = intent.getStringExtra("password");

        databaseUtils utils = new databaseUtils(Chatlst.this);
        chatAdapter = new ChatAdapter(utils.getChats(username));
        RecyclerView view = findViewById(R.id.recyclerview_chats);
        view.setAdapter(chatAdapter);
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (server_utils.searchByUsername(query)){
                    chatAdapter.bind(query);
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
                startActivity(IntentToChat);
            }
        });

    }
}