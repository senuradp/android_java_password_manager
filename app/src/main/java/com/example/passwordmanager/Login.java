package com.example.passwordmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail, editTextPswd;
    private Button loginBtn;
    private TextView regTextBtn;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //hide action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //check network connection
        checkConnection();

        //binding
        editTextEmail = findViewById(R.id.eMail);
        editTextPswd = findViewById(R.id.lPswd);
        loginBtn = findViewById(R.id.loginBtn);
        regTextBtn = findViewById(R.id.registerTextBtn);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(this);
        regTextBtn.setOnClickListener(this);

    }

    //click events to buttons
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.loginBtn:
                loginUser();
                break;
            case R.id.registerTextBtn:
                startActivity(new Intent(Login.this,Register.class));
                break;
        }
    }


    public void loginUser(){
        // trim to remove spaces at the start and the end
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPswd.getText().toString().trim();

        //validate fields
        if(TextUtils.isEmpty(email)){
            editTextEmail.setError("Email is required !");
            return;
        }

        if(TextUtils.isEmpty(password)){
            editTextPswd.setError("Password is required !");
            return;
        }

        if(password.length() < 6){
            editTextPswd.setError("Password must be greater than or equal to 6 characters !");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        loginBtn.setVisibility(View.GONE);
        regTextBtn.setVisibility(View.GONE);

        //authenticate user
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Login.this, "Login successful !", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    loginBtn.setVisibility(View.VISIBLE);
                    regTextBtn.setVisibility(View.VISIBLE);
                    startActivity(new Intent(Login.this,Home.class));
                }else{
                    Toast.makeText(Login.this, "Error occurred while logging in !"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    loginBtn.setVisibility(View.VISIBLE);
                    regTextBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //check connection function
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