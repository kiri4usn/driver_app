package com.example.auth;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.IconCompat;

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

public class BackgroundService extends Service {
    @Override
    public void onCreate(){



        Timer myTimer2;
        myTimer2 = new Timer();
        myTimer2.schedule(new TimerTask() {
            public void run() {
                try {
                    SharedPreferences cfg = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor cfgE = cfg.edit();
                    JSONObject json;
                    String result = getnotify(cfg.getString("user_id", "0").toString()).toString();
                    result = result.substring(result.indexOf(123), result.lastIndexOf("]"));
                    json = new JSONObject(result);
                    System.out.println("sdfsdf: "+cfg.getString("user_id", "0").toString());
                    JSONArray list = json.getJSONArray("Lines");
                    ArrayList<HashMap<String, String>> orders = new ArrayList();
                    System.out.println(json.toString());
                    int b = 0;
                    for (int i = 0; i < json.getJSONArray("Lines").length(); ++i) {

                        JSONObject notif = list.getJSONObject(i);
                        String count = "("+notif.getString("count")+") ";
                        noti(count+notif.getString("title"), notif.getString("description"), "TEST");
                    }

                    //orderList.setAdapter(adapter1);
                } catch (IOException | JSONException var15) {
                    var15.printStackTrace();
                }


            }
        }, 0, 5000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }




    public Optional<String> getnotify(String id) throws MalformedURLException, IOException {
        String t = "500";
        SharedPreferences cfg = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor cfgE = cfg.edit();
        String urlLink = cfg.getString("host","unknown").toString()+"/notifications.php?id="+id;
        System.out.println(urlLink);
        HttpURLConnection con = (HttpURLConnection) new URL(urlLink).openConnection();
        con.setRequestProperty("Content-Type", "application/x-www-from-urlencoded");
        con.setConnectTimeout(500);
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        try(OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream())){
            writer.write("");
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

    public void noti(String title, String text, String chan) throws MalformedURLException, IOException {
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(getApplicationContext(), orders.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
                    0, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            channel = new NotificationChannel(chan, "Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            channel.setVibrationPattern(new long[]{ 200,200,200,200 });
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this,chan)
                    .setContentTitle(title)
                    .setContentText(text)
                    //.setContentIntent(contentIntent)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setColor(R.color.black)
                    //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon)) // большая картинка
                    .setPriority(PRIORITY_HIGH)
                    .setVibrate(new long[]{ 200,200,200,200 })
                    .build();
            notificationManager.notify(42,notification);


        }
    }
}