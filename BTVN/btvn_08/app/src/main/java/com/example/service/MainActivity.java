package com.example.service;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView txtMsg;
    Intent intentCallService4, intentCallService5, intentCallService6;
    BroadcastReceiver receiver;
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        txtMsg = (TextView) findViewById(R.id.txtMsg);
        findViewById(R.id.btnStart4).setOnClickListener(this);
        findViewById(R.id.btnStop4).setOnClickListener(this);
        findViewById(R.id.btnStart5).setOnClickListener(this);
        findViewById(R.id.btnStop5).setOnClickListener(this);
        findViewById(R.id.btnStart6).setOnClickListener(this);
        findViewById(R.id.btnStop6).setOnClickListener(this);
        Log.e("MAIN", "Main started");

        intentCallService4 = new Intent(this, ServiceMusic.class);
        intentCallService5 = new Intent(this, ServiceFibonacci.class);
        intentCallService6 = new Intent(this, ServiceGPS.class);

        IntentFilter filter5 = new IntentFilter("matos.action.GOSERVICE5");
        IntentFilter filter6 = new IntentFilter("matos.action.GPSFIX");
        receiver = new MyEmbeddedBroadcastReceiver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(receiver, filter5, Context.RECEIVER_EXPORTED);
            registerReceiver(receiver, filter6, Context.RECEIVER_EXPORTED);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnStart4) {
            Log.e("MAIN", "onClick: starting service4");
            startService(intentCallService4);
        }
        else if (v.getId() == R.id.btnStop4) {
            Log.e("MAIN", "onClick: stopping service4");
            stopService(intentCallService4);
        }
        else if (v.getId() == R.id.btnStart5) {
            Log.e("MAIN", "onClick: starting service5");
            startService(intentCallService5);
        }
        else if (v.getId() == R.id.btnStop5) {
            Log.e("MAIN", "onClick: stopping service5");
            stopService(intentCallService5);
        }
        else if (v.getId() == R.id.btnStart6) {
            Log.e("MAIN", "onClick: starting service6");
            startService(intentCallService6);
        }
        else if (v.getId() == R.id.btnStop6) {
            Log.e("MAIN", "onClick: stopping service6");
            stopService(intentCallService6);
        }
    }

    public class MyEmbeddedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("MAIN>>", "ACTION: " + intent.getAction());

            if (intent.getAction().equals("matos.action.GOSERVICE5")) {
                String service5Data = intent.getStringExtra("MyService5DataItem");
                Log.e("MAIN>>", "Data received from Service5: " + service5Data);
                txtMsg.append("\nService5Data: > " + service5Data);
            }

            else if (intent.getAction().equals("matos.action.GPSFIX")) {
                double latitude = intent.getDoubleExtra("latitude", -1);
                double longitude = intent.getDoubleExtra("longitude", -1);
                String provider = intent.getStringExtra("provider");
                String service6Data = provider + " lat: " + Double.toString(latitude)
                        + " lon: " + Double.toString(longitude);
                Log.e("MAIN>>", "Data received from Service6:" + service6Data);
                txtMsg.append("\nService6Data: > "+ service6Data);
            }
        }
    }
}