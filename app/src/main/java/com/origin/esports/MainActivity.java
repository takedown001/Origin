package com.origin.esports;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.origin.esports.Originconfig.URL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class MainActivity extends AppCompatActivity {
//textbox
    private EditText firstname;
    private EditText lastname;
    private EditText username;
    private EditText pubgusername;
    private EditText email;
    private EditText mobile;
    private EditText password;
    private EditText promocode;
    private Button signup;
    private ProgressDialog pDialog;

    private static final String TAG_FIRSTNAME = URL.FIRSTNAME;
    private static final String TAG_LASTNAME = URL.LASTNAME;
    private static final String TAG_USERNAME = URL.USERNAME;
    private static final String TAG_PUBGUSERNAME = URL.PUBGUSERNAME;
    private static final String TAG_EMAIL = URL.EMAIL;
    private static final String TAG_MOBILE = URL.MOBILE;
    private static final String TAG_PASSWORD = URL.PASSWORD;
    private static final String TAG_PROMOCODE = "promocode";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        username = findViewById(R.id.username);
        pubgusername = findViewById(R.id.Pubgusername);
        email = findViewById(R.id.email);
        mobile = findViewById(R.id.mobileNumber);
        password = findViewById(R.id.password);
        promocode = findViewById(R.id.promocode);
        signup = findViewById(R.id.registerBtn);


        findViewById(R.id.loginFromRegister).setOnClickListener(view -> {
            //if user pressed on login
            //we will open the login screen
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkdetails()) {
                    // Loading offers in Background Thread
                    Intent intent = new Intent(MainActivity.this, MobileVerifyActivity.class);
//                    intent.putExtra(TAG_FIRSTNAME, firstname.getText().toString().trim());
//                    intent.putExtra(TAG_LASTNAME, lastname.getText().toString().trim());
                    intent.putExtra(TAG_USERNAME, username.getText().toString().trim());
                    intent.putExtra(TAG_PUBGUSERNAME, pubgusername.getText().toString().trim());
            //        intent.putExtra(TAG_EMAIL, email.getText().toString().trim());
                    intent.putExtra(TAG_MOBILE, mobile.getText().toString().trim());
                    intent.putExtra(TAG_PASSWORD, password.getText().toString().trim());
                    intent.putExtra(TAG_PROMOCODE, promocode.getText().toString().trim());
                    intent.putExtra("fromregister",true);
                    startActivity(intent);

                }
            }
        });

    }
    private boolean checkdetails() {

        //special character checking



        Pattern p = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher("I am a string"+username.getText().toString());
        boolean b = m.find();

        if (b)
            System.out.println("takedown_There is a special character in my string");

//        if (email.getText().toString().trim().isEmpty()) {
//            Toast.makeText(MainActivity.this, "Enter Value for Email", Toast.LENGTH_SHORT).show();
//            email.requestFocus();
//            return false;
    //   } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
//            Toast.makeText(MainActivity.this, "Enter valid Value for Email", Toast.LENGTH_SHORT).show();
//            email.requestFocus();
//            return false;
        else if (password.getText().toString().trim().isEmpty()) {
            Toast.makeText(MainActivity.this, "Enter Value for Password", Toast.LENGTH_SHORT).show();
            password.requestFocus();
            return false;
      // } else if (firstname.getText().toString().trim().isEmpty()) {
//            Toast.makeText(MainActivity.this, "Enter Value for FirstName", Toast.LENGTH_SHORT).show();
//            firstname.requestFocus();
//            return false;
//        } else if (lastname.getText().toString().trim().isEmpty()) {
//            Toast.makeText(MainActivity.this, "Enter Value for LastName", Toast.LENGTH_SHORT).show();
//            lastname.requestFocus();
//            return false;
        } else if (username.getText().toString().trim().isEmpty()) {
            Toast.makeText(MainActivity.this, "Enter Value for Username", Toast.LENGTH_SHORT).show();
            username.requestFocus();
            return false;
        } else if (p.matcher(username.getText().toString()).find()) {
            Toast.makeText(MainActivity.this, "Enter Username without any special characters", Toast.LENGTH_SHORT).show();
            username.requestFocus();
            return false;
        } else if (pubgusername.getText().toString().trim().isEmpty()) {
            Toast.makeText(MainActivity.this, "Enter Value for Pubg Username", Toast.LENGTH_SHORT).show();
            pubgusername.requestFocus();
            return false;
        } else if (mobile.getText().toString().trim().isEmpty()) {
            Toast.makeText(MainActivity.this, "Enter Value for Mobile", Toast.LENGTH_SHORT).show();
            mobile.requestFocus();
            return false;
        } else if (!Patterns.PHONE.matcher(mobile.getText().toString().trim()).matches()) {
            Toast.makeText(MainActivity.this, "Enter Valid Value for MobileNumber", Toast.LENGTH_SHORT).show();
            mobile.requestFocus();
            return false;
        }


        return true;
    }



}