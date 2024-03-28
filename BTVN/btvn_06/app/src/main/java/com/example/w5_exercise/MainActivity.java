package com.example.w5_exercise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity implements MainCallbacks {
    FragmentTransaction ft;
    FragmentRight fragmentRight;
    FragmentLeft fragmentLeft;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialize fragments
        fragmentLeft = FragmentLeft.newInstance("first-blue");
        fragmentRight = FragmentRight.newInstance("first-red");

        // Begin a fragment transaction for FragmentLeft
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameList, fragmentLeft);
        ft.addToBackStack(null); // Add transaction to the back stack
        ft.commit();

        // Begin another fragment transaction for FragmentRight
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameDetails, fragmentRight);
        ft.addToBackStack(null); // Add transaction to the back stack
        ft.commit();

        // Set click listeners for navigation buttons
        findViewById(R.id.First_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentLeft.navigateToFirstItem();
            }
        });

        findViewById(R.id.Last_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentLeft.navigateToLastItem();
            }
        });

        findViewById(R.id.Next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentLeft.navigateToNextItem();
            }
        });

        findViewById(R.id.Previous_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentLeft.navigateToPreviousItem();
            }
        });
    }

    // Example method where database operations are performed


    @Override
    public void onMsgFromFragToMain(String sender, String strValue) {
        if (sender.equals("BLUE-FRAG")) {
            try {
                fragmentRight.onMsgFromMainToFragment(strValue);
            } catch (Exception e) {
                Log.e("ERROR", "onStrFromFragToMain " + e.getMessage());
            }
        }
    }
}
