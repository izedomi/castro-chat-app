package com.example.castro.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.castro.Activities.TimeAgo;
import com.example.castro.Models.MessagesModel;
import com.example.castro.R;
import com.example.castro.ViewHolders.MessagesViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessagesRecyclerAdapter extends RecyclerView.Adapter<MessagesViewHolder> {


    private ArrayList<MessagesModel> mList = new ArrayList<>();
    Context mCxt;
    boolean status;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    public MessagesRecyclerAdapter(ArrayList<MessagesModel> mList, Context cxt, boolean s) {
        this.mList = mList;
        this.mCxt = cxt;
        this.status = s;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_message, parent, false);

        return new MessagesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final @NonNull MessagesViewHolder h, int position) {

        String from = mList.get(position).getFrom();
        String msgText = mList.get(position).getMessage();
        Long msgTime = mList.get(position).getTime();
        String msgType = mList.get(position).getType();

        TimeAgo timeAgo = new TimeAgo();
        String lastSeen = timeAgo.getTimeAgo(msgTime, mCxt);

        if(!from.equals("ADMIN")){
            mRef.child("Users").child(from).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null){
                       // Log.i("THUMB", dataSnapshot.toString());

                        if(dataSnapshot.child("thumbnail").getValue().toString() != null){
                        String thumbnail = dataSnapshot.child("thumbnail").getValue().toString();
                        //tvToolbarName.setText(dataSnapshot.child("fullname").getValue().toString());
                        Picasso.get().load(thumbnail)
                                .placeholder(R.drawable.user_avatar)
                                .error(R.drawable.user_avatar)
                                .into(h.imvMsgReceiver);
                        };

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        //if status is true, chat activity is active
        if(status){
            if(msgType.equals("text")){
                if(from.equals(mAuth.getCurrentUser().getUid())){
                    h.tvMsgSenderText.setVisibility(View.VISIBLE);
                    h.tvMsgSenderText.setText(msgText);
                    h.imvImgSent.setVisibility(View.GONE);
                    h.llSenderRow.setVisibility(View.VISIBLE);
                    h.llReceiverRow.setVisibility(View.GONE);

                }
                else{
                    h.tvMsgReceiverText.setVisibility(View.VISIBLE);
                    h.tvMsgReceiverText.setText(msgText);
                    h.imvImgReceived.setVisibility(View.GONE);
                    h.llReceiverRow.setVisibility(View.VISIBLE);
                    h.llSenderRow.setVisibility(View.GONE);

                }
            }
            else{
                if(from.equals(mAuth.getCurrentUser().getUid())){
                    h.tvMsgSenderText.setVisibility(View.GONE);
                    h.imvImgSent.setVisibility(View.VISIBLE);
                    Picasso.get().load(msgText).placeholder(R.drawable.user_avatar).into(h.imvImgSent);
                    h.llSenderRow.setVisibility(View.VISIBLE);
                    h.llReceiverRow.setVisibility(View.GONE);

                }
                else{
                    h.tvMsgReceiverText.setVisibility(View.GONE);
                    h.imvImgReceived.setVisibility(View.VISIBLE);
                    Picasso.get().load(msgText).placeholder(R.drawable.user_avatar).into(h.imvImgReceived);
                    h.llReceiverRow.setVisibility(View.VISIBLE);
                    h.llSenderRow.setVisibility(View.GONE);

                }
            }

        }
        //if status is false, broadcast activity is active
        if(!status){
            if(msgType.equals("text")){
                if(from.equals("ADMIN")){
                    h.tvMsgSenderText.setVisibility(View.VISIBLE);
                    h.tvMsgSenderText.setText(msgText);
                    h.imvImgSent.setVisibility(View.GONE);
                    h.llSenderRow.setVisibility(View.VISIBLE);
                    h.llReceiverRow.setVisibility(View.GONE);

                }
                else{
                    h.tvMsgReceiverText.setVisibility(View.VISIBLE);
                    h.tvMsgReceiverText.setText(msgText);
                    h.imvImgReceived.setVisibility(View.GONE);
                    h.llReceiverRow.setVisibility(View.VISIBLE);
                    h.llSenderRow.setVisibility(View.GONE);

                }
            }
            else{
                if(from.equals("ADMIN")){
                    h.tvMsgSenderText.setVisibility(View.GONE);
                    h.imvImgSent.setVisibility(View.VISIBLE);
                    Picasso.get().load(msgText).placeholder(R.drawable.user_avatar).error(R.drawable.user_avatar).into(h.imvImgSent);
                    h.llSenderRow.setVisibility(View.VISIBLE);
                    h.llReceiverRow.setVisibility(View.GONE);

                }
                else{
                    h.tvMsgReceiverText.setVisibility(View.GONE);
                    h.imvImgReceived.setVisibility(View.VISIBLE);
                    Picasso.get().load(msgText).placeholder(R.drawable.user_avatar).error(R.drawable.user_avatar).into(h.imvImgReceived);
                    h.llReceiverRow.setVisibility(View.VISIBLE);
                    h.llSenderRow.setVisibility(View.GONE);

                }
            }

        }

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }
}
