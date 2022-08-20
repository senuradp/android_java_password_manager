package com.example.passwordmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AddAccount extends AppCompatActivity implements View.OnClickListener{

    private EditText txtName, txtEmail, txtAccPageUrl;
    private TextInputEditText txtPswd;
    private Button btnAdd;
    private ImageView btnBack, iconImg;
    private Spinner imageSpinner;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    HashMap<Integer,String> spinnerMap = new HashMap<Integer,String>();
    String[] spinnerArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        checkConnection();

        txtName = findViewById(R.id.txtNameAdd);
        txtEmail = findViewById(R.id.txtEmailAdd);
        txtPswd = findViewById(R.id.txtPswdAdd);
        txtAccPageUrl = findViewById(R.id.accPageUrl);
        btnAdd = findViewById(R.id.btnAdd);
        btnBack = findViewById(R.id.btnBack);
        imageSpinner = findViewById(R.id.changeImgIconSpin);
        iconImg = findViewById(R.id.iconImg);

        btnAdd.setOnClickListener(this);
        btnBack.setOnClickListener(this);

//        load icons
//        From line 74 to 84 will convert the output json string
//        to json array and loop through every data.
//        While looping that data social name will be added to the spinner array and
//        url with the position will be added to the spinnerMap hash array it is like a associative array
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

    }

    //load json file from assets folder
//    This function read the local json file and return the data as string .
    public String loadJSONFromAsset() {
        String json = null;
        try {
//            Input stream is used to read files
            InputStream is = getAssets().open("data.json");
            int size = is.available();
//            block of memory into which we can write data, which we can later be read again.
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAdd:
                insertData();
                clear();
                finish();
                break;
            case R.id.btnBack:
                finish();
        }
    }

    private void insertData(){
        Map<String,Object> map = new HashMap<>();
//        DatabaseReference userData = FirebaseDatabase.getInstance().getReference("passwordmanager-fc464").child();
        map.put("socialMediaName",txtName.getText().toString().trim());
        map.put("email",txtEmail.getText().toString().trim());
        map.put("password",txtPswd.getText().toString().trim());
        map.put("accountPageUrl",txtAccPageUrl.getText().toString().trim());
        map.put("imageUrl",imageSpinner.getSelectedItemPosition());

        firebaseDatabase.getReference("passwordmanager-fc464").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("account")
                .push()
                .setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddAccount.this, "Account added successfully !", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddAccount.this, "Failed to add account !", Toast.LENGTH_SHORT).show();
                        checkConnection();
                    }
                });

    }

    private void clear(){
        txtName.setText("");
        txtEmail.setText("");
        txtPswd.setText("");
        txtAccPageUrl.setText("");
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