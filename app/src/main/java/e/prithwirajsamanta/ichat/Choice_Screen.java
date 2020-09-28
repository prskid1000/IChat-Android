package e.prithwirajsamanta.ichat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Choice_Screen extends Activity {
    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_choice__screen);

            final String userid=getIntent().getExtras().getString("userid");
            downloadJSON("http://progwithme.dx.am/app/get_contact.php?userid="+userid,userid);

    }
    private void downloadJSON(final String urlWebService,final String userid) {

        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    guiBuild(userid,s);
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

    private void guiBuild(final String userid,final String result) {

        LinearLayout cl=(LinearLayout) (findViewById(R.id.choice));
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);
        LinearLayout.LayoutParams paramsb=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsb.setMargins(10,10,10,10);

        final EditText etext = new EditText(this);
        etext.setLayoutParams(params);
        etext.setHint("New Contact");
        etext.setTextSize(21);
        cl.addView(etext);

        Button btn1=new Button(this);
        btn1.setLayoutParams(paramsb);
        btn1.setPadding(2,2,2,2);
        btn1.setText("Create");
        btn1.setBackgroundColor(Color.parseColor("#4CAF50"));
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data=etext.getText().toString();
                downloadJSON("http://progwithme.dx.am/app/add_contact.php?userid="+userid+"&receiver="+etext.getText().toString(),userid);
                finish();
                startActivity(getIntent());
            }
        });
        cl.addView(btn1);

        Scanner sc=new Scanner(result);
        String token=null;
         while((token=sc.next())!=null){
                final TextView text = new TextView(this);
                text.setLayoutParams(params);
                text.setText(token);
                text.setBackgroundColor(Color.parseColor("#18641B"));
                text.setTextSize(24);
                text.setTextColor(-1);
                text.setPadding(1, 20, 1, 20);
                text.setGravity(Gravity.CENTER_HORIZONTAL);
                text.setClickable(true);
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Opening Chat for this Contact", Toast.LENGTH_LONG); // initiate the Toast with context, message and duration for the Toast
                        toast.show();
                        Bundle dataBundle = new Bundle();
                        dataBundle.putInt("id", 0);
                        Intent intent = new Intent(getApplicationContext(), Chat.class);
                        intent.putExtras(dataBundle);
                        intent.putExtra("userid",userid);
                        intent.putExtra("receiver",text.getText().toString());
                        startActivity(intent);
                    }
                });
                cl.addView(text);
                Button btn = new Button(this);
                btn.setText("Delete");
                btn.setBackgroundColor(Color.parseColor("#4CAF50"));
                btn.setLayoutParams(paramsb);
                btn.setPadding(2, 2, 2, 2);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        downloadJSON("http://progwithme.dx.am/app/remove_contact.php?userid="+userid+"&receiver="+text.getText().toString(),userid);
                        finish();
                        startActivity(getIntent());
                    }
                });
                cl.addView(btn);
        }
        sc.close();
    }
}
