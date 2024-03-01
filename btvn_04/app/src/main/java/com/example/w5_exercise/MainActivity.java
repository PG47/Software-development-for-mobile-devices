package com.example.w5_exercise;

import static android.app.ProgressDialog.show;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements MainCallbacks {
    FragmentTransaction ft; FragmentRight fragmentRight; FragmentLeft fragmentLeft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_main);
        ft = getSupportFragmentManager().beginTransaction(); fragmentLeft = FragmentLeft.newInstance("first-blue");
        ft.replace(R.id.frameList, fragmentLeft); ft.commit();
        ft = getSupportFragmentManager().beginTransaction(); fragmentRight = FragmentRight.newInstance("first-red");
        ft.replace(R.id.frameDetails, fragmentRight); ft.commit();
    }
    @Override
    public void onMsgFromFragToMain(String sender, String strValue) {
        if (sender.equals("BLUE-FRAG")) {
            try {
                fragmentRight.onMsgFromMainToFragment(strValue);
            }
            catch (Exception e) { Log.e("ERROR", "onStrFromFragToMain " + e.getMessage()); }
        }
    }
}