package com.example.rmesaage.ChatChoose;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rmesaage.Chat.Message;
import com.example.rmesaage.Chat.UserChat;
import com.example.rmesaage.R;
import com.example.rmesaage.utils.databaseUtils;
import com.example.rmesaage.utils.server_utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;


public class Chatlst extends AppCompatActivity {
    private ChatAdapter chatAdapter;
    private boolean update = true;
    Timer timer = new Timer();
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
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Message> res = server_utils.sync(username);
                if (res!=null && res.size()>0){
                    ArrayList<ChatLstItem> senders = databaseUtils.getChats(username,getApplicationContext());
                    ArrayList<Message> inserted = new ArrayList<>();
                    for (int i = 0; i < senders.size(); i++) {
                        ArrayList<Message> localMessages = databaseUtils.getMsList(username,senders.get(i).getName(),getApplicationContext());
                        for (int j = 0; j < localMessages.size(); j++) {
                            if (!res.get(i).equals(localMessages.get(j))){
                                databaseUtils.insert(res.get(i),getApplicationContext());
                                inserted.add(res.get(i));
                            }
                        }
                    }
                    res.removeAll(inserted);
                    for (int i = 0; i < res.size(); i++) {
                        databaseUtils.insert(res.get(i),getApplicationContext());
                    }
                }
                System.out.println("Синхронизированно");
            }
        });
        thread.start();
        Toolbar tools = findViewById(R.id.toolbar_chats);
        ImageView gif = tools.findViewById(R.id.gif);
        Picasso.get().load("https://media.tenor.com/D4b9-Caw8CEAAAAC/alphabet-run.gif").resize(48, 48).into(gif, new Callback() {
            @Override
            public void onSuccess() {
                // Код, который выполнится при успешной загрузке и отображении GIF
                System.out.println("Загружено");
            }

            @Override
            public void onError(Exception e) {
                // Код, который выполнится в случае ошибки загрузки или отображения GIF
                e.printStackTrace();
            }
        });


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
}