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
import android.util.Patterns;
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
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextfName, editTextlName, editTextemail, editTextpassword;
    private Button regBtn;
    private TextView loginTextBtn;
    private ProgressBar progressBar;

    //Create firebase instance
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //check network connection
        checkConnection();

        //binding
        editTextfName = findViewById(R.id.fName);
        editTextlName = findViewById(R.id.lName);
        editTextemail = findViewById(R.id.eMail);
        editTextpassword = findViewById(R.id.rPswd);
        regBtn = findViewById(R.id.regBtn);
        loginTextBtn = findViewById(R.id.loginTextBtn);
        progressBar = findViewById(R.id.progressBar);

        //add the listener
        regBtn.setOnClickListener(this);

        loginTextBtn.setOnClickListener(this);

    }

    //implement the onclick method
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.regBtn:
                //implement method to take input, validate, register user on firebaseauth and store
                registerUser();
                break;
            case R.id.loginTextBtn:
                startActivity(new Intent(this,Login.class));
                break;
        }
    }// end of oncreate

    public void registerUser(){
        //retrieve inputs and trim to remove spaces in start and end
        final String fName = editTextfName.getText().toString().trim();
        final String lName = editTextlName.getText().toString().trim();
        final String email = editTextemail.getText().toString().trim();

        // not because user can change the password
        String password = editTextpassword.getText().toString().trim();

        //validate
        if (fName.isEmpty()){
            editTextfName.setError("Full name cannot be left blank");
            editTextfName.requestFocus();
            return;
        }
        if (lName.isEmpty()){
            editTextlName.setError("Age cannot be left blank");
            editTextlName.requestFocus();
            return;
        }
        if (email.isEmpty()){
            editTextemail.setError("Email cannot be left blank");
            editTextemail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextemail.setError("Please provide a valid email");
            editTextemail.requestFocus();
            return;
        }
        if (password.isEmpty()){
            editTextpassword.setError("Full name cannot be left blank");
            editTextpassword.requestFocus();
            return;
        }
        if (password.length()<6){
            editTextpassword.setError("Please provide a password with minimum of 6 characters");
            editTextpassword.requestFocus();
            return;
        }
        //set visibility of the progress bar
        progressBar.setVisibility(View.VISIBLE);
        regBtn.setVisibility(View.GONE);
        loginTextBtn.setVisibility(View.GONE);

        //create the user on firebase and register the user and save the record in the database
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    UserModel user = new UserModel(fName, lName, email);
                    FirebaseDatabase.getInstance().getReference("passwordmanager-fc464").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //if added to db
                            if(task.isSuccessful()){
                                Toast.makeText(Register.this, "User registration successful !", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                regBtn.setVisibility(View.VISIBLE);
                                loginTextBtn.setVisibility(View.VISIBLE);
                                startActivity(new Intent(Register.this, Home.class));
                            }
                            //user not added to firbase auth
                            else {
                                Toast.makeText(Register.this, "User registration failed !", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                regBtn.setVisibility(View.VISIBLE);
                                loginTextBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
                //if user is not created display error message
                else{
                    Toast.makeText(Register.this, "Error occurred while creating new user !", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    regBtn.setVisibility(View.VISIBLE);
                    loginTextBtn.setVisibility(View.VISIBLE);
                }
            }
        });

    }

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