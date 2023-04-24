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
import com.example.rmesaage.utils.MessageReceiverManager;
import com.example.rmesaage.utils.databaseUtils;
import com.example.rmesaage.utils.databaseUtils.message;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

public class UserChat extends AppCompatActivity {
    ChatAdapter chatAdapter;
    private MessageListener messageListener = new MessageListener() {
        @Override
        public void onMessageReceived(Message message) {
            chatAdapter.insert(message);
        }
    };

    private Producer<String, String> producer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MessageReceiverManager.getInstance().start();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        MessageReceiverManager.getInstance().addMessageListener(messageListener);
        ArrayList<Message> lst = new ArrayList<>();
        databaseUtils utils = new databaseUtils(UserChat.this);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String sendTo = intent.getStringExtra("sendTo");
        String sendToIP = intent.getStringExtra("sendToIP");
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
                String message = editText.getText().toString();
                sendMessageToKafka(message);
                chatAdapter.insert(new Message(username,message));
                editText.setText("");
            }
        });
        createKafkaProducer();
        subscribeToKafkaTopic();
    }
    private void createKafkaProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "kafka-broker1:9092,kafka-broker2:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<String, String>(props);
    }
    private void sendMessageToKafka(String message) {
        String topicName = "chat-messages";

        ProducerRecord<String, String> record = new ProducerRecord<String, String>(topicName, message);
        producer.send(record);
    }

    private void subscribeToKafkaTopic() {
        String topicName = "chat-messages";
        Properties props = new Properties();
        props.put("bootstrap.servers", "kafka-broker1:9092,kafka-broker2:9092");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList(topicName));
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, String> record : records) {
                        chatAdapter.insert(new Message(record.key(), record.value()));
                    }
                }
            }
        }).start();
    }
}