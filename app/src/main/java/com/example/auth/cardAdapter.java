package com.example.auth;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.auth.R.id;
import com.example.auth.R.layout;

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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class cardAdapter extends ArrayAdapter<ArrayList> {
    private Context context;
    private ArrayList<HashMap<String, String>> orders;

    public String Version = "0.0.21";
    public cardAdapter(Context context, ArrayList<HashMap<String, String>> orders) {
        super(context, layout.item, (ArrayList)orders);
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    public View getView(int position, @NonNull View contentView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layout.item, parent, false);
        HashMap<String, String> thisOrder = (HashMap)this.orders.get(position);
        Button orderN = (Button) view.findViewById(id.orderN);
        TextView addr = (TextView)view.findViewById(id.Addr);
        TextView summ = (TextView)view.findViewById(id.summ);
        TextView paytype = (TextView)view.findViewById(id.payType);
        TextView status = (TextView)view.findViewById(id.orderStatus);
        TextView time = (TextView)view.findViewById(id.timing);

        orderN.setText((String)thisOrder.get("orderN"));
        summ.setText(thisOrder.get("summ")+" ₽");
        paytype.setText(thisOrder.get("payType"));
        status.setText(thisOrder.get("status"));
        addr.setText(thisOrder.get("addr"));
        time.setText("Доставить ко времени: ⌚"+thisOrder.get("time"));


        if(Objects.equals(thisOrder.get("statusid"), "8")){
            orderN.setBackgroundResource(R.drawable.redbtt);
            orderN.setTextColor(R.color.black);
        } else {
            if(Objects.equals(thisOrder.get("statusid"), "9")){
                orderN.setBackgroundResource(R.drawable.btt);
            } else {
                orderN.setBackgroundResource(R.drawable.btt);
            }
        }

        LinearLayout card = (LinearLayout) view.findViewById(id.card);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent orderInfo = new Intent(context, OrderInfo.class);
                orderInfo.putExtra("id", (String)thisOrder.get("id"));
                orderInfo.putExtra("courier", (String)thisOrder.get("courier"));
                context.startActivity(orderInfo);
                 */
                showDiaolig(thisOrder);
            }
        });

        return view;
    }

    public void showDiaolig(HashMap<String,String> thisOrder){
        final Dialog dialog = new Dialog(this.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout.orderinfo);

        TextView courier = (TextView) dialog.findViewById(id.courier);
        courier.setText("Курьер: " + thisOrder.get("courier"));

        TextView payType = (TextView) dialog.findViewById(id.payType2);
        payType.setText("Тип оплаты: " + thisOrder.get("payType"));

        TextView phone = (TextView) dialog.findViewById(id.phone);
        phone.setText("Телефон клиента: " + thisOrder.get("phone"));

        Button setStatus1 = (Button)dialog.findViewById(id.status1);
        Button setStatus2 = (Button)dialog.findViewById(id.status2);
        Button setStatus3 = (Button)dialog.findViewById(id.status3);

        if(Objects.equals(thisOrder.get("statusid"), "8")){
            setStatus1.setBackgroundResource(R.drawable.statusbtt1);
            setStatus1.setTextColor(R.color.white);
        } else {
            if (Objects.equals(thisOrder.get("statusid"), "9")) {
                setStatus2.setBackgroundResource(R.drawable.statusbtt1);
                setStatus2.setTextColor(R.color.white);
            } else {
                if (Objects.equals(thisOrder.get("statusid"), "10")) {
                    setStatus3.setBackgroundResource(R.drawable.statusbtt1);
                    setStatus3.setTextColor(R.color.white);
                    setStatus1.setEnabled(false);
                    setStatus2.setEnabled(false);
                }
            }
        }

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);


        setStatus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context.getApplicationContext(),"Этот статус вы изменить не можете!",Toast.LENGTH_LONG).show();
                setStatus1.setBackgroundResource(R.drawable.statusbtt1);
                setStatus1.setTextColor(ContextCompat.getColor(context, R.color.black));
                setStatus2.setBackgroundResource(R.drawable.statusbtt2);
                setStatus2.setTextColor(ContextCompat.getColor(context, R.color.white));
                setStatus3.setBackgroundResource(R.drawable.statusbtt2);
                setStatus3.setTextColor(ContextCompat.getColor(context, R.color.white));
                if(!Objects.equals(thisOrder.get("statusid"),"10")) {
                    try {
                        setSatus(thisOrder.get("id").toString(), "", "8");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Toast.makeText(context.getApplicationContext(),"Вы не можете изменить статус этого заказа!",Toast.LENGTH_LONG).show();
                }
            }
        });

        setStatus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStatus1.setBackgroundResource(R.drawable.statusbtt2);
                setStatus1.setTextColor(ContextCompat.getColor(context, R.color.white));
                setStatus2.setBackgroundResource(R.drawable.statusbtt1);
                setStatus2.setTextColor(ContextCompat.getColor(context, R.color.black));
                setStatus3.setBackgroundResource(R.drawable.statusbtt2);
                setStatus3.setTextColor(ContextCompat.getColor(context, R.color.white));
                if(!Objects.equals(thisOrder.get("statusid"),"10")) {
                    try {
                        setSatus(thisOrder.get("id").toString(), "", "9");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Toast.makeText(context.getApplicationContext(),"Вы не можете изменить статус этого заказа!",Toast.LENGTH_LONG).show();
                }
            }
        });

        setStatus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context.getApplicationContext(),"Этот статус вы изменить не можете!",Toast.LENGTH_LONG).show();
                setStatus1.setBackgroundResource(R.drawable.statusbtt2);
                setStatus1.setTextColor(ContextCompat.getColor(context, R.color.white));
                setStatus2.setBackgroundResource(R.drawable.statusbtt2);
                setStatus2.setTextColor(ContextCompat.getColor(context, R.color.white));
                setStatus3.setBackgroundResource(R.drawable.statusbtt1);
                setStatus3.setTextColor(ContextCompat.getColor(context, R.color.black));
                try {
                    setSatus(thisOrder.get("id").toString(),"", "10");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            }
        });
    }

    public Optional<String> setSatus(String id, String requestBody, String status) throws MalformedURLException, IOException {
        String t = "500";
        SharedPreferences cfg = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor cfgE = cfg.edit();
        String urlLink = cfg.getString("host","unknown").toString()+"/setstatus.php?id="+id+"&status="+status;
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
            Toast.makeText(context.getApplicationContext(),"Нет соединения!",Toast.LENGTH_LONG).show();
            return Optional.empty();
        }

        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(con.getInputStream(), Charset.forName("utf-8")))){
            return Optional.of(reader.lines().collect(Collectors.joining(System.lineSeparator())));
        }
    }
}