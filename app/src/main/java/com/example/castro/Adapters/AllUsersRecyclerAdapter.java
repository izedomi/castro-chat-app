package com.example.castro.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.castro.Activities.AllUsersActivity;
import com.example.castro.Activities.ProfileActivity;
import com.example.castro.Models.AllUserModel;
import com.example.castro.R;
import com.example.castro.ViewHolders.AllUserViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllUsersRecyclerAdapter extends RecyclerView.Adapter<AllUserViewHolder> {

    ArrayList<AllUserModel> mList = new ArrayList<>();
    ArrayList<String> mKeys = new ArrayList<>();
    Context mCxt;

    public AllUsersRecyclerAdapter(Context mcxt, ArrayList<AllUserModel> mlist, ArrayList<String> mkeys) {
        this.mList = mlist;
        this.mCxt = mcxt;
        this.mKeys = mkeys;
    }

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mRefUsers = FirebaseDatabase.getInstance().getReference().child("Users");
    DatabaseReference mRefChat = FirebaseDatabase.getInstance().getReference().child("Chats");
    DatabaseReference mRefFriends = FirebaseDatabase.getInstance().getReference().child("Friends");
    DatabaseReference mRefMessages = FirebaseDatabase.getInstance().getReference().child("Messages");
    DatabaseReference mRefRequests = FirebaseDatabase.getInstance().getReference().child("Requests");

    @NonNull
    @Override
    public AllUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mCxt).inflate(R.layout.row_all_user, parent, false);
        return new AllUserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AllUserViewHolder h, int position) {

        Log.i("d", String.valueOf(this.mList.size()));
        Log.i("dd", String.valueOf(this.mKeys.size()));


        //Log.i("ddd", mList.get(position).getFullname());

        //final String userId = "MO2NYTrnzxcXdBziTac0iTXbr0K2";
        final String userId = mKeys.get(position);
       // position = position - 1;

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
          //  if((userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) || (!mList.get(position).getSetup().equals("1")) ){
             //   h.llMainRow.setVisibility(View.GONE);
           // }
            //else{
                h.llMainRow.setVisibility(View.VISIBLE);
                h.tvName.setText(mList.get(position).getFullname());
                h.tvDepartment.setText(mList.get(position).getDepartment() + " | " + mList.get(position).getRank());
                Glide
                        .with(mCxt)
                        .load(mList.get(position).getImage_url())
                        .centerCrop()
                        .placeholder(R.drawable.user_avatar)
                        .fallback(R.drawable.user_avatar)
                        .into(h.imvProfile);


                h.llMainRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence[] options = new CharSequence[]{"Open Profile"};
                        if(mAuth.getCurrentUser().getEmail().equals("ema@gmail.com")){
                            options = new CharSequence[]{"Open Profile", "Delete User"};
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(mCxt);
                        builder.setTitle("Select Option");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                switch (i){
                                    case 0:
                                        Intent iProfile = new Intent(mCxt, ProfileActivity.class);
                                        iProfile.putExtra("user_id", userId);
                                        iProfile.putExtra("action", 0);
                                        mCxt.startActivity(iProfile);
                                        break;
                                    case 1:
                                        delete_user(userId);

                                         /*  Intent iSingle = new Intent(AllUsersActivity.this, ChatActivity.class);
                                            iSingle.putExtra("user_id", userId);
                                            startActivity(iSingle);
                                            */
                                                    break;
                                            }

                                        }
                                    });
                                    builder.show();

                                }
                            });

                   // }

               }


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setFilter(ArrayList<AllUserModel> newList, ArrayList<String> newKeys){
        mList = new ArrayList<>();
        mKeys = new ArrayList<>();
        mKeys.addAll(newKeys);
        mList.addAll(newList);
        notifyDataSetChanged();
    }

    public void delete_user(final String userId){
        delete_from_chats(userId);
        delete_from_requests(userId);
        delete_from_messages(userId);
        delete_from_friends(userId);
        delete_from_users(userId);

    }

    public void delete_from_chats(final String userId){
        mRefChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot d: dataSnapshot.getChildren()){

                    Log.i("dataDDDc", d.getKey().toString());
                    Log.i("dataDDc", d.getValue().toString());
                    // Log.i("dataDD", d.getChildren().toString());

                    for(DataSnapshot e: d.getChildren()){
                        if(e.getKey().toString().equals(userId)){
                            Log.i("dataEEEc", e.getKey().toString());
                            Log.i("dataEEc", e.getValue().toString());
                            mRefChat.child(d.getKey()).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });

                        }
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRefChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(userId)){
                    mRefChat.child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            }
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void delete_from_requests(final String userId){
        mRefRequests.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot d: dataSnapshot.getChildren()){

                    Log.i("dataDDDr", d.getKey().toString());
                    Log.i("dataDDr", d.getValue().toString());
                    // Log.i("dataDD", d.getChildren().toString());

                    for(DataSnapshot e: d.getChildren()){
                        if(e.getKey().toString().equals(userId)){
                            Log.i("dataEEEr", e.getKey().toString());
                            Log.i("dataEEr", e.getValue().toString());
                            mRefRequests.child(d.getKey()).child(e.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });

                        }
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRefRequests.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(userId)){
                    mRefRequests.child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            }
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void delete_from_messages(final String userId){
        mRefMessages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot d: dataSnapshot.getChildren()){

                    Log.i("dataDDDm", d.getKey().toString());
                    Log.i("dataDDm", d.getValue().toString());
                    // Log.i("dataDD", d.getChildren().toString());

                    for(DataSnapshot e: d.getChildren()){
                        if(e.getKey().toString().equals(userId)){
                            Log.i("dataEEEm", e.getKey().toString());
                            Log.i("dataEEm", e.getValue().toString());
                            mRefMessages.child(d.getKey()).child(e.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });

                        }
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRefMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(userId)){
                    mRefMessages.child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            }
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void delete_from_friends(final String userId){
        mRefFriends.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot d: dataSnapshot.getChildren()){

                    Log.i("dataDDDf", d.getKey().toString());
                    Log.i("dataDDf", d.getValue().toString());
                    // Log.i("dataDD", d.getChildren().toString());

                    for(DataSnapshot e: d.getChildren()){
                        if(e.getKey().toString().equals(userId)){
                            Log.i("dataEEEf", e.getKey().toString());
                            Log.i("dataEEf", e.getValue().toString());
                            mRefFriends.child(d.getKey()).child(e.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });

                        }
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRefFriends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(userId)){
                    mRefFriends.child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            }
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void delete_from_users(final String userId){
        mRefUsers.child(userId).child("setup").setValue("2").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(mCxt, "user deleted successfully", Toast.LENGTH_SHORT).show();

                    //refresh current activity
                    ((Activity)mCxt).finish();
                    ((Activity)mCxt).getIntent();
                }
            }
        });
    }

}
