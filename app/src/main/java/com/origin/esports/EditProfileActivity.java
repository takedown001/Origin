package com.origin.esports;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.origin.esports.Originconfig.URL;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EditProfileActivity extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParserString jsonparser = new JSONParserString();


    // url to get all products list
    private static final String url = URL.EditProfile;
    private static final String urlresetpassword = URL.urlresetpassword;

    // JSON Node names
    public static final String TAG_ERROR = URL.ERROR;

    //user

    private static final String TAG_USERID = URL.USERID;
    private static final String TAG_FIRSTNAME = URL.FIRSTNAME;
    private static final String TAG_LASTNAME = URL.LASTNAME;
    private static final String TAG_USERNAME = URL.USERNAME;
    private static final String TAG_PUBGUSERNAME = URL.PUBGUSERNAME;
    private static final String TAG_EMAIL = URL.EMAIL;
    private static final String TAG_MOBILE = URL.MOBILE;
    private static final String TAG_PASSWORD = URL.PASSWORD;
    private static final String TAG_OLDPASSWORD = URL.OLDPASSWORD;
    private static final String TAG_MSG = URL.MSG;
    private static final String TAG_kills = URL.kills;
    private static final String TAG_TOTALMATCHPLAYED = URL.TOTALMATCHPLAYED;
    private static final String TAG_WONAMOUNT = URL.WONAMOUNT;
    private static final String TAG_BALANCE = URL.BALANCE;

    public static String getfirstname, getlastname, getpubgusername;

    public static int getkills;
    public static int gettotalmatchplayed;
    public static int getwonamount;
    public static int getbalance;
    private static final String TAG_ISFIRSTSTART = "firstStart";

    // products JSONArray
    private JSONArray jsonarray = null;

    //Prefrance


    private int success;

    private RadioGroup Gender;
    private TextInputEditText birthdate;
    private TextInputEditText eMail;
    private String email;
    private RadioButton female;
    private String firstname;
    private TextInputEditText fname;

    private String lastname;
    private TextInputEditText lname;
    private TextInputEditText mNumber;
    private RadioButton male;
    private String mnumber;
    private TextInputEditText newPass;
    private TextInputEditText oldPass;
    private Button resetPassButton;
    private TextInputEditText retypeNewPass;
    private Button saveButton;
    private TextView successText;
    private TextView successTextPassword;
    private TextInputEditText uname;
    private TextInputEditText pubgusername;
    private String username;
    private String pubguname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        // Hashmap for ListView

        SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);
        fname = (TextInputEditText) findViewById(R.id.firstname);
        lname = (TextInputEditText) findViewById(R.id.lastname);
        uname = (TextInputEditText) findViewById(R.id.username);
        pubgusername = (TextInputEditText) findViewById(R.id.pubgusername);
        eMail = (TextInputEditText) findViewById(R.id.email);
        mNumber = (TextInputEditText) findViewById(R.id.mobileNumber);
        birthdate = (TextInputEditText) findViewById(R.id.dob);
        Gender = (RadioGroup) findViewById(R.id.gender);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        saveButton = (Button) findViewById(R.id.saveBtn);
        oldPass = (TextInputEditText) findViewById(R.id.oldpass);
        newPass = (TextInputEditText) findViewById(R.id.newpass);
        retypeNewPass = (TextInputEditText) findViewById(R.id.retypeNewPass);
        resetPassButton = (Button) findViewById(R.id.changePassBtn);
        successText = (TextView) findViewById(R.id.messageView);
        successTextPassword = (TextView) findViewById(R.id.passwordMessageView);

        username = shred.getString(TAG_USERNAME, "");
        pubguname = shred.getString(TAG_PUBGUSERNAME, "");
        firstname = shred.getString(TAG_FIRSTNAME, "");
        lastname = shred.getString(TAG_LASTNAME, "");
        email = shred.getString(TAG_EMAIL, "");
        mnumber = shred.getString(TAG_MOBILE, "");


        fname.setText(firstname);
        lname.setText(lastname);
        uname.setText(username);
        pubgusername.setText(pubguname);
        eMail.setText(email);
        mNumber.setText(mnumber);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( pubgusername.getText().length() > 1 ) {
                    // Loading jsonarray in Background Thread
                    new OneLoadAllProducts().execute();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Please enter value for all field", Toast.LENGTH_SHORT).show();
                }
            }
        });
        resetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldPass.getText().length() > 1 && newPass.getText().length() > 1 && retypeNewPass.getText().length() > 1) {
                    if (newPass.getText().toString().equals(retypeNewPass.getText().toString())) {
                        // Loading jsonarray in Background Thread
                        new ResetPassword().execute();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "NewPassword And RetypePass is not Same", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "Please enter value for all field", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    class OneLoadAllProducts extends AsyncTask<Void, Void, String> {
        SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);
        SharedPreferences editprofile = getSharedPreferences("editprofile", MODE_PRIVATE);
        SharedPreferences.Editor editor = editprofile.edit();

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProfileActivity.this);
            pDialog.setMessage("Loading Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         */
        protected String doInBackground(Void... voids) {
            // Building Parameters
            JSONObject params = new JSONObject();
            String rq = null;
            try {

            params.put(TAG_USERID, String.valueOf(shred.getInt(TAG_USERID, 0)));
            params.put(TAG_PUBGUSERNAME, pubgusername.getText().toString());
//            params.put(TAG_FIRSTNAME, fname.getText().toString());
//            params.put(TAG_LASTNAME, lname.getText().toString());
            rq = jsonparser.makeHttpRequest(url, params);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // getting JSON string from URL
            return rq;

        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            if (s == null || s.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                return;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        JSONObject ack = new JSONObject(s);

                        String decData = Helper.profileDecrypt(ack.get("Data").toString(), ack.get("Hash").toString());
                        if (!Helper.verify(decData, ack.get("Sign").toString(), JSONParserString.publickey)) {
                            Toast.makeText(EditProfileActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            //converting response to json object
                            JSONObject obj = new JSONObject(decData);
                            //if no error in response
//                            getfirstname = obj.getString(TAG_FIRSTNAME);
//                            getlastname = obj.getString(TAG_LASTNAME);
                            getpubgusername = obj.getString(TAG_PUBGUSERNAME);
                            getkills = obj.getInt(TAG_kills);
                            gettotalmatchplayed = obj.getInt(TAG_TOTALMATCHPLAYED);
                            getwonamount = obj.getInt(TAG_WONAMOUNT);
                            getbalance = obj.getInt(TAG_BALANCE);

                            //checking for error to authenticate
                            if (!obj.getBoolean(TAG_ERROR)) {

                                //saving to prefrences

                                editor.putBoolean(TAG_ISFIRSTSTART, false).apply();
//                                editor.putString(TAG_FIRSTNAME, getfirstname).apply();
//                                editor.putString(TAG_LASTNAME, getlastname).apply();
                                editor.putString(TAG_PUBGUSERNAME, getpubgusername).apply();
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }

    }

    class ResetPassword extends AsyncTask<Void, Void, String> {
        SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProfileActivity.this);
            pDialog.setMessage("Loading Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         */
        protected String doInBackground(Void... voids) {
            // Building Parameters
            JSONObject params = new JSONObject();
            String rq = null;
            try {

                params.put(TAG_USERID, String.valueOf(shred.getInt(TAG_USERID, 0)));
                params.put(TAG_USERNAME, shred.getString(TAG_USERNAME, "Not Found"));
                params.put(TAG_PASSWORD, newPass.getText().toString());
                params.put(TAG_OLDPASSWORD, oldPass.getText().toString());
              rq =   jsonparser.makeHttpRequest(urlresetpassword, params);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return rq;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            if (s == null || s.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                return;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        JSONObject ack = new JSONObject(s);
                        // Log.d("test", String.valueOf(ack));
                        String decData = Helper.profileDecrypt(ack.get("Data").toString(), ack.get("Hash").toString());
                        if (!Helper.verify(decData, ack.get("Sign").toString(), JSONParserString.publickey)) {
                            Toast.makeText(EditProfileActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            JSONObject obj = new JSONObject(decData);
                            if (!obj.getBoolean(TAG_ERROR)) {

                                Intent intent = new Intent(EditProfileActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                                Toast.makeText(EditProfileActivity.this,obj.getString(TAG_MSG), Toast.LENGTH_LONG).show();

                            } else {
                                // no jsonarray found
                                Toast.makeText(EditProfileActivity.this, obj.getString(TAG_MSG), Toast.LENGTH_LONG).show();

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


        }

    }
}

