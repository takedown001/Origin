package com.origin.esports;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.origin.esports.Originconfig.URL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinMatchConfirmationActivity extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    private final JSONParserString jsonParser = new JSONParserString();

    // url to get all products list
    private static final String url = URL.JoinMatch;

    // JSON Node names
    private static final String TAG_SUCCESS = URL.SUCCESS;
    private static final String TAG_MESSAGE = URL.MSG;

    //user
    private static final String TAG_USERID = URL.USERID;
    private static final String TAG_USERNAME = URL.USERNAME;
    private static final String TAG_DEDUCT = "deduct";
    //balance
    private static final String TAG_USERBALANCE = "balance";
    private static final String TAG_WINMONEY = "winmoney";
    //match
    private static final String TAG_MATCHID = "matchid";
    private int deduct;

    private int success;

    private String accountBalance;
    private Button addMoney;
    private LinearLayout addMoneyLL;
    private TextView balance;
    private TextView balanceStatus;
    private Button cancel;
    private String entryFee;
    private TextView entryfee;
    private int fee;
    private String joinStatus;
    private String matchID;
    private String matchType;
    private int myBalance;
    private int mywithdrawbal;
    private Button next;
    private LinearLayout nextButtonLL;
    private String privateStatus;
    private String username;
    private TextView withdraw;
    private String withdrawamt;
    private String message;
    //Prefrance


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_match_confirmation);

        SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);
        withdrawamt = String.valueOf(shred.getInt(TAG_WINMONEY, 0));
        withdraw = (TextView) findViewById(R.id.withdraw);
        next = (Button) findViewById(R.id.next);
        balance = (TextView) findViewById(R.id.balance);
        entryfee = (TextView) findViewById(R.id.entryFee);
        balanceStatus = (TextView) findViewById(R.id.statusTextView);
        nextButtonLL = (LinearLayout) findViewById(R.id.nextButtonLL);
        addMoneyLL = (LinearLayout) findViewById(R.id.addMoneyLL);
        addMoney = (Button) findViewById(R.id.addMoneyButton);
        cancel = (Button) findViewById(R.id.cancelButton);
        username = shred.getString(TAG_USERNAME, "");
        accountBalance = String.valueOf(shred.getInt(TAG_USERBALANCE, 0));
        matchID = getIntent().getStringExtra("matchID");
        matchType = getIntent().getStringExtra("matchType");
        entryFee = getIntent().getStringExtra("entryFee");
        joinStatus = getIntent().getStringExtra("JoinStatus");
        privateStatus = getIntent().getStringExtra("isPrivate");

        if (matchType.equals("Free")) {
            entryfee.setText("Free");
            entryfee.setTextColor(Color.parseColor("#1E7E34"));
        } else {
            entryfee.setText(entryFee);
        }
        if (joinStatus.equals("1")) {
            next.setText("Add New Entry");
        }
        withdraw.setText("₹" + withdrawamt);
        balance.setText(accountBalance);
        Matcher mbal = Pattern.compile("\\d+").matcher(accountBalance);
        while (mbal.find()) {
            myBalance = Integer.parseInt(mbal.group());
        }
        Matcher mentry = Pattern.compile("\\d+").matcher(entryFee);

        while (mentry.find()) {
            fee = Integer.parseInt(mentry.group());
        }
        Matcher wbal = Pattern.compile("\\d+").matcher(withdrawamt);
        while (wbal.find()) {
            mywithdrawbal = Integer.parseInt(wbal.group());
        }
        if (myBalance >= fee) {
            balanceStatus.setText(R.string.sufficientBalanceText);
            deduct = 0;
            next.setText("NEXT");
        } else if (mywithdrawbal >= fee) {
            balanceStatus.setText("You Have Sufficient withdraw Balance");
            deduct = 1;
            next.setText("NEXT");
        } else {
            balanceStatus.setTextColor(Color.parseColor("#ff0000"));
            balanceStatus.setText(R.string.insufficientBalanceText);
            addMoneyLL.setVisibility(View.VISIBLE);
            nextButtonLL.setVisibility(View.GONE);
        }
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Join Player in Match in Background Thread
                new JoinMatch().execute();
            }
        });
        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinMatchConfirmationActivity.this, MyWalletActivity.class);
                startActivity(intent);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    class JoinMatch extends AsyncTask<String, String, String> {
        SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = shred.edit();

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(JoinMatchConfirmationActivity.this);
            pDialog.setMessage("Loading Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         */
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        protected String doInBackground(String... args) {
            // Building Parameters
            JSONObject params = new JSONObject();
            String rq = null;
            try {
                params.put(TAG_USERID, String.valueOf(shred.getInt(TAG_USERID, 0)));
                params.put(TAG_USERNAME, shred.getString(TAG_USERNAME, ""));
                params.put(TAG_MATCHID, matchID);
                params.put(TAG_DEDUCT, String.valueOf(deduct));

                rq = jsonParser.makeHttpRequest(url, params);

                // Check your log cat for JSON reponse
//            Log.d("All Offers: ", json.toString());


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
         **/
        protected void onPostExecute(String s) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            if (s == null || s.isEmpty()) {
                Toast.makeText(JoinMatchConfirmationActivity.this, "Server Error", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(JoinMatchConfirmationActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            JSONObject obj = new JSONObject(decData);
                            success = obj.getInt(TAG_SUCCESS);
                            message = obj.getString(TAG_MESSAGE);

                            if (success == 1) {
                                // offers found
                                // Getting Array of offers
                                //update balance
                                if (deduct == 0) {
                                    int bal = shred.getInt(TAG_USERBALANCE, 0) - fee;
                                    editor.putInt(TAG_USERBALANCE, bal);
                                    editor.apply();
                                    Intent intent = new Intent(JoinMatchConfirmationActivity.this, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    int wbal = shred.getInt(TAG_WINMONEY, 0) - fee;
                                    editor.putInt(TAG_WINMONEY, wbal);
                                    editor.apply();
                                    Intent intent = new Intent(JoinMatchConfirmationActivity.this, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                                Toast.makeText(JoinMatchConfirmationActivity.this, message, Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(JoinMatchConfirmationActivity.this, message, Toast.LENGTH_LONG).show();

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
