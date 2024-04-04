package com.example.rss;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    ImageAdapter adapterMainSubjects;
    GridView myMainGridView;
    Integer[] images = {R.mipmap.ic_news_vne_foreground, R.mipmap.ic_news_dt_foreground, R.mipmap.ic_news_tn_foreground, R.mipmap.ic_news_bm_foreground};
    String[] titles = {"VNEXPRESS", "DAN TRI", "THANH NIEN", "BAO MOI"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myMainGridView = (GridView) findViewById(R.id.newsGrid);
        myMainGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent callShowChannels = new Intent(MainActivity.this, ShowChannels.class);
                Bundle myBundle = new Bundle();
                myBundle.putInt("urlImage", images[i]);
                myBundle.putString("urlTitle", titles[i]);
                callShowChannels.putExtras(myBundle);
                startActivity(callShowChannels);
            }
        });
        adapterMainSubjects = new ImageAdapter(images, this);
        myMainGridView.setAdapter(adapterMainSubjects);

        this.setTitle("NEWS APP");
    }
}