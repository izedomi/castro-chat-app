package com.example.castro.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.castro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText edtEmail;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        btnSend = (Button) findViewById(R.id.btn_send);

        setTitle("Reset Password");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();
                if(!email.isEmpty()){
                    send_password_reset_email(email);
                }
            }
        });


    }

    public void send_password_reset_email(String emailAddress){

        FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "A Reset Password Link has been sent to your email", Toast.LENGTH_SHORT).show();
                            Log.d("EMAIL STATUS", "Email sent.");
                            edtEmail.setText("");
                            Intent iLogin = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                            startActivity(iLogin);
                        }
                        else {
                            Toast.makeText(ForgotPasswordActivity.this, task.getException().getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
                            Log.d("EMAIL ERR0", task.getException().getLocalizedMessage().toString());
                            Log.d("EMAIL ERR1", task.getException().toString());
                        }
                    }
                });



    }
}
