package com.example.castro.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.castro.Models.UserModel;
import com.example.castro.R;
import com.example.castro.ViewHolders.UserViewHolder;

import java.util.ArrayList;

public class UserRecyclerviewAdapter extends RecyclerView.Adapter<UserViewHolder> {


    Context mCxt;
    int mResource;
    ArrayList<UserModel> mData = new ArrayList<>();



    public UserRecyclerviewAdapter(int resource, Context cxt, ArrayList<UserModel> data){
        mCxt = cxt;
        mData = data;
        mResource = resource;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mCxt).inflate(mResource, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        holder.imvProfile.setImageResource(mData.get(position).getUserImage());
        holder.tvMsg.setText(mData.get(position).getUserMsg());
        holder.tvName.setText(mData.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
