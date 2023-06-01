package com.example.rmesaage.Chat;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rmesaage.Chat.MediaPicker.ImagePickerActivity;
import com.example.rmesaage.R;
import com.example.rmesaage.utils.databaseUtils;
import com.example.rmesaage.utils.server_utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class UserChat extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    ChatAdapter chatAdapter;
    private static final int REQUEST_IMAGE_PICKER = 1;
    private ActivityResultLauncher<Intent> imagePickerLauncher;


    public void OnNewMessageRec(Message message){
        chatAdapter.insert(message);
        System.out.println(message.getBitMaps()+ " - maps");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatAdapter.notifyItemInserted(chatAdapter.getItemCount());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String sendTo = intent.getStringExtra("SendTo");
        ArrayList<Message> messages = databaseUtils.getMsList(username,sendTo,getApplicationContext());
        TextView name = findViewById(R.id.ChatName);
        name.setText(sendTo);



        chatAdapter = new ChatAdapter(messages,username);
        RecyclerView recyclerView = findViewById(R.id.recyclerview_chats);
        recyclerView.setAdapter(chatAdapter);
        databaseUtils.setChat(this);
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
                Message message = new Message(chatAdapter.getItemCount()+1,username,editText.getText().toString(),null,sendTo,null);
                chatAdapter.insert(message);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatAdapter.notifyItemInserted(chatAdapter.getItemCount());
                    }
                });
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        server_utils.sendMessage(message,getApplicationContext());
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                editText.setText("");
            }
        });

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            // Обработайте результат выбора изображений здесь
                            Intent data = result.getData();
                            if (data != null) {
                                // Получите выбранные изображения из интента и обработайте их
                                    String path = data.getStringExtra(ImagePickerActivity.EXTRA_SELECTED_IMAGES);
                                try {
                                    BufferedReader br = new BufferedReader(new FileReader(new File(path)));
                                    ArrayList<byte[]> res = new ArrayList<>();
                                    ArrayList<String> paths = new ArrayList<>();
                                    String line = "";
                                    while ((line = br.readLine())!=null){
                                        Uri imageUri = Uri.parse(line);
                                        paths.add(line);
                                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                        byte[] buffer = new byte[1024];
                                        int bytesRead;

                                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                                            byteArrayOutputStream.write(buffer, 0, bytesRead);
                                        }
                                        inputStream.close();
                                        res.add(byteArrayOutputStream.toByteArray());
                                    }
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            server_utils.sendMessage(new Message(0,username,null,res,sendTo, paths),getApplicationContext());
                                        }
                                    });
                                    thread.start();
                                    chatAdapter.insert(new Message(0,username,null,res,sendTo,paths));
                                    chatAdapter.notifyItemInserted(chatAdapter.getItemCount());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                });


        ImageButton attach = findViewById(R.id.attach);
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserChat.this, ImagePickerActivity.class);
                imagePickerLauncher.launch(intent);
            }
        });

    }
}