package com.example.lifecycle;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;

public class MainActivity extends AppCompatActivity {

    EditText txtMsg;
    Button btnSubmit,btnExit;
    TextView txtSpy;
    int originalColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtMsg = findViewById(R.id.txtMsg);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnExit = findViewById(R.id.btnExit);
        txtSpy = findViewById(R.id.txtSpy);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSubmit(v);
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickExit(v);
            }
        });

        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
        originalColor = getWindow().getDecorView().getSolidColor();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "onBackPressed", Toast.LENGTH_SHORT).show();
    }

    public void onClickExit(View view) {
        finishAffinity();
        System.exit(0);
    }
    public void onClickSubmit(View view) {
        String text = String.valueOf(txtMsg.getText());
        if(text.equals("Nguyễn Gia Bảo")) {
            Toast.makeText(this, "Change background to Red!", Toast.LENGTH_SHORT).show();
            getWindow().getDecorView().setBackgroundColor(Color.RED);
        } else if (text.equals("Nguyễn Quang Tuấn")) {
            Toast.makeText(this, "Change background to Blue!", Toast.LENGTH_SHORT).show();
            getWindow().getDecorView().setBackgroundColor(Color.BLUE);
        } else if (text.equals("Cao Hữu Quốc")) {
            Toast.makeText(this, "Change background to Green!", Toast.LENGTH_SHORT).show();
            getWindow().getDecorView().setBackgroundColor(Color.GREEN);
        } else if (text.equals("Mai Quý Đạt")) {
            Toast.makeText(this, "Change background to Yellow!", Toast.LENGTH_SHORT).show();
            getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
        } else if (text.equals("Ngô Xuân Hiếu")) {
            Toast.makeText(this, "Change background to Purple!", Toast.LENGTH_SHORT).show();
            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#800080"));
        } else {
            Toast.makeText(this, "Reset background to default", Toast.LENGTH_SHORT).show();
            getWindow().getDecorView().setBackgroundColor(originalColor);
        }

    }
}
