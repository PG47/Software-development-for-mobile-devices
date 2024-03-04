package com.example.usersignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class resultform extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultform);

        // Get intent
        Intent intent = getIntent();

        // Retrieve data
        String username = intent.getStringExtra("USERNAME");
        String password = intent.getStringExtra("PASSWORD");
        String birthdate = intent.getStringExtra("DATE_OF_BIRTH");
        String gender = intent.getStringExtra("GENDER");
        String hobbies = intent.getStringExtra("HOBBIES");

        String maskedPassword = maskPassword(password);

        TextView strResult = findViewById(R.id.strResult);
        String template = getString(R.string.result_text);
        template = template.replace("{{username}}", username)
                .replace("{{password}}", maskedPassword)
                .replace("{{birthdate}}", birthdate)
                .replace("{{gender}}", gender)
                .replace("{{hobbies}}", hobbies);

        strResult.setText(template);

        Button btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            };
        });
    }
    private String maskPassword(String password) {
        StringBuilder maskedPassword = new StringBuilder();
        for (int i = 0; i < password.length(); i++) {
            maskedPassword.append("*");
        }
        return maskedPassword.toString();
    }
}