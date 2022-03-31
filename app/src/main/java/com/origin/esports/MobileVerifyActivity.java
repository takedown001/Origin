package com.origin.esports;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.origin.esports.Originconfig.URL;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.Timer;

import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.rilixtech.widget.countrycodepicker.Country;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;



public class MobileVerifyActivity extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    private final JSONParserString jsonParser = new JSONParserString();

    // url to get all products list
    private static final String url = URL.REGISTER;
    private static final String urlresetpass = URL.urlresetpasswordmobile;
    private static final String TAG_ISFIRSTSTART = "firstStart";
    // JSON Node names
    private static final String TAG_SUCCESS = URL.SUCCESS;
    private static final String TAG_FIRSTNAME = URL.FIRSTNAME;
    private static final String TAG_LASTNAME = URL.LASTNAME;
    private static final String TAG_USERNAME = URL.USERNAME;
    private static final String TAG_GAMEUSERNAME = URL.PUBGUSERNAME;
    private static final String TAG_EMAIL = URL.EMAIL;
    private static final String TAG_MOBILE = URL.MOBILE;
    private static final String TAG_PASSWORD = URL.PASSWORD;
    private static final String TAG_PROMOCODE = "promocode";
    private static final String TAG_MSG = URL.MSG;
    //Textbox
    private String firstname;
    private String lastname;
    private String username;
    private String gameusername;
    private String email;
    private String countrycode;
    private String mobile;
    private String password;
    private String other;
    private String promocode;
    String phoneNumber;
    private int success;

    private boolean ispass;

    private TextInputEditText newPass;
    private Button resetPassButton;
    private TextInputEditText retypeNewPass;
    String codeSent;
    private FirebaseAuth mAuth;
    private CountryCodePicker ccp;
    private TextInputEditText phoneed;
    private TextInputEditText codeed;
    private FloatingActionButton fabbutton;
    private String mVerificationId;
    private TextView timertext;
    private Timer timer;
    private ImageView verifiedimg;
    private Boolean mVerified = false;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String Result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verify);
        codeed = (TextInputEditText) this.findViewById(R.id.verificationed);
        fabbutton = (FloatingActionButton) findViewById(R.id.sendverifybt);
        timertext = (TextView) findViewById(R.id.timertv);
        verifiedimg = (ImageView) findViewById(R.id.verifiedsign);

        newPass = (TextInputEditText) findViewById(R.id.newpass);
        retypeNewPass = (TextInputEditText) findViewById(R.id.retypeNewPass);
        resetPassButton = (Button) findViewById(R.id.changePassBtn);

        try {
            ispass = getIntent().getBooleanExtra("fromregister", false);
            if (ispass) {
//                firstname = getIntent().getStringExtra(TAG_FIRSTNAME);
//                lastname = getIntent().getStringExtra(TAG_LASTNAME);
                username = getIntent().getStringExtra(TAG_USERNAME);
                gameusername = getIntent().getStringExtra(TAG_GAMEUSERNAME);
            //    email = getIntent().getStringExtra(TAG_EMAIL);
                mobile = getIntent().getStringExtra(TAG_MOBILE);
                password = getIntent().getStringExtra(TAG_PASSWORD);
                promocode = getIntent().getStringExtra(TAG_PROMOCODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        phoneed = (TextInputEditText) this.findViewById(R.id.numbered);
        ccp.registerCarrierNumberEditText(phoneed);

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {

            @Override
            public void onCountrySelected() {

            }

            public void onCountrySelected(Country selectedCountry) {
                countrycode = selectedCountry.getPhoneCode();
                Toast.makeText(MobileVerifyActivity.this, "Updated " + selectedCountry.getPhoneCode(), Toast.LENGTH_SHORT).show();
            }

        });


        if (!ispass) {
            phoneed.setEnabled(true);

        } else {
            phoneed.setText(mobile);
        }

        fabbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabbutton.getTag().equals("send")) {
                    if (!phoneed.getText().toString().trim().isEmpty() && phoneed.getText().toString().trim().length() >= 5) {
                        //      startPhoneNumberVerification(ccp.getSelectedCountryCodeWithPlus()+phoneed.getText().toString().trim());
                        sendVerificationCode(ccp.getSelectedCountryCodeWithPlus() + phoneed.getText().toString().trim());
                        mVerified = false;
                        starttimer();
                        codeed.setVisibility(View.VISIBLE);
                        fabbutton.setImageResource(R.drawable.ic_arrow_forward_white_24dp);
                        fabbutton.setTag("verify");
                    } else {
                        phoneed.setError("Please enter valid mobile number");
                    }
                }

                if (fabbutton.getTag().equals("verify")) {
                    if (!codeed.getText().toString().trim().isEmpty() && !mVerified) {
                        Snackbar snackbar = Snackbar
                                .make((ConstraintLayout) findViewById(R.id.parentlayout), "Please wait...", Snackbar.LENGTH_LONG);

                        snackbar.show();
                        verifySignInCode(codeed.getText().toString());
                        // PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, codeed.getText().toString());
                        //      signInWithPhoneAuthCredential(credential);
                    }


                }


            }
        });


        resetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newPass.getText().length() > 1 && retypeNewPass.getText().length() > 1) {
                    if (newPass.getText().toString().equals(retypeNewPass.getText().toString())) {
                        // Loading jsonarray in Background Thread
                        new OneLoadAllProductsResetPass().execute();
                    } else {
                        Toast.makeText(MobileVerifyActivity.this, "NewPassword And RetypePass is not Same", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MobileVerifyActivity.this, "Please enter value for all field", Toast.LENGTH_SHORT).show();
                }

            }
        });

        timertext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!phoneed.getText().toString().trim().isEmpty() && phoneed.getText().toString().trim().length() == 10) {
                    resendVerificationCode(phoneed.getText().toString().trim(), mResendToken);
                    mVerified = false;
                    starttimer();
                    codeed.setVisibility(View.VISIBLE);
                    fabbutton.setImageResource(R.drawable.ic_arrow_forward_white_24dp);
                    fabbutton.setTag("verify");
                    Snackbar snackbar = Snackbar
                            .make((ConstraintLayout) findViewById(R.id.parentlayout), "Resending verification code...", Snackbar.LENGTH_LONG);

                    snackbar.show();
                }
            }

        });

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verificaiton without
            //     user action.
            Log.d("TAG", "onVerificationCompleted:" + phoneAuthCredential);

            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w("TAG", "onVerificationFailed", e);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                Snackbar snackbar = Snackbar
                        .make((ConstraintLayout) findViewById(R.id.parentlayout), "Verification Failed !! Invalied verification Code", Snackbar.LENGTH_LONG);

                snackbar.show();
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Snackbar snackbar = Snackbar
                        .make((ConstraintLayout) findViewById(R.id.parentlayout), "Verification Failed !! Too many request. Try after some time. ", Snackbar.LENGTH_LONG);

                snackbar.show();
            }

        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            codeSent = s;
        }
    };

    private void sendVerificationCode(String phone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }


    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void verifySignInCode(String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            mVerified = true;
                            timer.cancel();
                            verifiedimg.setVisibility(View.VISIBLE);
                            timertext.setVisibility(View.INVISIBLE);
                            phoneed.setEnabled(false);
                            ((TextInputLayout) findViewById(R.id.enterotp)).setVisibility(View.GONE);
                            codeed.setVisibility(View.INVISIBLE);
                            Snackbar snackbar = Snackbar
                                    .make((ConstraintLayout) findViewById(R.id.parentlayout), "Successfully Verified", Snackbar.LENGTH_LONG);

                            snackbar.show();
                            if (ispass) {
                                new Register().execute();
                            } else {
                                ((LinearLayout) findViewById(R.id.entermobile)).setVisibility(View.GONE);
                                ((LinearLayout) findViewById(R.id.resetpass)).setVisibility(View.VISIBLE);
                            }
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Snackbar snackbar = Snackbar
                                        .make((ConstraintLayout) findViewById(R.id.parentlayout), "Invalid OTP ! Please enter correct OTP", Snackbar.LENGTH_LONG);

                                snackbar.show();
                            }
                        }
                    }
                });
    }

    public void starttimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {

            int second = 60;

            @Override
            public void run() {
                if (second <= 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timertext.setText("RESEND CODE");
                            timer.cancel();
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timertext.setText("00:" + second--);
                        }
                    });
                }

            }
        }, 0, 1000);
    }


    @SuppressLint("StaticFieldLeak")
    class Register extends AsyncTask<Void, Void, String> {
        SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = shred.edit();

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MobileVerifyActivity.this);
            pDialog.setMessage("Registering...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            //displaying the progress bar while user registers on the server

        }

        @Override
        protected String doInBackground(Void... voids) {
            JSONObject params = new JSONObject();
            String rq = null;
            try {
            //    params.put(TAG_FIRSTNAME, firstname);
           //   params.put(TAG_LASTNAME, lastname);
                params.put(TAG_USERNAME, username);
                params.put(TAG_GAMEUSERNAME, gameusername);
//              params.put(TAG_EMAIL, email);
                params.put(TAG_MOBILE, mobile);
                params.put(TAG_PASSWORD, password);
                params.put(TAG_PROMOCODE, promocode);

            rq = jsonParser.makeHttpRequest(URL.REGISTER,params);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return rq;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            //hiding the progressbar after completion
            if (s == null || s.isEmpty()) {
                Toast.makeText(MobileVerifyActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                return;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject ack = new JSONObject(s);

                        String decData = Helper.profileDecrypt(ack.get("Data").toString(), ack.get("Hash").toString());
                        if (!Helper.verify(decData, ack.get("Sign").toString(), JSONParserString.publickey)) {
                            Toast.makeText(MobileVerifyActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            JSONObject obj = new JSONObject(decData);
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(MobileVerifyActivity.this, obj.getString(TAG_MSG), Toast.LENGTH_LONG).show();
                                //getting the user from the response
                                editor.putString(TAG_USERNAME, username).apply();
                                editor.putBoolean(TAG_ISFIRSTSTART, false).apply();
//                                editor.putString(TAG_FIRSTNAME, firstname).apply();
//                                editor.putString(TAG_LASTNAME, lastname).apply();
                                editor.putString(TAG_GAMEUSERNAME, gameusername).apply();
                      //          editor.putString(TAG_EMAIL, email).apply();
                                editor.putString(TAG_MOBILE, mobile).apply();
                                editor.putString(TAG_PROMOCODE, promocode).apply();
                                startActivity(new Intent(MobileVerifyActivity.this, LoginActivity.class));
                                Toast.makeText(MobileVerifyActivity.this, obj.getString(TAG_MSG), Toast.LENGTH_SHORT).show();
                                finish();

                            } else {
                                Toast.makeText(MobileVerifyActivity.this, obj.getString(TAG_MSG), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private class OneLoadAllProductsResetPass extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MobileVerifyActivity.this);
            pDialog.setMessage("Loading Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            JSONObject params = new JSONObject();

            String rq = null;
            try {
                params.put(TAG_MOBILE, phoneed.getText().toString().trim());
                params.put(TAG_PASSWORD, newPass.getText().toString());

                // getting JSON string from URL
                rq = jsonParser.makeHttpRequest(urlresetpass, params);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return rq;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String s) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();

            if (s == null || s.isEmpty()) {
                Toast.makeText(MobileVerifyActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                return;
            }

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /*
                      Updating parsed JSON data into ListView
                     */
                    try {
                        JSONObject ack = new JSONObject(s);
                        // Log.d("test", String.valueOf(ack));
                        String decData = Helper.profileDecrypt(ack.get("Data").toString(), ack.get("Hash").toString());
                        if (!Helper.verify(decData, ack.get("Sign").toString(), JSONParserString.publickey)) {
                            Toast.makeText(MobileVerifyActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            JSONObject obj = new JSONObject(decData);
                            success = obj.getInt("success");
                            if (success == 1) {
                                Intent intent = new Intent(MobileVerifyActivity.this, LoginActivity.class);
                                startActivity(intent);
                                Toast.makeText(MobileVerifyActivity.this, "Password changed Succsessfully", Toast.LENGTH_LONG).show();

                            } else if (success == 2) {
                                // no offers found
                                Toast.makeText(MobileVerifyActivity.this, "Mobile Number Not Registered", Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(MobileVerifyActivity.this, "Something went wrong.Please try again!", Toast.LENGTH_LONG).show();

                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });

        }
    }
}
