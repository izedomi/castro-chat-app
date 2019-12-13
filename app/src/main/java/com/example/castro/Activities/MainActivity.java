package com.example.castro.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.castro.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, OverviewActivity.class);
                //Intent i = new Intent(MainActivity.this, ChatActivity.class);
                //Intent i = new Intent(MainActivity.this, SignupActivity.class);
                //Intent i = new Intent(MainActivity.this, AccountSetupActivity.class);
                //i.putExtra("setup", 1);
                startActivity(i);
                finish();
            }
        }, 5000);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
