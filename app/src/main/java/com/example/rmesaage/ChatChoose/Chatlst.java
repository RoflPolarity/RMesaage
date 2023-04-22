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
import com.example.rmesaage.utils.MessageReceiver;
import com.example.rmesaage.utils.databaseUtils;
import com.example.rmesaage.utils.server_utils;

import java.io.IOException;
import java.util.ArrayList;

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
        ArrayList<User> users = server_utils.getIpTable();
        Intent intent = getIntent();
        String username = intent.getStringExtra("author");
        databaseUtils utils = new databaseUtils(Chatlst.this);
        chatAdapter = new ChatAdapter(utils.getChats(username));
        RecyclerView view = findViewById(R.id.recyclerview_chats);
        view.setAdapter(chatAdapter);
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getUsername().equals(query)){
                        chatAdapter.bind(users.get(i));
                        return true;
                    }
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
                IntentToChat.putExtra("username",getIntent().getStringExtra("author"));
                IntentToChat.putExtra("sendTo",chatAdapter.getChatList().get(position).getName());
                User sendTo = null;
                for (int i = 0; i < users.size(); i++) {
                    if (chatAdapter.getChatList().get(position).getName().equals(users.get(i).getUsername())){
                        sendTo = users.get(i);
                        break;
                    }
                }
                IntentToChat.putExtra("sendToIP",sendTo.getIp());
                startActivity(IntentToChat);
            }
        });

        MessageReceiver messageReceiver = new MessageReceiver();
        messageReceiver.addMessageListener(messageListener);
        new Thread(messageReceiver).start();
    }
}