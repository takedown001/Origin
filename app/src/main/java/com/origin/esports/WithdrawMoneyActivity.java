package com.origin.esports;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;

import com.origin.esports.Originconfig.URL;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;




import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class WithdrawMoneyActivity extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    private final JSONParserString jsonParser = new JSONParserString();

    private ArrayList<HashMap<String, String>> offersList;

    // url to get all products list
    private static final String url = URL.Withdraw;

    // JSON Node names
    private static final String TAG_SUCCESS = URL.SUCCESS;

    //user
    private static final String TAG_USERID = URL.USERID;
    private static final String TAG_USERNAME = URL.USERNAME;
    private static final String TAG_MOBILE = URL.MOBILE;

    //balance
    private static final String TAG_WINMONEY = URL.WINMONEY;


    // products JSONArray
    JSONArray jsonarray = null;

    private String message;

    private int success;

    //new
    private Context context;

    private String AmountToWithdraw;
    private String availableBalance;
    private TextView errorMessage;
    private String paytmNo;
    private TextView start;
    private TextInputEditText paytmNumber;
    private String username;
    private Button withdraw;
    private TextInputEditText withdrawAmount;
    private int withdrawalAmount;

    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_money);
        SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);
        context = getApplicationContext();

        mode = getIntent().getStringExtra("mode");



        // Hashmap for ListView
        offersList = new ArrayList<>();

        start = (TextView) findViewById(R.id.start);
        paytmNumber = (TextInputEditText) findViewById(R.id.numberWithdraw);
        withdrawAmount = (TextInputEditText) findViewById(R.id.amountWithdraw);
        withdraw = (Button) findViewById(R.id.withdrawButton);
        errorMessage = (TextView) findViewById(R.id.errorMsg);

        username = shred.getString(TAG_USERNAME,"");
        paytmNo = shred.getString(TAG_MOBILE,"");
        availableBalance = String.valueOf(shred.getInt(TAG_WINMONEY,0));

        if(mode.equalsIgnoreCase("paytm")) {
            paytmNumber.setText(paytmNo);
            paytmNumber.setHint("Paytm Number");
        } else if(mode.equalsIgnoreCase("paypal")) {
//            paytmNumber.setText(paytmNo);
            start.setText("ID : ");
            paytmNumber.setHint("Enter PayPal ID");
        } else if(mode.equalsIgnoreCase("googlepay")) {
            paytmNumber.setText(paytmNo);
            paytmNumber.setHint("Google Pay Number");
        } else if(mode.equalsIgnoreCase("phonepay")) {
            paytmNumber.setText(paytmNo);
            paytmNumber.setHint("PhonePe Number");
        }

        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paytmNo = paytmNumber.getText().toString();
                AmountToWithdraw = withdrawAmount.getText().toString().trim();
                if (!AmountToWithdraw.isEmpty()) {
                    withdrawalAmount = Integer.parseInt(AmountToWithdraw);
                    submitWithdrawData();
                    return;
                }
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText("Enter Withdrawal Amount");
                errorMessage.setTextColor(Color.parseColor("#ff0000"));
            }
        });

    }

    private void submitWithdrawData() {
        if(mode.equalsIgnoreCase("paypal")) {
            if(validateWithdrawalAmount()) {
                new OneLoadAllProducts().execute();
            }
        } else {
            if (validatePaytmNumber() && validateWithdrawalAmount()) {
                new OneLoadAllProducts().execute();
            }
        }
    }

    private boolean validateWithdrawalAmount() {
        if (withdrawalAmount > Integer.parseInt(availableBalance)) {
            errorMessage.setText("withdrawal amount is greater than Available balance");
            errorMessage.setTextColor(Color.parseColor("#ff0000"));
            errorMessage.setVisibility(View.VISIBLE);
            return false;
        }
        if (withdrawalAmount >= 100) {
            return true;
        }
        errorMessage.setText("Minimum withdrawal amount is ₹ 100.");
        errorMessage.setTextColor(Color.parseColor("#ff0000"));
        errorMessage.setVisibility(View.VISIBLE);
        return false;
    }

    private boolean validatePaytmNumber() {
        if (paytmNo.length() <= 10) {
            if (paytmNo.length() >= 10) {
                return true;
            }
        }
        paytmNumber.setError("Paytm Number should be of 10 Digits");
        return false;
    }

    /**
     * Prepares sample data to provide data set to adapter
     */

    class OneLoadAllProducts extends AsyncTask<String, String, String> {
        SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(WithdrawMoneyActivity.this);
            pDialog.setMessage("Loading Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters

            JSONObject params = new JSONObject();
            String rq = null;
            try {

            params.put(TAG_USERID, String.valueOf(shred.getInt(TAG_USERID,0)));
            params.put("mode",mode);
            params.put("paytmnumber",paytmNo);
            params.put("withdrawamount", String.valueOf((-1)*withdrawalAmount));
            params.put("status", "Withdrawal Pending");
            // getting JSON string from URL
            rq = jsonParser.makeHttpRequest(url, params);

                // Checking for SUCCESS TAG

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return rq;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String s) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            if (s == null || s.isEmpty()) {
                Toast.makeText(WithdrawMoneyActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                return;
            }
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                    try {
                        JSONObject ack = new JSONObject(s);
                        // Log.d("test", String.valueOf(ack));
                        String decData = Helper.profileDecrypt(ack.get("Data").toString(), ack.get("Hash").toString());
                        if (!Helper.verify(decData, ack.get("Sign").toString(), JSONParserString.publickey)) {
                            Toast.makeText(WithdrawMoneyActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            JSONObject json = new JSONObject(decData);
                            success = json.getInt(TAG_SUCCESS);
                            message = json.getString("message");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (success == 1) {
                        // jsonarray found
                        // Getting Array of jsonarray
                        int bal = shred.getInt(TAG_WINMONEY,0)-withdrawalAmount;
                        shred.edit().putInt(TAG_WINMONEY,bal).apply();

                        Intent intent = new Intent(context, HomeActivity.class);
                        startActivity(intent);


                        Toast.makeText(context,message ,Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(context,message,Toast.LENGTH_LONG).show();

                    }

                }
            });

        }

    }
}
