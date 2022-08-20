package com.example.passwordmanager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class GeneratePassword extends AppCompatActivity implements View.OnClickListener {

    private Button genBtn8, genBtn16;
    private TextView output;
    private ImageView btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_password);

        //hide action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //check network connection
        checkConnection();

        //binding
        genBtn8 = findViewById(R.id.genBtn8);
        genBtn16 = findViewById(R.id.genBtn16);
        output = findViewById(R.id.genPswdOutput);
        btnBack = findViewById(R.id.btnBack);

        genBtn8.setOnClickListener(this);
        genBtn16.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    //add click events to buttons
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.genBtn8:
                output.setText(generatePassword(8));
                break;
            case R.id.genBtn16:
                output.setText(generatePassword(16));
                break;
            case R.id.btnBack:
                finish();
                break;
        }
    }

    // generate random password
    private String  generatePassword(int length){
        char [] array = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%&".toCharArray();
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i<length; i++){
            char c = array[random.nextInt(array.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

//    check network connection
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