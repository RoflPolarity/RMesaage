package com.example.rmesaage.ChatChoose;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rmesaage.Chat.Message;
import com.example.rmesaage.Chat.UserChat;
import com.example.rmesaage.R;
import com.example.rmesaage.utils.server_utils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class Chatlst extends AppCompatActivity {
    private ChatAdapter chatAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        String username = intent.getStringExtra("author");
        chatAdapter = new ChatAdapter(new ArrayList<>());
        RecyclerView view = findViewById(R.id.recyclerview_chats);
        view.setAdapter(chatAdapter);
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (server_utils.searchByUsername(query)){
                    chatAdapter.bind(query,"");
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
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Message> messages = server_utils.getChats(username);
                        chatAdapter.chatList.clear();
                        for (int i = 0; i < messages.size(); i++) {
                            chatAdapter.chatList.add(new ChatLstItem(messages.get(i).getText(),messages.get(i).getMessageUser()));
                            chatAdapter.notifyDataSetChanged();
                        }

                    }
                });
            }
        },0,1500);
    }
}