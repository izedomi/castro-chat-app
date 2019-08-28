package com.example.castro.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    ProgressDialog signupDialog;

    Toolbar toolbar;
    TextView tvLogin;
    TextInputEditText edtStaffId;
    TextInputEditText edtEmail;
    TextInputEditText edtPassword;
    TextInputEditText edtConfirmPassword;
    Button btnSignup;

    public void initialize_widgets(){

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        signupDialog = new ProgressDialog(this);
        signupDialog.setTitle("Sign Up");
        signupDialog.setMessage("creating new user account...please wait");
        signupDialog.setCanceledOnTouchOutside(false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        tvLogin = (TextView) findViewById(R.id.tv_login);
        edtStaffId = (TextInputEditText) findViewById(R.id.edt_staff_id);
        edtEmail = (TextInputEditText) findViewById(R.id.edt_email);
        edtPassword = (TextInputEditText) findViewById(R.id.edt_password);
        edtConfirmPassword = (TextInputEditText) findViewById(R.id.edt_confirm_password);
        btnSignup = (Button) findViewById(R.id.btn_signup);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setTitle("Create Account");

        initialize_widgets();
        setSupportActionBar(toolbar);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String staffId = edtStaffId.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String confirmPassword = edtConfirmPassword.getText().toString().trim();

                if(staffId.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
                    Toast.makeText(SignupActivity.this, "All fields are required!!!", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(password.equals(confirmPassword)){
                        signupDialog.show();
                        signup_user(email, password, staffId);
                    }
                    else{
                        Toast.makeText(SignupActivity.this, "Password do not match!!!", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iLogin = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(iLogin);
                finish();
            }
        });



    }

    private void signup_user(String email, String password, final String staffId) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Map<String, String> userDetails = new HashMap<>();
                            userDetails.put("device_token", FirebaseInstanceId.getInstance().getToken());
                            userDetails.put("staff_Id", staffId);
                            userDetails.put("fullname", "");
                            userDetails.put("username", "");
                            userDetails.put("image_url", "default");
                            userDetails.put("thumbnail", "default");
                            userDetails.put("rank", "");
                            userDetails.put("department", "");
                            userDetails.put("gender", "");
                            userDetails.put("setup", "0");

                            DatabaseReference userRef = dbRef.child("Users").child(mAuth.getCurrentUser().getUid());
                            userRef.setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>(){
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        signupDialog.dismiss();
                                        Toast.makeText(SignupActivity.this, "User Account Created Successfully", Toast.LENGTH_SHORT).show();
                                        Intent iSetup = new Intent(SignupActivity.this, AccountSetupActivity.class);
                                        iSetup.putExtra("setup", 0);
                                        startActivity(iSetup);
                                        finish();
                                    }

                                }
                            });


                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                             Log.i("SIGN UP ERROR", "createUserWithEmail:failure:", task.getException());
                            signupDialog.dismiss();
                            Toast.makeText(SignupActivity.this, "Failed to create new user account!.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                    }
                });
    }



}
