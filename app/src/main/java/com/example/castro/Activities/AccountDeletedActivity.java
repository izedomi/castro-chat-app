package com.example.castro.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.castro.R;
import com.google.firebase.auth.FirebaseAuth;

public class AccountDeletedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_deleted);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(AccountDeletedActivity.this, LoginActivity.class);
        startActivity(i);
    }
}
