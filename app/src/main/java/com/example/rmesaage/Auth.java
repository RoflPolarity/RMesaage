package com.example.rmesaage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rmesaage.ChatChoose.Chatlst;
import com.example.rmesaage.utils.server_utils;

public class Auth extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        Button btnAuth = findViewById(R.id.loginButton);
        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText username = findViewById(R.id.usernameEditText);
                EditText password = findViewById(R.id.passwordEditText);
                if (server_utils.auth(username.getText().toString(),password.getText().toString())){
                    Intent intent = new Intent(Auth.this, Chatlst.class);
                    intent.putExtra("author",username.getText().toString());
                    intent.putExtra("password",password.getText().toString());
                    startActivity(intent);
                }else btnAuth.setText("2");
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