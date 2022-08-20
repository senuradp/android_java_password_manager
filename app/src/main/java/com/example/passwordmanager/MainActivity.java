package com.example.passwordmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button registerBtn, loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //check network connection
        checkConnection();

        //binding
        loginBtn = findViewById(R.id.fLogin);
        registerBtn = findViewById(R.id.fRegister);

        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

    }

    //add click events to button
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fLogin:
                startActivity(new Intent(this, Login.class));
                break;
            case R.id.fRegister:
                startActivity(new Intent(this, Register.class));
                break;
        }
    }

//    to check network connection
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

//    @Override
//    public void onConfigurationChanged(@NonNull Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            setContentView(R.layout.activity_main);
//            Toast.makeText(this, "Portrait Mode", Toast.LENGTH_SHORT).show();
//        }
//        else if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
//            setContentView(R.layout.activity_main);
//            Toast.makeText(this, "Portrait Mode", Toast.LENGTH_SHORT).show();
//        }
//    }
}