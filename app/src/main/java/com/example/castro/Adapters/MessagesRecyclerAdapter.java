package com.example.castro.Adapters;

import android.content.Context;
import android.graphics.Color;
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

import java.util.ArrayList;

public class MessagesRecyclerAdapter extends RecyclerView.Adapter<MessagesViewHolder> {


    private ArrayList<MessagesModel> mList = new ArrayList<>();
    Context mCxt;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public MessagesRecyclerAdapter(ArrayList<MessagesModel> mList, Context cxt) {
        this.mList = mList;
        this.mCxt = cxt;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_message, parent, false);

        return new MessagesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder h, int position) {

        String from = mList.get(position).getFrom();
        String msgText = mList.get(position).getMessage();
        Long msgTime = mList.get(position).getTime();


        TimeAgo timeAgo = new TimeAgo();
        String lastSeen = timeAgo.getTimeAgo(msgTime, mCxt);



        if(from.equals(mAuth.getCurrentUser().getUid())){
            h.tvMsgSenderText.setText(msgText);
            h.llSenderRow.setVisibility(View.VISIBLE);
            h.llReceiverRow.setVisibility(View.GONE);

        }
        else{
            h.tvMsgReceiverText.setText(msgText);
            h.llReceiverRow.setVisibility(View.VISIBLE);
            h.llSenderRow.setVisibility(View.GONE);

        }




    }


    @Override
    public int getItemCount() {
        return mList.size();
    }
}
