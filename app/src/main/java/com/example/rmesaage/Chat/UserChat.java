package com.example.rmesaage.Chat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rmesaage.Chat.MediaPicker.Md_adapter;
import com.example.rmesaage.Chat.MediaPicker.MyFragment;
import com.example.rmesaage.R;
import com.example.rmesaage.utils.databaseUtils;
import com.example.rmesaage.utils.server_utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UserChat extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String sendTo = intent.getStringExtra("SendTo");
        ArrayList<Message> messages = databaseUtils.getMsList(username,sendTo);
        TextView name = findViewById(R.id.ChatName);
        name.setText(sendTo);


        chatAdapter = new ChatAdapter(messages,username);
        RecyclerView recyclerView = findViewById(R.id.recyclerview_chats);
        recyclerView.setAdapter(chatAdapter);

        Md_adapter adapter = new Md_adapter(getAllMedia(),getApplicationContext());
        verifyStoragePermissions();
        FrameLayout frameLayout = findViewById(R.id.fragment_container);
        MyFragment fragment = MyFragment.newInstance(adapter,frameLayout,chatAdapter,username,sendTo);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

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
                Message message = new Message(chatAdapter.getItemCount()+1,username,editText.getText().toString(),null,sendTo);
                chatAdapter.insert(message);
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

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatAdapter.updateDialog(databaseUtils.getMsList(username,sendTo));
                    }
                });
            }
        },0,1000);

        ImageButton attach = findViewById(R.id.attach);
        final boolean[] visible = {false};
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!visible[0]) {
                    frameLayout.setVisibility(View.VISIBLE);
                    visible[0] = true;
                }else{
                    frameLayout.setVisibility(View.GONE);
                    visible[0] = false;
                }
            }
        });

    }

    public List<String> getAllMedia() {
        List<String> mediaList = new ArrayList<>();
        String[] projection = {MediaStore.Images.Media.DATA};
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";
        try (Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, sortOrder)) {
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                while (cursor.moveToNext()) {
                    String path = cursor.getString(columnIndex);
                    mediaList.add(path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaList;
    }


    private void verifyStoragePermissions() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
        int permission = ContextCompat.checkSelfPermission(this, permissions[0]);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_EXTERNAL_STORAGE);
        }
    }

}