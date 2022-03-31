package com.origin.esports;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.origin.esports.Originconfig.URL;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;





public class LoginActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword;
    private ProgressDialog pDialog;
    private static final String TAG_USERID = URL.USERID;
    private static final String TAG_FIRSTNAME = URL.FIRSTNAME;
    private static final String TAG_LASTNAME = URL.LASTNAME;
    private static final String TAG_USERNAME = URL.USERNAME;
    private static final String TAG_PUBGUSERNAME = URL.PUBGUSERNAME;
    private static final String TAG_EMAIL = URL.EMAIL;
    private static final String TAG_PASSWORD = URL.PASSWORD;
    private static final String TAG_MOBILE = URL.MOBILE;
    private static final String TAG_ERROR = URL.ERROR;
    private static final String TAG_MSG = URL.MSG;
    private static final String TAG_kills = URL.kills;
    private static final String TAG_TOTALMATCHPLAYED = URL.TOTALMATCHPLAYED;
    private static final String TAG_WONAMOUNT = URL.WONAMOUNT;
    private static final String TAG_BALANCE = URL.BALANCE;
    private ArrayList<HashMap<String, String>> offersList;
    public static  String getfirstname,getlastname,getusername,getpubgusername,getemail;
    public static String getmobile;
    public static int getuserid;
    public static int getkills;
    public static int gettotalmatchplayed;
    public static int getwonamount;
    public static int getbalance;
    private static final String TAG_ISFIRSTSTART ="firstStart";
    JSONParserString jsonParserString = new JSONParserString();
    private TextView resetpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        offersList = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = (EditText) findViewById(R.id.username);
        editTextPassword = (EditText) findViewById(R.id.password);
        resetpass = findViewById(R.id.resetnow);

        //if user presses on login
        //calling the method login
        findViewById(R.id.signinbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        //if user presses on not registered
        findViewById(R.id.registerFromLogin).setOnClickListener(view -> {
            //open register screen
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        });
        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this,"Email us from your registed email id",Toast.LENGTH_LONG).show();
                // Loading offers in Background Thread
                Intent intent = new Intent(LoginActivity.this, MobileVerifyActivity.class);
                intent.putExtra("fromregister",false);
                startActivity(intent);
            }
        });

    }

    private void userLogin() {
        //first getting the values
        final String username = editTextUsername.getText().toString();
        final String password = editTextPassword.getText().toString();

        //validating inputs
        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Please enter your username");
            editTextUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter your password");
            editTextPassword.requestFocus();
            return;
        }


        //if everything is fine


        class UserLogin extends AsyncTask<Void, Void, String> {

            SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);
            SharedPreferences.Editor editor = shred.edit();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(LoginActivity.this);
                pDialog.setMessage("Loading Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();


            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                //creating request parameters
                String rq = null;
                JSONObject params = new JSONObject();
                try {
                    params.put(TAG_USERNAME, username);
                    params.put(TAG_PASSWORD, password);
                    rq = jsonParserString.makeHttpRequest(URL.LOGIN, params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //returing the response
                Log.d("test",rq);
                return rq;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.dismiss();
                if (s == null || s.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject ack = new JSONObject(s);

                            String decData = Helper.profileDecrypt(ack.get("Data").toString(), ack.get("Hash").toString());
                            if (!Helper.verify(decData, ack.get("Sign").toString(), JSONParserString.publickey)) {
                                Toast.makeText(LoginActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                 Log.d("test",s);
                                JSONObject obj = new JSONObject(decData);
                                //checking for error to authenticate
                                if (!obj.getBoolean(TAG_ERROR)) {

//                                    getfirstname = obj.getString(TAG_FIRSTNAME);
//                                    getlastname = obj.getString(TAG_LASTNAME);
                                    getusername = obj.getString(TAG_USERNAME);
                                    getpubgusername = obj.getString(TAG_PUBGUSERNAME);
//                                    getemail = obj.getString(TAG_EMAIL);
                                    getmobile = obj.getString(TAG_MOBILE);
                                    getuserid = obj.getInt(TAG_USERID);
                                    getkills = obj.getInt(TAG_kills);
                                    gettotalmatchplayed = obj.getInt(TAG_TOTALMATCHPLAYED);
                                    getwonamount = obj.getInt(TAG_WONAMOUNT);
                                    getbalance = obj.getInt(TAG_BALANCE);

                                    //saving to prefrences
                                    editor.putString(TAG_USERNAME, getusername).apply();
                                    editor.putBoolean(TAG_ISFIRSTSTART, false).apply();
//                                    editor.putString(TAG_FIRSTNAME, getfirstname).apply();
//                                    editor.putString(TAG_LASTNAME, getlastname).apply();
                                    editor.putString(TAG_PUBGUSERNAME, getpubgusername).apply();
//                                    editor.putString(TAG_EMAIL, getemail).apply();
                                    editor.putString(TAG_MOBILE, getmobile).apply();
                                    editor.putInt(TAG_USERID, getuserid).apply();
                                    editor.putInt(TAG_kills, getkills).apply();
                                    editor.putInt(TAG_WONAMOUNT, getwonamount).apply();
                                    editor.putInt(TAG_BALANCE, getbalance).apply();
                                    editor.putInt(TAG_TOTALMATCHPLAYED, gettotalmatchplayed).apply();


                                    Toast.makeText(getApplicationContext(), obj.getString(TAG_MSG), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));


                                    //getting the user from the response.
                                    //starting the profile activity
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), obj.getString(TAG_MSG), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        }
        UserLogin ul = new UserLogin();
        ul.execute();
    }
}