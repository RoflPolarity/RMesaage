package com.example.rmesaage.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rmesaage.Chat.MediaPicker.MyMediaChooser;
import com.example.rmesaage.R;
import com.example.rmesaage.utils.server_utils;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class UserChat extends AppCompatActivity {
    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String sendTo = intent.getStringExtra("SendTo");
        FrameLayout frameLayout = findViewById(R.id.fragment_container);
        ArrayList<Message> messages = server_utils.getMessage(username,sendTo);
        TextView name = findViewById(R.id.ChatName);
        name.setText(sendTo);
        chatAdapter = new ChatAdapter(messages,username,sendTo);
        RecyclerView recyclerView = findViewById(R.id.recyclerview_chats);
        recyclerView.setAdapter(chatAdapter);
        EditText editText = findViewById(R.id.edit_text_message);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.scrollToPosition(chatAdapter.messageList.size() - 1);
                    }
                }, 0);
            }
        });

        ImageButton button = findViewById(R.id.button_send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.edit_text_message);
                Message message = new Message(username,editText.getText().toString(),chatAdapter.getItemCount()+1);
                chatAdapter.insert(message);
                server_utils.sendMessage(username,sendTo,editText.getText().toString());
                editText.setText("");
            }
        });

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatAdapter.updateDialog(server_utils.getMessage(username,sendTo));
                    }
                });
            }
        },0,1000);

        ImageButton attach = findViewById(R.id.attach);
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayout.setVisibility(View.VISIBLE);
                MyMediaChooser fragment = new MyMediaChooser();
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
            }
        });
    }




}