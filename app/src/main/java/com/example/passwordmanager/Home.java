package com.example.passwordmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Home extends AppCompatActivity {

    RecyclerView recyclerView;
    AccAdapter accAdapter;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();

        // check network connection
        checkConnection();

        //binding
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //retrieve data from firebase into recycler view
        FirebaseRecyclerOptions<AccountsModel> options = new FirebaseRecyclerOptions.Builder<AccountsModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference("passwordmanager-fc464").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("account"), AccountsModel.class)
                .build();
        accAdapter = new AccAdapter(options, Home.this);
        recyclerView.setAdapter(accAdapter);
        recyclerView.setItemAnimator(null);

        //bottom navigation
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.dashboard:
                        break;
                    case R.id.add:
                        startActivity(new Intent(Home.this, AddAccount.class));
                        break;
                    case R.id.genPswd:
                        startActivity(new Intent(Home.this, GeneratePassword.class));
                        break;
                    case R.id.profile:
                        startActivity(new Intent(Home.this, Profile.class));
                        break;
                }
                return Home.super.onOptionsItemSelected(item);
            }
        });
    }

    // to retrieve and send related details to the update account page when clicked on account
    public void editItem(String id){
        Intent editAndUpdate = new Intent(Home.this, UpdateAccount.class);
        editAndUpdate.putExtra("password_key", id);
        startActivity(editAndUpdate);
    }

    @Override
    protected void onStart() {
        super.onStart();
        accAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        accAdapter.stopListening();
    }



    //menu
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.search, menu);
//        MenuItem item = menu.findItem(R.id.search);
//        SearchView searchView = (SearchView) item.getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                txtSearch(s);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                txtSearch(s);
//                return false;
//            }
//        });
//        return super.onCreateOptionsMenu(menu);
//
//
//    }
//
//    private void txtSearch(String str) {
//        FirebaseRecyclerOptions<AccountsModel> options = new FirebaseRecyclerOptions.Builder<AccountsModel>()
//                .setQuery(FirebaseDatabase.getInstance().getReference("passwordmanager-fc464").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("socialMediaName")
//                .startAt(str).endAt(str+"~"), AccountsModel.class)
//                .build();
//        accAdapter = new AccAdapter(options,Home.this);
//        accAdapter.startListening();
//        recyclerView.setAdapter(accAdapter);
//    }

    public void checkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if(null!=activeNetwork){
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                Toast.makeText(this, "Wifi - Enabled", Toast.LENGTH_SHORT).show();
            }
            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                Toast.makeText(this, "Mobile Network - Enabled", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

}