package com.example.castro.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.castro.Activities.AllUsersActivity;
import com.example.castro.Activities.ProfileActivity;
import com.example.castro.Adapters.RequestRecylerAdapter;
import com.example.castro.Adapters.UserRecyclerviewAdapter;
import com.example.castro.Models.AllUserModel;
import com.example.castro.Models.RequestsModel;
import com.example.castro.Models.UserModel;
import com.example.castro.R;
import com.example.castro.ViewHolders.AllUserViewHolder;
import com.example.castro.ViewHolders.RequestViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestsFragments extends Fragment {

    RecyclerView rcv;
    DatabaseReference mReference;
    FirebaseAuth mAuth;
    private RequestRecylerAdapter adapter;
    View v;

    ArrayList<AllUserModel> mData = new ArrayList<>();
    ArrayList<String> userKeysList = new ArrayList<>();

    public RequestsFragments(){}


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_requests, container, false);

        rcv = v.findViewById(R.id.request_recyclerview);
        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference().child("Requests");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Toast.makeText(getContext(), dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
               // Log.i("Friends Request: ", dataSnapshot.getValue().toString());
                mData.clear();
                //adapter.notifyDataSetChanged();
                if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())){
                    Log.i("REQUIST ID:",  "yes");

                    for(final DataSnapshot k :  dataSnapshot.child(mAuth.getCurrentUser().getUid()).getChildren()){

                        DatabaseReference mReferenceRequestType = mReference.child(mAuth.getCurrentUser().getUid()).child(k.getKey().toString());

                        mReferenceRequestType.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //mData.clear();
                                //adapter.notifyDataSetChanged();
                                if(dataSnapshot.getValue() != null){
                                    if(dataSnapshot.hasChild("request_type")){
                                        if(dataSnapshot.child("request_type").getValue().toString().equals("recieved")){
                                            Log.i("REQUEST ID", k.getKey().toString());
                                            DatabaseReference mRefUsers = FirebaseDatabase.getInstance()
                                                    .getReference()
                                                    .child("Users")
                                                    .child(k.getKey().toString());

                                            mRefUsers.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    //mData.clear();
                                                    //adapter.notifyDataSetChanged();
                                                    AllUserModel requests =  dataSnapshot.getValue(AllUserModel.class);
                                                    // mData.add(new AllUserModel(dataSnapshot.child("")))
                                                    mData.add(requests);
                                                    userKeysList.add(k.getKey().toString());
                                                    adapter.notifyDataSetChanged();
                                                    Log.i("REQUIST ID:",  dataSnapshot.getValue().toString());
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }

                                    }

                                }
                                else{
                                    mData.clear();
                                    userKeysList.clear();
                                    adapter.notifyDataSetChanged();
                                   // Toast.makeText(getActivity().getApplicationContext(), "No requests available", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        //mData.add(new RequestsModel(k.getKey().toString()));
                    }
                   // adapter.notifyDataSetChanged();


                }
                else{
                    Log.i("REQUEST ID:", "no does not exists");
                    //Toast.makeText(getContext(), "No Requests", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        rcv.hasFixedSize();
        rcv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RequestRecylerAdapter(getContext(), R.layout.row_request_fragment, mData, userKeysList);
        rcv.setAdapter(adapter);

        return v;
    }


    @Override
    public void onStop() {
        super.onStop();
        mData.clear();
        userKeysList.clear();
    }

    @Override
    public void onStart() {
        super.onStart();
        mData.clear();
        userKeysList.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        mData.clear();
        userKeysList.clear();
    }
}
