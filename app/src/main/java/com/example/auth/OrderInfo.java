package com.example.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrderInfo extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setContentView(R.layout.orderinfo);

        Intent info = getIntent();

        TextView courier = findViewById(R.id.courier);
        courier.setText(info.getStringExtra("courier").toString());


        Button navCloseOrders = findViewById(R.id.openOrders);
        navCloseOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOrderActivity();
            }
        });
    }

    public void openOrderActivity(){
        Intent intent = new Intent(this, orders.class);
        startActivity(intent);
    }
}
