package com.example.rmesaage;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rmesaage.utils.server_utils;

public class Register extends AppCompatActivity {
    private EditText username,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.edittext_username);
        password = findViewById(R.id.edittext_password);
        Button btnReg = findViewById(R.id.button_signup);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameString = username.getText().toString(),
                passwordString = password.getText().toString();
                if (server_utils.reg(usernameString,passwordString)){
                    btnReg.setText("1");
                }else btnReg.setText("2");
            }
        });
    }
}