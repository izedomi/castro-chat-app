package com.example.castro.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.castro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class AccountSetupActivity extends AppCompatActivity {

    private final int GALLERY_REQUEST_CODE = 1;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private ProgressDialog setupDialog;
    private ProgressDialog  changeImageDialog;
    //private

    Toolbar toolbar;
    CircleImageView imvProfileImage;
    TextInputEditText tvFullname;
    TextInputEditText tvUsername;
    TextInputEditText tvDepartment;
    TextInputEditText tvRank;
    TextInputEditText tvGender;
    Button btnSaveProfile;

    String fullname;
    String username;
    String department;
    String rank;
    String gender;
    String downloadUrl = "default";
    String thumbnailDownloadUrl;
    int setupStatus;
    Uri imageUri;


    public void setup_dialog(){
        String title="";
        String message="";
        if(setupStatus == 0){
            title = "Account Setup";
            message = "setting up new user account...";
        }
        if(setupStatus == 1){
            title = "Profile Update";
            message = "updating user profile...";
        }
        setupDialog = new ProgressDialog(this);
        setupDialog.setTitle(title);
        setupDialog.setCanceledOnTouchOutside(false);
        setupDialog.setMessage(message);

        changeImageDialog = new ProgressDialog(this);
        changeImageDialog.setTitle("Change Profile Image");
        changeImageDialog.setCanceledOnTouchOutside(false);
        changeImageDialog.setMessage("uploading profile image...");
    }
    public void initialize_widget(){
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imvProfileImage = (CircleImageView) findViewById(R.id.image);
        tvFullname = (TextInputEditText) findViewById(R.id.edt_fullname);
        tvUsername = (TextInputEditText) findViewById(R.id.edt_username);
        tvDepartment = (TextInputEditText) findViewById(R.id.edt_department);
        tvRank = (TextInputEditText) findViewById(R.id.edt_rank);
        tvGender = (TextInputEditText) findViewById(R.id.edt_gender);
        btnSaveProfile = (Button) findViewById(R.id.btn_save_profile);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);
        setTitle("Profile Setup");

        initialize_widget();
        setSupportActionBar(toolbar);

        setupStatus = getIntent().getExtras().getInt("setup");
        setup_dialog();
        if(setupStatus == 1){set_initial_values();}


        //imageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.user_avatar);
        imvProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);

            }
        });

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullname = tvFullname.getText().toString().trim().toLowerCase();
                username = tvUsername.getText().toString().trim().toLowerCase();
                department = tvDepartment.getText().toString().trim().toLowerCase();
                rank = tvRank.getText().toString().trim().toLowerCase();
                gender = tvGender.getText().toString().trim().toLowerCase();

                if(fullname.isEmpty() || username.isEmpty() || department.isEmpty() || rank.isEmpty() || gender.isEmpty()){
                    Toast.makeText(AccountSetupActivity.this, "Please complete all fields!", Toast.LENGTH_SHORT).show();
                }
                else{
                    setupDialog.show();
                    save_user_profile_details();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            Uri sourceUri = data.getData();

            CropImage.activity(sourceUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                File filePath = new File(imageUri.getPath());
                changeImageDialog.show();
                save_user_profile_image(imageUri, filePath);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(setupStatus == 0){
            Intent iOverview = new Intent(AccountSetupActivity.this, LoginActivity.class);
            startActivity(iOverview);
            finish();
        }

    }

    public void set_initial_values(){
        DatabaseReference myRef = mDatabaseRef.child("Users").child(mAuth.getCurrentUser().getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    tvFullname.setText(dataSnapshot.child("fullname").getValue().toString());
                    tvUsername.setText(dataSnapshot.child("username").getValue().toString());
                    tvDepartment.setText(dataSnapshot.child("department").getValue().toString());
                    tvRank.setText(dataSnapshot.child("rank").getValue().toString());
                    tvGender.setText(dataSnapshot.child("gender").getValue().toString());
                    thumbnailDownloadUrl = dataSnapshot.child("thumbnail").getValue().toString();
                    if(dataSnapshot.child("thumbnail").getValue().toString().equals("default")){
                        imvProfileImage.setImageDrawable(getDrawable(R.drawable.user_avatar));
                        //imageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.user_avatar);
                    }else{

                        Picasso.get().load(thumbnailDownloadUrl)
                                .placeholder(R.drawable.user_avatar)
                                .error(R.drawable.user_avatar)
                                .into(imvProfileImage);
                    }
                }
                else{
                    Toast.makeText(AccountSetupActivity.this, "", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void create_and_upload_image_thumbnail(File filePath){

        Bitmap bitmap;
        final UploadTask uploadTask;
        final byte[] thumbNailByte;
        try{

            bitmap = new Compressor(this)
                    .setMaxHeight(200)
                    .setMaxWidth(200)
                    .setQuality(75)
                    .compressToBitmap(filePath);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            thumbNailByte = os.toByteArray();

            String userId = mAuth.getCurrentUser().getUid();
            SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmm");
            String dateString = formatter.format(new Date());
            final StorageReference thumb = mStorageRef.child("thumbs").child(dateString + ".jpg");
            uploadTask = thumb.putBytes(thumbNailByte);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                    thumb.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            String userId = mAuth.getCurrentUser().getUid();
                            final DatabaseReference userRef = mDatabaseRef.child("Users").child(userId);
                            userRef.child("thumbnail").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        //imvProfileImage.setImageURI(imgUri);
                                        Picasso.get().load(uri.toString())
                                                .placeholder(R.drawable.user_avatar)
                                                .error(R.drawable.user_avatar)
                                                .into(imvProfileImage);
                                        changeImageDialog.dismiss();
                                        Toast.makeText(AccountSetupActivity.this, "Profile Image Changed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                           // Toast.makeText(AccountSetupActivity.this, uri.toString(), Toast.LENGTH_SHORT).show();
                            //Log.i("TAGGGGG", uri.toString());
                        }
                    });

                }
            });

            //uploadTask = mStorageRef.putBytes(data);
            //return thumbnailDownloadUrl;
            //Log.i("TAGG", thumbnailDownloadUrl);
        }
        catch (Exception e){
            Toast.makeText(this, "unable to create thumb nail for image", Toast.LENGTH_SHORT).show();
        }

        //return thumbnailDownloadUrl;
    }


    public void save_user_profile_image(final Uri imgUri, final File filePath ){

        if(imgUri != null){
            String userId = mAuth.getCurrentUser().getUid();
            final StorageReference f = mStorageRef.child("Profile_Images").child(imgUri.getLastPathSegment());

            f.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    f.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            //dialog.dismiss()
                           // create_and_upload_image_thumbnail(filePath);

                            //Toast.makeText(AccountSetupActivity.this, thumbnailDownloadUrl, Toast.LENGTH_SHORT).show();

                           /* if(setupStatus == 0){
                                downloadUrl = uri.toString();
                                imvProfileImage.setImageURI(imgUri);
                                changeImageDialog.dismiss();
                                Toast.makeText(AccountSetupActivity.this, "Profile Image Changed", Toast.LENGTH_SHORT).show();
                            }*/
                           // if(setupStatus == 1){

                                Map profileImage = new HashMap<>();
                                profileImage.put("image_url", uri.toString());
                               // profileImage.put("thumbnail", thumbnailDownloadUrl);

                                String userId = mAuth.getCurrentUser().getUid();
                                final DatabaseReference userRef = mDatabaseRef.child("Users").child(userId);
                                userRef.updateChildren(profileImage).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            create_and_upload_image_thumbnail(filePath);

                                        }
                                    }
                                });


                           // }
                        }
                    });
                                   }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(AccountSetupActivity.this, "image upload unsuccessful", Toast.LENGTH_SHORT).show();
                    Log.i("Error, upload photo:", exception.getMessage().toString());
                    Log.i("Error Message,:", exception.getLocalizedMessage().toString());
                }
            });

        }else{
            Toast.makeText(this, "No Image selected!", Toast.LENGTH_SHORT).show();
        }


    }

    public void save_user_profile_details(){
        if(mAuth.getCurrentUser() != null){
            String userId = mAuth.getCurrentUser().getUid();
            DatabaseReference userRef = mDatabaseRef.child("Users").child(userId);
            Map userProfile = new HashMap();
            userProfile.put("fullname", fullname);
            userProfile.put("username", username);
            userProfile.put("rank", rank);
            userProfile.put("department", department);
            userProfile.put("gender", gender);
            userProfile.put("setup", "1");

            userRef.updateChildren(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        setupDialog.dismiss();
                        String msg = "Account Setup Successful";
                        if(setupStatus == 1){msg = "Profile updated successfully";}
                        Toast.makeText(AccountSetupActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Intent iOverview = new Intent(AccountSetupActivity.this, OverviewActivity.class);
                        startActivity(iOverview);
                    }
                }
            });
        }
    }

}
