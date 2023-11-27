package com.example.auth;

import android.content.Context;
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
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.auth.R.id;
import com.example.auth.R.layout;
import com.example.auth.settings;

public class settings extends AppCompatActivity {
    private Context context;

    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(1);
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setContentView(layout.settings);
        ThreadPolicy policy = (new Builder()).permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Button SaveButt = (Button)this.findViewById(id.userSettingsSave);
        EditText hostAddr = (EditText)this.findViewById(id.s);
        SharedPreferences cfg = PreferenceManager.getDefaultSharedPreferences(this);
        Editor cfgE = cfg.edit();
        hostAddr.setText(cfg.getString("host", "unkown").toString());
        SaveButt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openLoginActivity();
                    }
                });
    }

    public void openLoginActivity() {
        SharedPreferences cfg = PreferenceManager.getDefaultSharedPreferences(this);
        Editor cfgE = cfg.edit();
        EditText hostAddr = (EditText)this.findViewById(id.s);
        cfgE.putString("host", hostAddr.getText().toString());
        cfgE.commit();
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }
}
