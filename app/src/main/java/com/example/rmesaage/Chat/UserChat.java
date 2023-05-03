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



import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class UserChat extends AppCompatActivity {
    ChatAdapter chatAdapter;
    private MessageListener messageListener = new MessageListener() {
        @Override
        public void onMessageReceived(Message message) {
            chatAdapter.insert(message);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        ArrayList<Message> lst = new ArrayList<>();
        databaseUtils utils = new databaseUtils(UserChat.this);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String sendTo = intent.getStringExtra("sendTo");
        String sendToIP = intent.getStringExtra("sendToIP");
        String sendToPort = intent.getStringExtra("sendToPort");
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
        chatAdapter = new ChatAdapter(lst,username);
        RecyclerView recyclerView = findViewById(R.id.recyclerview_chats);
        recyclerView.setAdapter(chatAdapter);
        Button button = findViewById(R.id.button_send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.edit_text_message);
                Message message = new Message(username,editText.getText().toString(),chatAdapter.getItemCount()+1);
                chatAdapter.insert(message);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            Socket socket = new Socket(sendToIP,Integer.parseInt(sendToPort));
                            ObjectOutputStream OIS = new ObjectOutputStream(socket.getOutputStream());
                            OIS.writeObject(message);
                            socket.close();
                        } catch (IOException e) {

                            throw new RuntimeException(e);
                        }
                    }
                });
                thread.start();
            }
        });
    }



}