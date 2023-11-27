package com.example.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.auth.R.id;
import com.example.auth.R.layout;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class closedOrders extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(1);
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setContentView(layout.orders);
        overridePendingTransition(0,0);
        ThreadPolicy policy = (new Builder()).permitAll().build();
        StrictMode.setThreadPolicy(policy);
        SharedPreferences cfg = PreferenceManager.getDefaultSharedPreferences(this);
        Editor cfgE = cfg.edit();
        ListView orderList = (ListView)this.findViewById(id.orderList);
        Button uname = (Button)this.findViewById(id.uname);
        String result = null;

        JSONObject json;


        Button navOpenOrders = findViewById(id.openOrders);
        navOpenOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread.interrupted();
                openOrderActivity();
            }
        });

        listUpdate();

        try {
            result = this.getUserInfo("").toString();
            result = result.substring(result.indexOf(123), result.lastIndexOf("]"));
            json = new JSONObject(result);
            uname.setText(json.getString("user_name"));
            cfgE.putString("user_name", json.getString("user_name"));
            cfgE.commit();
        } catch (JSONException | IOException var14) {
            var14.printStackTrace();
        }

        orderList.setAdapter(listUpdate());


    }


    public cardAdapter listUpdate(){
        try {
            JSONObject json;
            String result = this.getOrderList("").toString();
            result = result.substring(result.indexOf(123), result.lastIndexOf("]"));
            json = new JSONObject(result);
            JSONArray list = json.getJSONArray("Lines");
            ArrayList<HashMap<String, String>> orders = new ArrayList();

            for(int i = 0; i < json.getJSONArray("Lines").length(); ++i) {
                System.out.println(list.getString(i).toString());
                JSONObject order = list.getJSONObject(i);
                HashMap<String, String> thisOrder = new HashMap();
                thisOrder.put("orderN", order.getString("orderN").toString());
                thisOrder.put("id", order.getString("id").toString());
                thisOrder.put("phone", order.getString("phone").toString());
                thisOrder.put("addr", order.getString("addr").toString());
                thisOrder.put("payType", order.getString("payType").toString());
                thisOrder.put("time", order.getString("time").toString());
                thisOrder.put("status", order.getString("status").toString());
                thisOrder.put("statusid", order.getString("statusid").toString());
                thisOrder.put("courier", order.getString("courier").toString());
                thisOrder.put("summ", order.getString("summ").toString());
                System.out.println(order.getString("statusid"));
                orders.add(thisOrder);
            }
            cardAdapter adapter1 = new cardAdapter(this, orders);
            return adapter1;
            //orderList.setAdapter(adapter1);
        } catch (IOException | JSONException var15) {
            var15.printStackTrace();
        }
        return null;
    }

    public Optional<String> getOrderList(String requestBody) throws MalformedURLException, IOException {
        String t = "500";
        SharedPreferences cfg = PreferenceManager.getDefaultSharedPreferences(closedOrders.this);
        Editor cfgE = cfg.edit();
        String urlLink = cfg.getString("host","unknown").toString()+"/orderList.php?t=2&token="+cfg.getString("token", "unknown");
        System.out.println(urlLink);
        HttpURLConnection con = (HttpURLConnection) new URL(urlLink).openConnection();
        con.setRequestProperty("Content-Type", "application/x-www-from-urlencoded");
        con.setConnectTimeout(5000);
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        try(OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream())){
            writer.write(requestBody);
        }

        if(con.getResponseCode() != 200) {
            System.err.println("connection failed");
            return Optional.empty();
        }

        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(con.getInputStream(), Charset.forName("utf-8")))){
            return Optional.of(reader.lines().collect(Collectors.joining(System.lineSeparator())));
        }
    }

    public void openOrderActivity(){
        Intent intent = new Intent(this, orders.class);
        startActivity(intent);
    }
    public Optional<String> getUserInfo(String requestBody) throws MalformedURLException, IOException {
        String t = "500";
        SharedPreferences cfg = PreferenceManager.getDefaultSharedPreferences(closedOrders.this);
        Editor cfgE = cfg.edit();
        String urlLink = cfg.getString("host","unknown").toString()+"/userInfo.php?token="+cfg.getString("token", "unkown");
        System.out.println(urlLink);
        HttpURLConnection con = (HttpURLConnection) new URL(urlLink).openConnection();
        con.setRequestProperty("Content-Type", "application/x-www-from-urlencoded");
        con.setConnectTimeout(5000);
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        try(OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream())){
            writer.write(requestBody);
        }

        if(con.getResponseCode() != 200) {
            System.err.println("connection failed");
            return Optional.empty();
        }

        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(con.getInputStream(), Charset.forName("utf-8")))){
            return Optional.of(reader.lines().collect(Collectors.joining(System.lineSeparator())));
        }
    }
}
