package com.example.auth;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.auth.R.id;
import com.example.auth.R.layout;

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
import java.security.PublicKey;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    public static String Version = "0.0.21";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        EditText psswd = findViewById(R.id.psswd2);
        psswd.setText("16061992");

        startService(new Intent(this, BackgroundService.class));

        SharedPreferences cfg = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor cfgE = cfg.edit();
        cfgE.putString("token", null);

        cfgE.commit();
        Button authBtt = findViewById(R.id.authBtt);
        authBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String result = auth(psswd.getText().toString(), "").toString();
                    System.out.println(result);
                    result = result.substring(result.indexOf('{'), result.lastIndexOf("]"));
                    JSONObject json = new JSONObject(result);
                    System.out.println(json.getString("login"));
                    if (json.getBoolean("login") == true) {

                        cfgE.putString("token", json.getString("token"));
                        cfgE.commit();
                        openOrderActivity();

                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Button settButt = findViewById(R.id.settButt);
        settButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettingActivity();
            }
        });
    }

    public void openSettingActivity() {
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);

    }

    public void openOrderActivity() {


        Intent intent = new Intent(this, orders.class);
        startActivity(intent);
    }

    public Optional<String> auth(String psswd, String requestBody) throws MalformedURLException, IOException{
        String t = "500";
        SharedPreferences cfg = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor cfgE = cfg.edit();
        String urlLink = cfg.getString("host","unknown").toString()+"/login.php?psswd="+psswd;
        System.out.println(urlLink);
        HttpURLConnection con = (HttpURLConnection) new URL(urlLink).openConnection();
        con.setRequestProperty("Content-Type", "application/x-www-from-urlencoded");
        con.setConnectTimeout(500);
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        try(OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream())){
            writer.write(requestBody);
        }

        if(con.getResponseCode() != 200) {
            Toast.makeText(getApplicationContext(),"Нет соединения!",Toast.LENGTH_LONG).show();
            return Optional.empty();
        }

        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(con.getInputStream(), Charset.forName("utf-8")))){
            return Optional.of(reader.lines().collect(Collectors.joining(System.lineSeparator())));
        }
    }
}