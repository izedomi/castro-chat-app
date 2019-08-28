package com.example.castro.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.castro.Activities.ProfileActivity;
import com.example.castro.Activities.ChatActivity;
import com.example.castro.Models.Friends;
import com.example.castro.Models.UserModel;
import com.example.castro.R;
import com.example.castro.ViewHolders.UserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
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

public class FriendsFragment extends Fragment {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();

    String userId;
    String fullname;
    String imageUrl;

    public FriendsFragment(){}

    View v;

    RecyclerView rcv;
    ArrayList<UserModel> userData = new ArrayList<>();
    FirebaseRecyclerAdapter adapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_friends, container, false);

        rcv = v.findViewById(R.id.friend_recyclerview);

        rcv.hasFixedSize();
        rcv.setLayoutManager(new LinearLayoutManager(getContext()));
        fetch_friends();

        return v;
    }

    public void fetch_friends(){

        DatabaseReference mReferenceFriends = mReference.child("Friends").child(mAuth.getCurrentUser().getUid());
        mReferenceFriends.keepSynced(true);

        Query query = mReferenceFriends;
        //FirebaseDatabase.getInstance().getReference().child("Blog").limitToLast(50);

        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(query, Friends.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Friends, UserViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final UserViewHolder h, int i, @NonNull Friends model) {
                userId = null;
                userId = getRef(i).getKey();
                h.tvMsg.setText("since: "+ model.getDate());

                final String user_id = userId;
                DatabaseReference mReferenceUsers = mReference.child("Users").child(user_id);
                //mReferenceUsers.keepSynced(true);
                mReferenceUsers.addValueEventListener(new ValueEventListener() {
                    @Override
                   public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()  != null){
                            Log.i("AAA", dataSnapshot.toString());
                            fullname = dataSnapshot.child("fullname").getValue().toString();
                            imageUrl = dataSnapshot.child("image_url").getValue().toString();

                            //Toast.makeText(getContext(), imageUrl, Toast.LENGTH_SHORT).show();

                            h.tvName.setText(fullname);
                            if(dataSnapshot.hasChild("online")){
                                String onlineStatus = String.valueOf(dataSnapshot.child("online").getValue()) ;
                                if(onlineStatus.equals("true")){h.tvLoginIcon.setVisibility(View.VISIBLE); }
                                else {h.tvLoginIcon.setVisibility(View.GONE);}
                            }
                            Picasso.get().load(imageUrl)
                                    .placeholder(R.drawable.user_avatar)
                                    .error(R.drawable.user_avatar)
                                    .into(h.imvProfile);
                        }

                       /* Picasso.get().load(imageUrl)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.user_avatar)
                                .error(R.drawable.user_avatar)
                                .into(h.imvProfile, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(imageUrl)
                                                .placeholder(R.drawable.user_avatar)
                                                .error(R.drawable.user_avatar)
                                                .into(h.imvProfile);
                                    }
                                });

                                */
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                h.llMainRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CharSequence[] options = new CharSequence[]{"Open Profile", "Send Message"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Select Option");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                switch (i){
                                    case 0:
                                        Intent iProfile = new Intent(getContext(), ProfileActivity.class);
                                        iProfile.putExtra("user_id", user_id);
                                        iProfile.putExtra("action", 0);
                                        startActivity(iProfile);
                                        break;
                                    case 1:
                                        Intent iSingle = new Intent(getContext(), ChatActivity.class);
                                        iSingle.putExtra("user_id", user_id);
                                        startActivity(iSingle);
                                        break;
                                }

                            }
                        });
                        builder.show();

                    }
                });

            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_fragment, parent, false);
                return new UserViewHolder(v);
            }
        };

        rcv.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
