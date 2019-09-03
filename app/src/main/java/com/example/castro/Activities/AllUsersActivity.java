package com.example.castro.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.castro.Adapters.AllUsersRecyclerAdapter;
import com.example.castro.Models.AllUserModel;
import com.example.castro.R;
import com.example.castro.ViewHolders.AllUserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllUsersActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView rcv;
    DatabaseReference mRef;
    DatabaseReference mRefUsers;
    DatabaseReference mRefChat;
    DatabaseReference mRefFriends;
    DatabaseReference mRefMessages;
    DatabaseReference mRefRequests;
    FirebaseAuth mAuth;
    //private FirebaseRecyclerAdapter adapter;
    AllUsersRecyclerAdapter adapter;
    ArrayList<AllUserModel> mList;
    ArrayList<String> mKeys;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        setTitle("Colleagues");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rcv = (RecyclerView) findViewById(R.id.rcv);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mList = new ArrayList<>();
        mKeys = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        mRefUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mRefChat = FirebaseDatabase.getInstance().getReference().child("Chats");
        mRefFriends = FirebaseDatabase.getInstance().getReference().child("Friends");
        mRefMessages = FirebaseDatabase.getInstance().getReference().child("Messages");
        mRefRequests = FirebaseDatabase.getInstance().getReference().child("Requests");


        //fetch_users("osezuah winifred", false);

        adapter = new AllUsersRecyclerAdapter(AllUsersActivity.this, mList, mKeys);
        load_users();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
       // mLayoutManager.setReverseLayout(true);
       // mLayoutManager.setStackFromEnd(true);
        rcv.setHasFixedSize(true);
        rcv.setLayoutManager(mLayoutManager);

       // find_users("osezuah winifred", true);

    }

    public void fetch_users(String newText, boolean s){

   /*     Query query = mRefUsers.orderByChild("fullname");
        if(s){
            query = mRefUsers.orderByChild("fullname").startAt("osezuah winifred").endAt("osezuah winifred" + "\uf8ff");
        }


        //FirebaseDatabase.getInstance().getReference().child("Blog").limitToLast(50);

        FirebaseRecyclerOptions<AllUserModel> options =
                new FirebaseRecyclerOptions.Builder<AllUserModel>()
                        .setQuery(query, AllUserModel.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<AllUserModel, AllUserViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull AllUserViewHolder h, int i, @NonNull AllUserModel allUserModel) {
                final String userId = getRef(i).getKey();

                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    if((userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) || (!allUserModel.getSetup().equals("1")) ){
                        h.llMainRow.setVisibility(View.GONE);
                    }
                    else{
                            h.llMainRow.setVisibility(View.VISIBLE);
                            h.tvName.setText(allUserModel.getFullname());
                            h.tvDepartment.setText(allUserModel.getDepartment() + " | " + allUserModel.getRank());
                            Glide
                                    .with(AllUsersActivity.this)
                                    .load(allUserModel.getImage_url())
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

                                    AlertDialog.Builder builder = new AlertDialog.Builder(AllUsersActivity.this);
                                    builder.setTitle("Select Option");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            switch (i){
                                                case 0:
                                                    Intent iProfile = new Intent(AllUsersActivity.this, ProfileActivity.class);
                                                    iProfile.putExtra("user_id", userId);
                                                    iProfile.putExtra("action", 0);
                                                    startActivity(iProfile);
                                                    break;
                                                case 1:
                                                    delete_user(userId);

                                         */ /*  Intent iSingle = new Intent(AllUsersActivity.this, ChatActivity.class);
                                            iSingle.putExtra("user_id", userId);
                                            startActivity(iSingle);
                                            *//*
                                                    break;
                                            }

                                        }
                                    });
                                    builder.show();

                                }
                            });

                    }

                }**

            }

            @NonNull
            @Override
            public AllUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_all_user, parent, false);
                return new AllUserViewHolder(v);
            }

        };

        rcv.setAdapter(adapter);

        */
    }

    public void load_users(){
        mList.clear();
        mKeys.clear();
        mRefUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               // AllUserModel u = dataSnapshot.getValue(AllUserModel.class);
                //mList.add(dataSnapshot.getValue(AllUserModel.class));
                for(DataSnapshot s : dataSnapshot.getChildren()){

                    Log.i("MMM", s.child("setup").getValue().toString());
                    if(mAuth.getCurrentUser() != null){
                        if(!s.getKey().toString().equals(mAuth.getCurrentUser().getUid()) && s.child("setup").getValue().toString().equals("1")){
                            mKeys.add(s.getKey().toString());
                            mList.add(s.getValue(AllUserModel.class));
                        }
                    }
                }

                Log.i("c", String.valueOf(mList.size()));
                Log.i("cc", String.valueOf(mKeys.size()));
                Log.i("ccc", dataSnapshot.getValue().toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        rcv.setAdapter(adapter);


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
    public void delete_from_users(final String userId){
        mRefUsers.child(userId).child("setup").setValue("2").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AllUsersActivity.this, "user deleted successfully", Toast.LENGTH_SHORT).show();
                    //refresh current activity
                    finish();
                    startActivity(getIntent());
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
       // adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
       // adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_users_menu, menu);

        MenuItem search = menu.findItem(R.id.search);
        
        SearchView searchView = (SearchView) search.getActionView();
       //SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        //return true;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch(id){
            case R.id.sign_out:

                if(mAuth.getCurrentUser() != null){
                    DatabaseReference dbRefUsers = mRef.child("Users").child(mAuth.getCurrentUser().getUid());
                    dbRefUsers.child("online").setValue(ServerValue.TIMESTAMP);
                }
                mAuth.signOut();
                Intent intentContact = new Intent(AllUsersActivity.this, LoginActivity.class);
                startActivity(intentContact);
                finish();
                break;
        }
        //return true;
        return super.onOptionsItemSelected(item);
    }

    public void search(SearchView searchView){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText=newText.toLowerCase();
                ArrayList<AllUserModel> newList= new ArrayList<>();
                ArrayList<String> newKeys = new ArrayList<>();
                int x = 0;
                for (AllUserModel m : mList){
                    String mlist = m.getFullname().toLowerCase();
                    if (mlist.contains(newText)){
                        newList.add(m);
                        newKeys.add(mKeys.get(x));
                    }
                    x++;
                }
                adapter.setFilter(newList, newKeys);
                return true;
            }
        });
    }
}
