package com.example.rmesaage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rmesaage.ChatChoose.Chatlst;
import com.example.rmesaage.utils.databaseUtils;
import com.example.rmesaage.utils.server_utils;

public class Auth extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        SharedPreferences preferences = getSharedPreferences("myPrefs",MODE_PRIVATE);
        server_utils.initializeStreams();
        databaseUtils.init(Auth.this);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (server_utils.auth(preferences.getString("username",""),preferences.getString("password",""))){
                    Intent intent = new Intent(Auth.this,Chatlst.class);
                    intent.putExtra("author",preferences.getString("username",""));
                    intent.putExtra("password",preferences.getString("password",""));
                    startActivity(intent);
                    }
                }
            });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Button btnAuth = findViewById(R.id.loginButton);
        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EditText username = findViewById(R.id.usernameEditText);
                        EditText password = findViewById(R.id.passwordEditText);
                        if (server_utils.auth(username.getText().toString(),password.getText().toString())){
                            Intent intent = new Intent(Auth.this, Chatlst.class);
                            intent.putExtra("author",username.getText().toString());
                            startActivity(intent);
                        }else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Failed to establish a connection with the server", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Button btnRegister = findViewById(R.id.button_signup);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Auth.this,Register.class);
                startActivity(intent);
            }
        });
    }
}