package com.example.castro.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.castro.Adapters.MessagesRecyclerAdapter;
import com.example.castro.Models.MessagesModel;
import com.example.castro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference mReference;

    LinearLayoutManager mLinearLayoutManager;
    Toolbar toolbar;
    ImageView imvBackArrow;
    TextView tvToolbarName;
    TextView tvToolbarLastSeen;
    CircleImageView imvToolbarImage;
    RecyclerView rcv;

    MessagesRecyclerAdapter adapter;
    ArrayList<MessagesModel> mList = new ArrayList<>();

    ImageButton imgBtnGallery;
    ImageButton imgBtnSendMsg;
    EditText edtTextMsg;
    SwipeRefreshLayout swipeLayout;
    String userId;

    private static int TOTAL_ITEMS_TO_LOAD = 10;
    private int currentPage = 1;
    private int itemPos = 0;
    private String lastKey = "";
    private String prevKey = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvToolbarName = (TextView) findViewById(R.id.toolbar_name);
        tvToolbarLastSeen = (TextView) findViewById(R.id.toolbar_last_seen);
        imvToolbarImage = (CircleImageView) findViewById(R.id.toolbar_image);
        imgBtnGallery = (ImageButton) findViewById(R.id.img_btn_gallery);
        imgBtnSendMsg = (ImageButton) findViewById(R.id.img_btn_send_msg);
        edtTextMsg = (EditText) findViewById(R.id.edt_chat_msg);
        rcv = (RecyclerView) findViewById(R.id.rcv_center);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.msg_swipe_layout);

        adapter = new MessagesRecyclerAdapter(mList, ChatActivity.this);

        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userId = getIntent().getStringExtra("user_id");

        Toast.makeText(this, mAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
        Log.i("jjj", mAuth.getCurrentUser().getUid());
        //dbRefUsers.child("online").setValue(ServerValue.TIMESTAMP);

        rcv.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this);
        rcv.setLayoutManager(mLinearLayoutManager);
        rcv.setAdapter(adapter);

        load_messages();

        mReference.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              //  Toast.makeText(ChatActivity.this, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
              //  Log.i("CHAT ACTIVITY:", dataSnapshot.getValue().toString());
                    String online = dataSnapshot.child("online").getValue().toString();
                    if(online.equals("true")){tvToolbarLastSeen.setText("Online");}
                    else{
                        TimeAgo timeAgo = new TimeAgo();
                        Long l = Long.parseLong(online);
                        String lastSeen = timeAgo.getTimeAgo(l, getApplicationContext());
                        tvToolbarLastSeen.setText(lastSeen);
                    }
                tvToolbarName.setText(dataSnapshot.child("fullname").getValue().toString());
                Picasso.get().load(dataSnapshot.child("image_url").getValue().toString())
                        .placeholder(R.drawable.user_avatar)
                        .error(R.drawable.user_avatar)
                        .into(imvToolbarImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mReference.child("Chats").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(userId)){

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chats/"+mAuth.getCurrentUser().getUid()+"/"+userId, chatAddMap);
                    chatUserMap.put("Chats/"+userId+"/"+mAuth.getCurrentUser().getUid(), chatAddMap);

                    mReference.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Log.i("CHAT LOG ERROR", databaseError.getMessage().toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imgBtnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = edtTextMsg.getText().toString().trim();
                if(!msg.isEmpty()){
                    String pushKey = mReference.child("Messages").child(mAuth.getCurrentUser().getUid()).child(userId).push().getKey();

                    String currentUserRef = "Messages/"+mAuth.getCurrentUser().getUid()+"/"+userId+"/"+pushKey;
                    String chatUserRef = "Messages/"+userId+"/"+mAuth.getCurrentUser().getUid()+"/"+pushKey;

                    Map msgAddMap = new HashMap();
                    msgAddMap.put("message", msg);
                    msgAddMap.put("seen", false);
                    msgAddMap.put("type", "text");
                    msgAddMap.put("time", ServerValue.TIMESTAMP);
                    msgAddMap.put("from", mAuth.getCurrentUser().getUid());

                    Map msgUserMap = new HashMap();
                    msgUserMap.put(currentUserRef, msgAddMap);
                    msgUserMap.put(chatUserRef, msgAddMap);

                    mReference.updateChildren(msgUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Log.i("MESSAGE CHAT ERROR", databaseError.getMessage().toString());
                            }
                        }
                    });

                     edtTextMsg.setText("");
                }
            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage++;
               // mList.clear();
                itemPos = 0;
                load_more_messages();
            }
        });

    }

    public void load_more_messages(){
        DatabaseReference mRefMessages = mReference.child("Messages").child(mAuth.getCurrentUser().getUid()).child(userId);
        Query mQuery = mRefMessages.orderByKey().endAt(lastKey).limitToLast(TOTAL_ITEMS_TO_LOAD);

        mQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MessagesModel m = dataSnapshot.getValue(MessagesModel.class);
                String msgKey = dataSnapshot.getKey();
                if(!prevKey.equals(msgKey)){
                    mList.add(itemPos++, m);
                }
                else{
                    prevKey = lastKey;
                }
                if(itemPos == 1){

                    lastKey = msgKey;
                }
                adapter.notifyDataSetChanged();

                //rcv.scrollToPosition(mList.size() - 1);
                swipeLayout.setRefreshing(false);
                mLinearLayoutManager.scrollToPositionWithOffset(10, 0);
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
    }

    public void load_messages(){
        DatabaseReference mRefMessages = mReference.child("Messages").child(mAuth.getCurrentUser().getUid()).child(userId);
        Query mQuery = mRefMessages.limitToLast(currentPage * TOTAL_ITEMS_TO_LOAD);
        mQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MessagesModel m = dataSnapshot.getValue(MessagesModel.class);
                mList.add(m);
                adapter.notifyDataSetChanged();
                itemPos++;

                if(itemPos == 1){
                    String msgKey = dataSnapshot.getKey();
                    lastKey = msgKey;
                    prevKey = msgKey;
                }

                rcv.scrollToPosition(mList.size() - 1);
                swipeLayout.setRefreshing(false);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference dbRefUsers = mReference.child("Users").child(mAuth.getCurrentUser().getUid());
        dbRefUsers.child("online").setValue("true");
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseReference dbRefUsers = mReference.child("Users").child(mAuth.getCurrentUser().getUid());
        dbRefUsers.child("online").setValue("true");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuth.getCurrentUser() != null){
            DatabaseReference dbRefUsers = mReference.child("Users").child(mAuth.getCurrentUser().getUid());
            dbRefUsers.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
}
