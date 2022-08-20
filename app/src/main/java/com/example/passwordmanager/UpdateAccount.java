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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UpdateAccount extends AppCompatActivity {

    private EditText txtName, txtEmail, txtAccPageUrl;
    private TextInputEditText txtPswd;
    private Button btnUpdt;
    private ImageView btnBack, iconImg;
    private Spinner imageSpinner;
    private Intent intent;
    private String str;
    DatabaseReference databaseReference;
    HashMap<Integer,String> spinnerMap = new HashMap<Integer,String>();
    String[] spinnerArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        checkConnection();

        txtName = findViewById(R.id.txtNameUpdt);
        txtEmail = findViewById(R.id.txtEmailUpdt);
        txtPswd = findViewById(R.id.txtPswdUpdt);
        txtAccPageUrl = findViewById(R.id.txtAccPageUrlUpdt);
        btnUpdt = findViewById(R.id.btnUpdt);
        btnBack = findViewById(R.id.btnBackUpdt);

        imageSpinner = findViewById(R.id.changeImgIconSpin);
        iconImg = findViewById(R.id.iconImg);

        intent = getIntent();
        str = intent.getStringExtra("password_key");

        databaseReference = FirebaseDatabase.getInstance().getReference("passwordmanager-fc464")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("account").child(str);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AccountsModel accountsModel = snapshot.getValue(AccountsModel.class);
                txtName.setText(accountsModel.getSocialMediaName());
                txtEmail.setText(accountsModel.getEmail());
                txtPswd.setText(accountsModel.getPassword());
                txtAccPageUrl.setText(accountsModel.accountPageUrl);
                int position = accountsModel.getImageUrl();
//                System.out.println(position);
                imageSpinner.setSelection(position);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //load icons
        try {
            JSONArray objs = new JSONObject(loadJSONFromAsset()).getJSONArray("data");
            spinnerArray = new String[objs.length()];
            for (int i=0; i<objs.length(); i++ ){
                JSONObject jo_inside = objs.getJSONObject(i);
                spinnerArray[i] = jo_inside.getString("name");
                spinnerMap.put(i,jo_inside.getString("url"));
            }
        }catch(Exception e){
            System.out.println(e);
        }

        ArrayAdapter<String> adapter =new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        imageSpinner.setAdapter(adapter);

        imageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                System.out.println(spinnerMap.get(i));
                //load image from json file url via spinner
                Picasso.get()
                        .load(spinnerMap.get(i))
                        .resize(50, 50)
                        .centerCrop()
                        .into(iconImg);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnUpdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    //load json file from assets folder
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void update(){
        Map<String,Object> map = new HashMap<>();
        map.put("socialMediaName",txtName.getText().toString().trim());
        map.put("email",txtEmail.getText().toString().trim());
        map.put("password",txtPswd.getText().toString().trim());
        map.put("accountPageUrl",txtAccPageUrl.getText().toString().trim());

        FirebaseDatabase.getInstance().getReference("passwordmanager-fc464")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("account").child(str)
                .updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UpdateAccount.this, "Account successfully updated !", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateAccount.this, "Failed to update account !", Toast.LENGTH_SHORT).show();
                        finish();
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