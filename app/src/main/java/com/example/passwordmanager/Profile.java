package com.example.passwordmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class Profile extends AppCompatActivity {

    private TextView pFname, pLname, pEmail;
    private Spinner spinner;
    private int currentSelection;
    private ImageView btnBack;
    private Button logoutBtn;
    DatabaseReference databaseReference;

    //get instance of Firebase class
    FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //hide action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //check network connection
        checkConnection();

        //binding
        pFname = findViewById(R.id.profileFname);
        pLname = findViewById(R.id.profileLname);
        pEmail = findViewById(R.id.profileEmail);
        spinner = findViewById(R.id.changeLanguageSpin);
        btnBack = findViewById(R.id.btnBack);
        logoutBtn = findViewById(R.id.logoutBtn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference("passwordmanager-fc464")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                pFname.setText(userModel.getfName());
                pLname.setText(userModel.getlName());
                pEmail.setText(userModel.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //change language for selected item in drop down
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentSelection = spinner.getSelectedItemPosition();
                if(currentSelection==1){
                    String language = "en";
                    setLocale(language);
                    refreshApp();
                }
                if(currentSelection==2){
                    setLocale("si");
                    refreshApp();
                }
                if(currentSelection==3){
                    setLocale("ta");
                    refreshApp();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        //call logout function to logout button
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                logOutUser();
            }
        });

    }

    //logout function
    private void logOutUser() {
        Intent mainActivity = new Intent(Profile.this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
        finish();
    }

    //change language function
    private void setLocale(String language) {
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = new Locale(language);
        resources.updateConfiguration(configuration, metrics);
        onConfigurationChanged(configuration);
    }

    //refresh to apply changes
    private void refreshApp(){
        Intent refresh = new Intent(this, Profile.class);
        startActivity(refresh);
        finish();
    }

    // to check network connection
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