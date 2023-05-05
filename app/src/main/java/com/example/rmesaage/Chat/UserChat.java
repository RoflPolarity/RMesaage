package com.example.rmesaage.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rmesaage.R;
import com.example.rmesaage.interfaces.MessageListener;
import com.example.rmesaage.utils.databaseUtils;
import com.example.rmesaage.utils.databaseUtils.message;
import com.example.rmesaage.utils.server_utils;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class UserChat extends AppCompatActivity {
    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String sendTo = intent.getStringExtra("sendTo");
        ArrayList<Message> messages = server_utils.getMessage(username,sendTo);
        TextView name = findViewById(R.id.ChatName);
        name.setText(sendTo);
        chatAdapter = new ChatAdapter(messages,username);
        RecyclerView recyclerView = findViewById(R.id.recyclerview_chats);
        recyclerView.setAdapter(chatAdapter);
        Button button = findViewById(R.id.button_send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.edit_text_message);
                Message message = new Message(username,editText.getText().toString(),chatAdapter.getItemCount()+1);
                chatAdapter.insert(message);
                server_utils.sendMessage(username,sendTo,editText.getText().toString());
            }
        });

    }



}