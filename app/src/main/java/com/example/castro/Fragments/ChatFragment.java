package com.example.castro.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.castro.Activities.ChatActivity;
import com.example.castro.Activities.OverviewActivity;
import com.example.castro.Activities.ProfileActivity;
import com.example.castro.Adapters.UserRecyclerviewAdapter;
import com.example.castro.Models.Conv;
import com.example.castro.Models.Friends;
import com.example.castro.Models.UserModel;
import com.example.castro.R;
import com.example.castro.ViewHolders.ChatViewHolder;
import com.example.castro.ViewHolders.UserViewHolder;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    public ChatFragment(){}

    View v;
    RecyclerView rcv;
    FirebaseRecyclerAdapter adapter;

    FirebaseAuth mAuth;
    DatabaseReference mReference;
    DatabaseReference mReferenceUsers;
    DatabaseReference mReferencesMsg;
    DatabaseReference mReferenceChat;

    String name;
    String image;
    String userId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_chat, container, false);
        rcv = v.findViewById(R.id.chat_recyclerview);

        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();
        mReferenceUsers = mReference.child("Users");
        mReferencesMsg = mReference.child("Messages").child(mAuth.getCurrentUser().getUid());
        mReferenceChat = mReference.child("Chats").child(mAuth.getCurrentUser().getUid());

        mReference.keepSynced(true);
        mReferenceUsers.keepSynced(true);
        mReferencesMsg.keepSynced(true);
        mReferenceChat.keepSynced(true);

        LinearLayoutManager lMgr = new LinearLayoutManager(getContext());
        lMgr.setReverseLayout(true);
        lMgr.setStackFromEnd(true);

        rcv.hasFixedSize();
        rcv.setLayoutManager(lMgr);
        fetch_chats();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    public void onStop(){
        super.onStop();
        adapter.stopListening();
    }

    public void fetch_chats(){

        Query query = mReferenceChat.orderByChild("timestamp");

        FirebaseRecyclerOptions<Conv> options =
                new FirebaseRecyclerOptions.Builder<Conv>()
                        .setQuery(query, Conv.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Conv, ChatViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder h, int i, @NonNull final Conv model) {
                userId = null;
                userId = getRef(i).getKey();

                Query lastMsgQuery = mReferencesMsg.child(userId).limitToLast(1);

                final String user_id = userId;
                h.llMainRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mReferenceUsers.child(mAuth.getCurrentUser().getUid()).child("online").setValue("true");
                       // Toast.makeText(getContext(), user_id, Toast.LENGTH_SHORT).show();
                        Intent iSingle = new Intent(getContext(), ChatActivity.class);
                        iSingle.putExtra("user_id", user_id);
                        startActivity(iSingle);
                    }
                });

                lastMsgQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String data = dataSnapshot.child("message").getValue().toString();
                        String type = dataSnapshot.child("type").getValue().toString();

                        if(type.equals("image")){
                            h.tvMsg.setText("**image**");
                        }
                        else{
                            if(data.isEmpty() || data == null){h.tvMsg.setText("");}
                            h.tvMsg.setText(data);
                            if(model.isSeen()){
                                h.tvMsg.setTypeface(h.tvMsg.getTypeface(), Typeface.BOLD);
                            }
                            else{
                                h.tvMsg.setTypeface(h.tvMsg.getTypeface(), Typeface.NORMAL);
                            }
                        }

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                mReferenceUsers.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null){
                            //Log.i("aaa", dataSnapshot.toString());
                            name = dataSnapshot.child("fullname").getValue().toString();
                            image = dataSnapshot.child("thumbnail").getValue().toString();

                            h.tvName.setText(name);
                            Picasso.get().load(image).placeholder(R.drawable.user_avatar).into(h.imvProfile);

                            if(dataSnapshot.hasChild("online")){
                                String status = String.valueOf(dataSnapshot.child("online"));
                                if(status.equals("true")){
                                    h.tvLoginIcon.setVisibility(View.VISIBLE);
                                }

                            }

                        }
                        else{
                            Toast.makeText(getContext(), "No current chats", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_fragment, parent, false);
                return new ChatViewHolder(v);
            }
        };

        rcv.setAdapter(adapter);
    }




}
