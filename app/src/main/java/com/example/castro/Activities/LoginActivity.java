package com.example.castro.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.castro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    DatabaseReference mDatabaseReference;

    Toolbar toolbar;
    TextView tvSignup;
    ProgressDialog loginDialog;

    TextInputEditText edtEmail;
    TextInputEditText edtPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        loginDialog = new ProgressDialog(this);
        loginDialog.setTitle("loggin in");
        loginDialog.setCanceledOnTouchOutside(false);
        loginDialog.setMessage("authenticating user...please wait!");
        tvSignup = (TextView) findViewById(R.id.tv_signup);
        edtEmail = (TextInputEditText) findViewById(R.id.edt_email);
        edtPassword = (TextInputEditText) findViewById(R.id.edt_password);
        btnLogin = (Button) findViewById(R.id.btn_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iSignup = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(iSignup);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();

                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "All fields are required!!!", Toast.LENGTH_SHORT).show();
                }
                else{
                    loginDialog.show();
                    signin_user(email, password);
                }
            }
        });

    }

    private void signin_user(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String device_token = FirebaseInstanceId.getInstance().getToken();
                            mDatabaseReference.child(mAuth.getCurrentUser().getUid()).child("device_token").setValue(device_token).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        loginDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "User Login Successful", Toast.LENGTH_SHORT).show();
                                        Intent iOverview = new Intent(LoginActivity.this, OverviewActivity.class);
                                        startActivity(iOverview);
                                        finish();

                                    }
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            loginDialog.dismiss();
                            Log.i("Auth", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed. ",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

}
