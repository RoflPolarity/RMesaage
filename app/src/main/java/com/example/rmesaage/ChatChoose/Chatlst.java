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
import com.example.rmesaage.utils.KafkaServ;
import com.example.rmesaage.utils.MessageReceiverManager;
import com.example.rmesaage.utils.databaseUtils;
import com.example.rmesaage.utils.server_utils;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

public class Chatlst extends AppCompatActivity {
    private ChatAdapter chatAdapter;
    private Consumer<String, String> consumer;

    private MessageListener messageListener = new MessageListener() {
        @Override
        public void onMessageReceived(Message message) {
            chatAdapter.findAndUpdate(message);
        }
    };

    private void createKafkaConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "kafka-broker1:9092,kafka-broker2:9092");
        props.put("group.id", "my-group");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        consumer = new KafkaConsumer<String, String>(props);
    }

    private void subscribeToKafkaTopic() {
        String topicName = "chat-messages";

        consumer.subscribe(Arrays.asList(topicName));

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                    for (ConsumerRecord<String, String> record : records) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Обновление UI в главном потоке
                                String message = record.value();
                                // Отображение сообщения в списке чатов
                            }
                        });
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ArrayList<User> users = server_utils.getIpTable();
        Intent intent = getIntent();
        String username = intent.getStringExtra("author");
        String password = intent.getStringExtra("password");
        databaseUtils utils = new databaseUtils(Chatlst.this);
        chatAdapter = new ChatAdapter(utils.getChats(username));
        RecyclerView view = findViewById(R.id.recyclerview_chats);
        view.setAdapter(chatAdapter);
        SearchView searchView = findViewById(R.id.search_view);
        MessageReceiverManager messageReceiverManager = MessageReceiverManager.getInstance();
        messageReceiverManager.start();
        messageReceiverManager.addMessageListener(messageListener);
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
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                server_utils.updateIP(username,password);
            }
        },1000);

    }
}