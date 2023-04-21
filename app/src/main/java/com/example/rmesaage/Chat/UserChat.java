package com.example.rmesaage.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rmesaage.R;
import com.example.rmesaage.utils.databaseUtils;
import com.example.rmesaage.utils.databaseUtils.message;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class UserChat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        ArrayList<Message> lst = new ArrayList<>();
        databaseUtils utils = new databaseUtils(UserChat.this);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String sendTo = intent.getStringExtra("sendTo");

        ArrayList<message> userMsLst = utils.getMsList(username,sendTo);
        ArrayList<message> otherMsLst = utils.getMsList(sendTo,username);

        for (int i = 0; i < userMsLst.size(); i++) {
            lst.add(new Message(username,userMsLst.get(i).getText(),userMsLst.get(i).getId()));
        }
        for (int i = 0; i < otherMsLst.size(); i++) {
            lst.add(new Message(sendTo,otherMsLst.get(i).getText(),otherMsLst.get(i).getId()));
        }

        lst = (ArrayList<Message>) lst.stream().sorted((x, y)->x.getId()>y.getId() ? 1:-1).collect(Collectors.toList());
        if (lst.size()==0){
            utils.insert(new message(1,username,sendTo,""));
            lst.add(new Message(username,"",0));
        }
        TextView name = findViewById(R.id.ChatName);
        name.setText(sendTo);
        ChatAdapter chatAdapter = new ChatAdapter(lst,username);
        RecyclerView recyclerView = findViewById(R.id.recyclerview_chats);
        recyclerView.setAdapter(chatAdapter);

    }
}