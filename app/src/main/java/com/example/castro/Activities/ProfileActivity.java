package com.example.castro.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.castro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {


    ImageView imvProfileImage;
    TextView tvName;
    TextView tvStaffId;
    TextView tvGender;
    TextView tvEmail;
    TextView tvTitle;
    Button btnSendRequest;
    Button btnDeclineRequest;
    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    ProgressDialog mDialog;

    // 0 = not friends
    // 1 = sent request
    // 2 = recieved request
    // 3 = friends
    int currentStatus;
    String userId;
    int action;

    public void initialize_widgets(){
        imvProfileImage = (ImageView) findViewById(R.id.image);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvStaffId = (TextView) findViewById(R.id.tv_staff_id);
        tvGender = (TextView) findViewById(R.id.tv_gender);
        tvEmail = (TextView) findViewById(R.id.tv_email);
        btnSendRequest = (Button) findViewById(R.id.btn_send_request);
        btnDeclineRequest = (Button) findViewById(R.id.btn_decline_request);

        currentStatus = 0;

        mDialog = new ProgressDialog(this);
        mDialog.setTitle("Loading User Data");
        mDialog.setMessage("Please wait while we load user data");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Send Request");


        initialize_widgets();

        userId = getIntent().getExtras().getString("user_id");
        action = getIntent().getExtras().getInt("action");

        //Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();

        // display user details
        display_user_details();


        //button actions
        button_actions(btnSendRequest);

        //decline request
        decline_friend_request(btnDeclineRequest);

    }



    //group 1
    public void display_user_details(){
        if(action == 1){
            btnSendRequest.setVisibility(View.GONE);
            btnDeclineRequest.setVisibility(View.GONE);
        }
        DatabaseReference mDatabaseUser = mDatabaseReference.child("Users").child(userId);
        mDatabaseUser.keepSynced(true);
        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvName.setText(dataSnapshot.child("fullname").getValue().toString());
                tvStaffId.setText(dataSnapshot.child("staff_Id").getValue().toString());
                tvTitle.setText(
                        dataSnapshot.child("department").getValue().toString() + " | "
                                + dataSnapshot.child("rank").getValue().toString());
                final String imageUrl = dataSnapshot.child("image_url").getValue().toString();
                //tvEmail.setText(mAuth.getCurrentUser().getEmail().toString());
                tvGender.setText(dataSnapshot.child("gender").getValue().toString());

                Picasso.get().load(imageUrl)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.user_avatar)
                        .error(R.drawable.user_avatar)
                        .into(imvProfileImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(imageUrl)
                                        .placeholder(R.drawable.user_avatar)
                                        .error(R.drawable.user_avatar)
                                        .into(imvProfileImage);
                            }
                        });
               /* Glide.with(ProfileActivity.this)
                        .load(dataSnapshot.child("image_url").getValue().toString())
                        .centerCrop()
                        .placeholder(R.drawable.user_avatar)
                        .fallback(R.drawable.user_avatar)
                        .into(imvProfileImage);
                        */
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Error retrieving user details...", Toast.LENGTH_SHORT).show();
            }
        });

        determine_friend_status();

    }
    public void determine_friend_status(){
        final DatabaseReference mDatabaseRequest = mDatabaseReference.child("Requests");
        final DatabaseReference mDatabaseFriends = mDatabaseReference.child("Friends");
        mDatabaseRequest.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(action == 0){
                    if(dataSnapshot.hasChild(userId)){
                        String requestType = dataSnapshot.child(userId).child("request_type").getValue().toString();
                        if(requestType.equals("sent")){
                            currentStatus = 1;
                            btnSendRequest.setEnabled(true);
                            btnSendRequest.setText("CANCEL FRIEND REQUEST");
                        }
                        else if(requestType.equals("recieved")){
                            currentStatus = 2;
                            btnSendRequest.setEnabled(true);
                            btnSendRequest.setText("Accept Friend Request");
                            btnDeclineRequest.setVisibility(View.VISIBLE);
                        }
                    }
                    else{
                        mDatabaseFriends.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(userId)){
                                    currentStatus = 3;
                                    btnSendRequest.setText("Unfriend User");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDialog.dismiss();

    }
    public void button_actions(final Button btnSendRequest){
        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSendRequest.setEnabled(false);

                //not frineds
                if(currentStatus==0){
                    //send friend request
                    send_friend_request();
                }
                //I sent a friend request to user
                if(currentStatus==1){
                    //cancel friend request
                    cancel_friend_request();
                }
                //user sent a friend request to me
                if(currentStatus==2){
                    //accept friend request
                    accept_friend_request();
                }
                //friends
                if(currentStatus==3){
                    //unfriend user
                    unfriend_user();
                }
            }
        });
    }


    //group 2
    public void is_user_my_friend(){
        //check if I have friends
        Toast.makeText(ProfileActivity.this, "called function: is friend", Toast.LENGTH_SHORT).show();

    }
    public void is_friend_request_sent(){
        //check if I have atleast an active sent friend request

        DatabaseReference mDatabaseRequest = mDatabaseReference.child("Request");
        mDatabaseRequest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //yes I have atleast one sent friend request:
                if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())){
                    //check if friend request was sent to user
                    Toast.makeText(ProfileActivity.this, "function called: sent friend request", Toast.LENGTH_SHORT).show();
                    DatabaseReference mDatabaseSentRequest = mDatabaseReference.child("Request").child(mAuth.getCurrentUser().getUid());
                    mDatabaseSentRequest.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(userId)){
                                currentStatus = 1;
                                btnSendRequest.setText("Cancel Friend Request");
                            }
                            else{
                                //check if I have recieved a friend request from user
                                has_user_sent_friend_request();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    //check if I have recieved a friend request from user
                    Toast.makeText(ProfileActivity.this, "function called: recieved", Toast.LENGTH_SHORT).show();
                    has_user_sent_friend_request();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void has_user_sent_friend_request(){
        //check if user have atleast an active sent friend request
        DatabaseReference mDatabaseRequest = mDatabaseReference.child("Request");
        mDatabaseRequest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //yes User have atleast one  friend request:
                if(dataSnapshot.hasChild(userId)){
                    //check if friend request was sent to me

                    DatabaseReference mDatabaseSentRequest = mDatabaseReference.child("Request").child(userId);
                    mDatabaseSentRequest.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())){
                                currentStatus = 2;
                                btnSendRequest.setText("Accept Friend Request");
                                btnDeclineRequest.setVisibility(View.VISIBLE);
                            }
                            else{
                                currentStatus = 0;
                                btnSendRequest.setText("Send Friend Request");
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    currentStatus = 0;
                    btnSendRequest.setText("Send Friend Request");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //group 3
    public void send_friend_request(){
        final DatabaseReference mDatabaseRequest = mDatabaseReference.child("Requests");
        final DatabaseReference mDatabaseNoty = mDatabaseReference.child("Notifications");
        mDatabaseRequest.child(mAuth.getCurrentUser().getUid()).child(userId).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mDatabaseRequest.child(userId).child(mAuth.getCurrentUser().getUid()).child("request_type").setValue("recieved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                HashMap<String, String> notification = new HashMap<>();
                                notification.put("from", mAuth.getCurrentUser().getUid());
                                notification.put("type", "request");

                                mDatabaseNoty.child(userId).push().setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            currentStatus = 1;
                                            btnSendRequest.setText("Cancel Friend Request");
                                            btnSendRequest.setEnabled(true);
                                            Toast.makeText(ProfileActivity.this, "Request Sent Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                            else{
                                
                            }
                        }
                    });

                }else{
                    Toast.makeText(ProfileActivity.this, "Failed Sending Request", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void cancel_friend_request(){
        final DatabaseReference mDatabaseRequest = mDatabaseReference.child("Requests");
        mDatabaseRequest.child(mAuth.getCurrentUser().getUid()).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    mDatabaseRequest.child(userId).child(mAuth.getCurrentUser().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                             currentStatus = 0;
                             btnSendRequest.setText("Send Friend Request");
                            btnSendRequest.setEnabled(true);
                            Toast.makeText(ProfileActivity.this, "Friend Request Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Toast.makeText(ProfileActivity.this, "Cancel Request Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void accept_friend_request(){
        final DatabaseReference mDatabaseRequest = mDatabaseReference.child("Requests");
        final DatabaseReference mDatabaseFriends = mDatabaseReference.child("Friends");

            final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
            mDatabaseFriends.child(mAuth.getCurrentUser().getUid()).child(userId).child("date").setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        mDatabaseFriends.child(userId).child(mAuth.getCurrentUser().getUid()).child("date").setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    mDatabaseRequest.child(mAuth.getCurrentUser().getUid()).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){

                                                mDatabaseRequest.child(userId).child(mAuth.getCurrentUser().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        currentStatus = 3;
                                                        btnSendRequest.setText("Unfriend User");
                                                        btnDeclineRequest.setVisibility(View.GONE);
                                                        Toast.makeText(ProfileActivity.this, "Friend Request Accepted", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    });

                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(ProfileActivity.this, "couldn't accept request..please try again", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        btnSendRequest.setEnabled(true);
    }
    public void decline_friend_request( final Button btnDeclineRequest){
        btnDeclineRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnDeclineRequest.setEnabled(false);
                final DatabaseReference mDatabaseRequest = mDatabaseReference.child("Requests");
                mDatabaseRequest.child(mAuth.getCurrentUser().getUid()).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            mDatabaseRequest.child(userId).child(mAuth.getCurrentUser().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    currentStatus = 0;
                                    btnSendRequest.setText("Send Friend Request");
                                    btnSendRequest.setEnabled(true);
                                    btnDeclineRequest.setVisibility(View.GONE);
                                    Toast.makeText(ProfileActivity.this, "Friend Request Declined", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else{
                            Toast.makeText(ProfileActivity.this, "Failed to Decline Request", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    public void unfriend_user(){
        final DatabaseReference mDatabaseFriends = mDatabaseReference.child("Friends");
        mDatabaseFriends.child(mAuth.getCurrentUser().getUid()).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mDatabaseFriends.child(userId).child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                currentStatus = 0;
                                btnSendRequest.setText("Send Friend Request");
                                btnSendRequest.setEnabled(true);
                            }
                        }
                    });
                }
            }
        });
    }

}
