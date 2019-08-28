package com.example.castro.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.castro.Activities.ProfileActivity;
import com.example.castro.Models.AllUserModel;
import com.example.castro.Models.RequestsModel;
import com.example.castro.Models.UserModel;
import com.example.castro.R;
import com.example.castro.ViewHolders.RequestViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RequestRecylerAdapter extends RecyclerView.Adapter<RequestViewHolder> {


    Context mCxt;
    int mResource;
    ArrayList<AllUserModel> mData = new ArrayList<>();
    ArrayList<String> userKeysList = new ArrayList<>();

    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    public RequestRecylerAdapter(Context mCxt, int mResource, ArrayList<AllUserModel> mData, ArrayList<String> userKeysList) {
        this.mCxt = mCxt;
        this.mResource = mResource;
        this.mData = mData;
        this.userKeysList = userKeysList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mCxt).inflate(mResource, parent, false);
        return new RequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, final int position) {

        holder.tvName.setText(mData.get(position).getFullname());
        holder.tvInfo.setText(mData.get(position).getDepartment() + " | " + mData.get(position).getRank());
        Picasso.get().load(mData.get(position).getImage_url()).placeholder(R.drawable.user_avatar).into(holder.imvProfile);

        //Toast.makeText(mCxt, userKeysList.get(position), Toast.LENGTH_SHORT).show();

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accept_friend_request(userKeysList.get(position));
            }
        });

        holder.btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decline_friend_request(userKeysList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public void accept_friend_request(final String userId){
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

                                                    Toast.makeText(mCxt, "Friend Request Accepted", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(mCxt, "couldn't accept request..please try again", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    public void decline_friend_request(final String userId){

        final DatabaseReference mDatabaseRequest = mDatabaseReference.child("Requests");
        mDatabaseRequest.child(mAuth.getCurrentUser().getUid()).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    mDatabaseRequest.child(userId).child(mAuth.getCurrentUser().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(mCxt, "Friend Request Declined", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Toast.makeText(mCxt, "Failed to Decline Request", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
