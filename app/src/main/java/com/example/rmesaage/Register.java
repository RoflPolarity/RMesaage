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
import com.example.rmesaage.utils.SaveFile;
import com.example.rmesaage.utils.server_utils;

import java.io.File;

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
                    Intent intent = new Intent(Register.this, Chatlst.class);
                    intent.putExtra("author",usernameString);
                    SharedPreferences preferences = getSharedPreferences("myPrefs",MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username",usernameString);
                    editor.putString("password",passwordString);
                    editor.apply();
                    startActivity(intent);
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Failed to establish a connection with the server", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
    }
}