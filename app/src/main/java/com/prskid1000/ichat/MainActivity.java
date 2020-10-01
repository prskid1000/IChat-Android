package com.prskid1000.ichat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.ACCESS_NETWORK_STATE},102);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.INTERNET},103);
        }

        Button button = (findViewById(R.id.button2));
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle dataBundle = new Bundle();
                EditText editText=(EditText) (findViewById(R.id.editText3));
                final String userid=editText.getText().toString();
                dataBundle.putInt("id", 0);
                Intent intent = new Intent(getApplicationContext(), ChoiceScreen.class);
                intent.putExtras(dataBundle);
                intent.putExtra("userid",userid);
                startActivity(intent);
            }
        });
    }
}