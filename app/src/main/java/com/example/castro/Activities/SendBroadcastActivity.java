package com.example.castro.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.castro.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class SendBroadcastActivity extends AppCompatActivity {

    private final int GALLERY_REQUEST_CODE = 1;

    DatabaseReference mRef;
    private StorageReference mStorageRef;
    FirebaseAuth mAuth;

    Toolbar toolbar;
    EditText edtBroadcastMsg;
    ImageButton imgBSendMsg;
    ImageButton imgBGallery;

    public void init(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        edtBroadcastMsg = findViewById(R.id.edt_broadcast_msg);
        imgBSendMsg = findViewById(R.id.img_btn_send_msg);
        imgBGallery = findViewById(R.id.img_btn_gallery);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_broadcast);

        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgBSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = edtBroadcastMsg.getText().toString().trim();
                if(!msg.isEmpty()){
                    String pushKey = mRef.child("Broadcast").push().getKey();

                    String broadCastRef = "Broadcast/"+pushKey;

                    Map msgAddMap = new HashMap();
                    msgAddMap.put("message", msg);
                    msgAddMap.put("seen", false);
                    msgAddMap.put("type", "text");
                    msgAddMap.put("time", ServerValue.TIMESTAMP);
                    msgAddMap.put("from", "ADMIN");

                    Map msgUserMap = new HashMap();
                    msgUserMap.put(broadCastRef, msgAddMap);

                    mRef.updateChildren(msgUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Log.i("MESSAGE CHAT ERROR", databaseError.getMessage().toString());
                            }
                            else{
                                Toast.makeText(SendBroadcastActivity.this, "Message Sent!!!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    edtBroadcastMsg.setText("");
                }
            }
        });

        imgBGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            Uri sourceUri = data.getData();

            String pushKey = mRef.child("Broadcast").push().getKey();

            final String broadCastRef = "Broadcast/"+pushKey;

            final StorageReference f = mStorageRef.child("Broadcast_Images").child(pushKey+".jpg");

            f.putFile(sourceUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    f.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {

                            Map msgAddMap = new HashMap();
                            msgAddMap.put("message", uri.toString());
                            msgAddMap.put("seen", false);
                            msgAddMap.put("type", "image");
                            msgAddMap.put("time", ServerValue.TIMESTAMP);
                            msgAddMap.put("from", "ADMIN");

                            Map msgUserMap = new HashMap();
                            msgUserMap.put(broadCastRef, msgAddMap);


                            mRef.updateChildren(msgUserMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if(databaseError != null){
                                        Log.i("MESSAGE CHAT ERROR", databaseError.getMessage().toString());
                                    }
                                }
                            });

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(SendBroadcastActivity.this, "image upload unsuccessful", Toast.LENGTH_SHORT).show();
                    Log.i("Error, upload photo:", exception.getMessage().toString());
                    Log.i("Error Message,:", exception.getLocalizedMessage().toString());
                }
            });

        }
    }

}
