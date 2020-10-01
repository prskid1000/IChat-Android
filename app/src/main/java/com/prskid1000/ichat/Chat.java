package com.prskid1000.ichat;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Chat extends AppCompatActivity {

    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().hide();

        LinearLayout cl=(LinearLayout) (findViewById(R.id.chat));
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams paramsb=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(100,10,100,10);
        params.height=200;

        final String userid=getIntent().getExtras().getString("userid");
        final String receiver=getIntent().getExtras().getString("receiver");

        final EditText etext = new EditText(this);
        etext.setLayoutParams(params);
        etext.setHint("New Message");
        etext.setTextSize(20);
        cl.addView(etext);

        Button btn1=new Button(this);
        LinearLayout.LayoutParams paramsb1=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsb1.setMargins(200,5,200,10);
        btn1.setLayoutParams(paramsb1);
        btn1.setPadding(1,1,1,1);
        btn1.setText("Refresh");
        btn1.setTextColor(-1);
        btn1.setBackgroundColor(Color.parseColor("#e5335c"));

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(getIntent());
            }
        });
        cl.addView(btn1);

        Button btn=new Button(this);
        paramsb1.setMargins(200,5,200,10);
        btn.setLayoutParams(paramsb1);
        btn.setPadding(1,1,1,1);
        btn.setText("Send");
        btn.setTextColor(-1);
        btn.setBackgroundColor(Color.parseColor("#e5335c"));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data=etext.getText().toString();
                downloadJSON("http://progwithme.dx.am/app/send_message.php?userid="+userid+"&receiver="+receiver+"&data="+etext.getText().toString().replace(' ','~'));
                finish();
                startActivity(getIntent());
            }
        });
        cl.addView(btn);

        downloadJSON("http://progwithme.dx.am/app/receive_message.php?userid="+userid+"&receiver="+receiver);
    }
    private void downloadJSON(final String urlWebService) {

        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    sendreceive(s);
                } catch (Exception e) {

                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {

                    return null;
                }
            }
        }
        DownloadJSON getJSON = new DownloadJSON();
        getJSON.execute();
    }

    private void sendreceive(final String s)
    {

        LinearLayout cl=(LinearLayout) (findViewById(R.id.chat));
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams paramsb=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(300,10,100,10);
        paramsb.setMargins(100,10,300,10);

        final String userid=getIntent().getExtras().getString("userid");
        final String receiver=getIntent().getExtras().getString("receiver");

        Scanner sc=new Scanner(s);
        String temp=null;
        boolean f=true;

        while(sc.hasNext()) {
            temp=sc.next();
            if(temp.compareTo("list")==0)continue;
            if(temp.compareToIgnoreCase("&")==0)
            {
                f=false;
                continue;
            }
            if(f) {
                TextView text = new TextView(this);
                text.setPadding(10, 20, 10, 20);
                text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                text.setLayoutParams(params);
                text.setText(userid.toUpperCase() + ":\n" + temp.replace('~',' '));
                text.setTextSize(22);
                text.setGravity(Gravity.RIGHT);
                text.setBackgroundColor(Color.parseColor("#a6f3e3"));
                text.setTextColor(Color.parseColor("#073128"));
                text.setClickable(false);
                cl.addView(text);
            }else{
                TextView text = new TextView(this);
                text.setPadding(10, 20, 10, 20);
                text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                text.setLayoutParams(paramsb);
                text.setText(receiver.toUpperCase() +":\n" + temp.replace('~',' '));
                text.setTextSize(22);
                text.setGravity(Gravity.LEFT);
                text.setBackgroundColor(Color.parseColor("#a6f3bd"));
                text.setTextColor(Color.parseColor("#073128"));
                text.setClickable(false);
                cl.addView(text);
            }
        }

        sc.close();
    }
}