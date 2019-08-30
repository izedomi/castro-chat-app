package com.example.castro.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.castro.Adapters.ViewPagerAdapter;
import com.example.castro.Fragments.ChatFragment;
import com.example.castro.Fragments.FriendsFragment;
import com.example.castro.Fragments.RequestsFragments;
import com.example.castro.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class OverviewActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference dbRef;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mtoggle;
    private Toolbar toolbar;

    ViewPager viewPager;
    TabLayout tabLayout;
    ViewPagerAdapter mViewPagerAdapter;
    NavigationView navView;
    DrawerLayout drawerLayout;
    TextView tvDrawerId;
    TextView tvDrawerEmail;
    LinearLayout llDrawerHeaderBg;
    CircleImageView imvDrawerImg;

    String staffId;


    public void initialize_widgets(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
       // mAuth.signOut();
        mDatabase = FirebaseDatabase.getInstance();
        dbRef = mDatabase.getReference();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        navView = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        View headerView = navView.getHeaderView(0);
        tvDrawerId = (TextView) headerView.findViewById(R.id.drawer_profile_id);
        tvDrawerEmail = (TextView) headerView.findViewById(R.id.drawer_profile_email);
        imvDrawerImg = (CircleImageView) headerView.findViewById(R.id.drawer_profile_img);
        llDrawerHeaderBg = (LinearLayout) headerView.findViewById(R.id.drawer_profile_bg);

        if(mAuth.getCurrentUser() != null){
            tvDrawerEmail.setText(mAuth.getCurrentUser().getEmail());
        }


        addMenu();


        //setup navigation drawer
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mtoggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mtoggle);
        mtoggle.syncState();


        //setup viewpager/tablayout
        final ChatFragment chatFragment = new ChatFragment();
        final FriendsFragment friendsFragment = new FriendsFragment();
        final RequestsFragments requestsFragments = new RequestsFragments();

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.add(chatFragment, "chats");
        mViewPagerAdapter.add(friendsFragment, "colleagues");
        mViewPagerAdapter.add(requestsFragments, "requests");
        viewPager.setAdapter(mViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // tvDrawerId.setText(staffId);

    }


    public void addMenu(){
        Menu menuProfile = navView.getMenu();
        Menu menuManageUsers = null;
        if((mAuth.getCurrentUser() != null) && (mAuth.getCurrentUser().getEmail() != null)){
            if(mAuth.getCurrentUser().getEmail().toString().equals("ema@gmail.com")){
                menuManageUsers = navView.getMenu();
            }
        }
        Menu menuAction = navView.getMenu();


        final SubMenu profile = menuProfile.addSubMenu("My Profile");
        if(mAuth.getCurrentUser() != null){
            if(mAuth.getCurrentUser().getEmail().toString().equals("ema@gmail.com")){
                final SubMenu manage = menuManageUsers.addSubMenu("Manage Account");

                manage.add("Manage Users >>").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Intent intentSendMsg = new Intent(OverviewActivity.this, AllUsersActivity.class);
                        startActivity(intentSendMsg);
                        return false;
                    }
                });
            }
        }

        final SubMenu action = menuAction.addSubMenu("Actions");


        if(mAuth.getCurrentUser() != null){
            dbRef.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null){
                        profile.clear();
                        profile.add(dataSnapshot.child("fullname").getValue().toString());
                        profile.add(dataSnapshot.child("username").getValue().toString());
                        profile.add(dataSnapshot.child("department").getValue().toString());
                        profile.add(dataSnapshot.child("rank").getValue().toString());
                        profile.add(dataSnapshot.child("gender").getValue().toString());

                        tvDrawerId.setText(dataSnapshot.child("staff_Id").getValue().toString());
                        //llDrawerHeaderBg.setBackground(dataSnapshot.child("thumbnail").getValue().toString());

                        // navUsername.setText(dataSnapshot.child("staff_Id").getValue().toString());

                        Picasso
                                .get()
                                .load(dataSnapshot.child("thumbnail").getValue().toString())
                                .placeholder(R.drawable.user_avatar).
                                error(R.drawable.user_avatar)
                                .into(imvDrawerImg);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        action.add("Edit Profile >>").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent iSetup = new Intent(OverviewActivity.this, AccountSetupActivity.class);
                iSetup.putExtra("setup", 1);
                startActivity(iSetup);
                return false;
            }
        });
        action.add("Logout >>").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                DatabaseReference dbRefUsers = dbRef.child("Users").child(mAuth.getCurrentUser().getUid());
                dbRefUsers.child("online").setValue(ServerValue.TIMESTAMP);
                mAuth.signOut();
                Intent iLogin = new Intent(OverviewActivity.this, LoginActivity.class);
                startActivity(iLogin);
                finish();
                return false;
            }
        });

        //drawerLayout.closeDrawers();
        drawerLayout.closeDrawer(GravityCompat.START);
    }


    public void check_if_profile_has_been_setup(){
        DatabaseReference userRef = dbRef.child("Users").child(mAuth.getCurrentUser().getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                    String setupStatus = dataSnapshot.child("setup").getValue().toString();
                    if(setupStatus.equals("0")){
                        Intent iSetup = new Intent(OverviewActivity.this, AccountSetupActivity.class);
                        iSetup.putExtra("setup", 0);
                        startActivity(iSetup);
                        finish();
                    }
                    if(setupStatus.equals("2")){
                        Intent iSetup = new Intent(OverviewActivity.this, AccountDeletedActivity.class);
                        startActivity(iSetup);
                        finish();
                    }

                  /*  else{
                        if(mAuth.getCurrentUser().getUid() != null){

                        }
                    }*/

               // Toast.makeText(OverviewActivity.this, dataSnapshot.toString(), Toast.LENGTH_SHORT).show();
                //Log.i("RRRRRRR", dataSnapshot.toString());

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
               // Log.i(TAG, "Failed to read value.", error.toException());
                Toast.makeText(OverviewActivity.this, "noooo", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent iLogin = new Intent(OverviewActivity.this, LoginActivity.class);
            startActivity(iLogin);
            finish();
        }
        if(currentUser != null ){

            DatabaseReference dbRefUsers = dbRef.child("Users").child(mAuth.getCurrentUser().getUid());
            dbRefUsers.child("online").setValue("true");
            check_if_profile_has_been_setup();

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuth.getCurrentUser() != null){
            DatabaseReference dbRefUsers = dbRef.child("Users").child(mAuth.getCurrentUser().getUid());
           // dbRefUsers.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){ mDrawerLayout.closeDrawer(GravityCompat.START);}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        if((mAuth.getCurrentUser() != null) && (mAuth.getCurrentUser().getEmail() != null)){
            if(mAuth.getCurrentUser().getEmail().equals("ema@gmail.com")){
                MenuItem sendBroadcast = menu.findItem(R.id.send_broadcast);
                sendBroadcast.setVisible(true);
            }
        }

        //int i = menu.size();
        //Toast.makeText(this, String.valueOf(i), Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch(id){
            case R.id.sign_out:

                if(mAuth.getCurrentUser() != null){
                    DatabaseReference dbRefUsers = dbRef.child("Users").child(mAuth.getCurrentUser().getUid());
                    dbRefUsers.child("online").setValue(ServerValue.TIMESTAMP);
                }
                    mAuth.signOut();
                    Intent intentContact = new Intent(OverviewActivity.this, LoginActivity.class);
                    startActivity(intentContact);
                    finish();
                break;
            case R.id.all_users:
               // Intent intentSendMsg = new Intent(OverviewActivity.this, AllUsersActivity.class);
                Intent intentSendMsg = new Intent(OverviewActivity.this, BroadcastActivity.class);
                startActivity(intentSendMsg);
                break;

            case R.id.send_broadcast:
                Intent intentBroadcast = new Intent(OverviewActivity.this, SendBroadcastActivity.class);
                startActivity(intentBroadcast);
                break;
        }
        return true;
    }
}
