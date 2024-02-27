package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    String[] items = {"Cao Huu Quoc\n0123456789", "Nguyen Quang Tuan\n0123456788", "Nguyen Gia Bao\n0123456787", "Mai Quy Dat\n0123456786", "Ngo Xuan Hieu\n0123456785"};
    Integer[] thumbnails = {R.drawable.m1, R.drawable.m2, R.drawable.m3, R.drawable.m4, R.drawable.m5};
    ListView myListView; TextView textMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myListView = (ListView) findViewById(R.id.myList);
        textMsg = (TextView) findViewById(R.id.textMsg);
        CustomList aa = new CustomList(this, R.layout.custom_list, thumbnails, items);
        myListView.setAdapter(aa);
        myListView.setDivider(null);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = items[i].split("\n")[0];
                textMsg.setText("You choose: " + name);
            }
        });
    }
}