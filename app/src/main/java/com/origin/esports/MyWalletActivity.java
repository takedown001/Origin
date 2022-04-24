package com.origin.esports;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.origin.esports.Originconfig.URL;

import com.origin.esports.fragment.AddMoneyFragment;
import com.origin.esports.fragment.TransactionsFragment;
import com.origin.esports.fragment.WithdrawFragment;

import com.google.android.material.tabs.TabLayout;
import com.payu.india.Extras.PayUChecksum;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PayuResponse;
import com.payu.india.Model.PostData;
import com.payu.india.Model.TransactionDetails;
import com.payu.india.Model.TransactionResponse;
import com.payu.india.Payu.Payu;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.payuui.Activity.PayUBaseActivity;
import com.payu.payuui.SdkuiUtil.SdkUIConstants;


import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;



import retrofit2.Call;
import retrofit2.Callback;

public class MyWalletActivity extends AppCompatActivity{

    // Progress Dialog
    private ProgressDialog pDialog;
    private String msgs;
    // Creating JSON Parser object
    private final JSONParserString jsonParser = new JSONParserString();
    // url to get all products list
    private static final String url = URL.Payment;

    // JSON Node names
    private static final String TAG_SUCCESS = URL.SUCCESS;

    //user
    private static final String TAG_USERID = URL.USERID;
    private static final String TAG_USERNAME = URL.USERNAME;
    private static final String TAG_MOBILE = URL.MOBILE;

    //balance
    private static final String TAG_USERBALANCE = URL.BALANCE;

    private static final String TAG_INSTA_ORDERID = "instaorderid";

    private String balance;
    private LinearLayout main;
    private String number;
    private TabLayout tabLayout;
    private String username;
    private ViewPager viewPager;
    private TextView walletBalance;
    private PaymentParams mPaymentParams;
    private int success;
    PayuHashes payuHashes = new PayuHashes();
    private String msg;


    String TAG ="mainActivity", txnid ="txt12346", amount ="20", phone ="9144040888",
            prodname ="BlueAppCourse", firstname ="kamal",email= "ppro8055@gmail.com",
          merchantkey=URL.MerchantKey;
    public void PaytmAddMoney(String amt,String username,String num) {
        mPaymentParams = new PaymentParams();
        SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);

        phone = num;
        amount = amt;
        prodname = "AddMoney";
        firstname = username ;

        final int min = 1000;
        final int max = 10000;
        final int random = new Random().nextInt((max - min) + 1) + min;
        txnid = "txt"+ shred.getInt(TAG_USERID,0) +random;
        startpay();

    }

    public void startpay(){

        mPaymentParams.setKey(merchantkey);
        mPaymentParams.setAmount(amount);
        mPaymentParams.setProductInfo(prodname);
        mPaymentParams.setFirstName(firstname);
        mPaymentParams.setEmail(email);
        mPaymentParams.setPhone(phone);
        mPaymentParams.setTxnId(txnid);
        mPaymentParams.setSurl("https://www.payumoney.com/mobileapp/payumoney/success.php");    // Success URL (surl)
        mPaymentParams.setFurl("https://www.payumoney.com/mobileapp/payumoney/failure.php");     //Failure URL (furl)
        mPaymentParams.setUdf1("udf1");
        mPaymentParams.setUdf2("udf2");
        mPaymentParams.setUdf3("udf3");
        mPaymentParams.setUdf4("udf4");
        mPaymentParams.setUdf5("udf5");
        try {

            new GetHash().execute();

        } catch (Exception e) {
            Log.e(TAG, " error s "+e.toString());
        }

    }

    class GetHash extends AsyncTask<Void, Void, String> {




        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MyWalletActivity.this);
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
                params.put("txnid",txnid);
                params.put("amount",amount);
                params.put("productinfo",prodname);
                params.put("firstname",firstname);
                rq = jsonParser.makeHttpRequest(URL.Hash, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //returing the response
            return rq;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            if (s == null || s.isEmpty()) {
                Toast.makeText(MyWalletActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                JSONObject ack = new JSONObject(s);
                String decData = Helper.profileDecrypt(ack.get("Data").toString(), ack.get("Hash").toString());
                if (!Helper.verify(decData, ack.get("Sign").toString(), JSONParserString.publickey)) {
                    Toast.makeText(MyWalletActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                    return;
                } else {

                    JSONObject obj = new JSONObject(decData);
//                    Log.d("Hash",obj.toString());
//                    Log.d("Hash" , calculateHash("xVadIpa2|txt12346|20|BlueAppCourse|kamal|ppro8055@gmail.com|udf1|udf2|udf3|udf4|udf5||||||yDxvb85n3p"));
//                    Log.d("Hash",obj.getString(PayuConstants.PAYMENT_PARAMS));
                    if(!obj.getString(PayuConstants.PAYMENT_PARAMS).isEmpty()) {
                        payuHashes.setPaymentHash(obj.getString(PayuConstants.PAYMENT_PARAMS));
                    }
                    if(!obj.getString(PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK).isEmpty()) {
                        payuHashes.setPaymentRelatedDetailsForMobileSdkHash(obj.getString(PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK));
                   }
                    if(!obj.getString(PayuConstants.VAS_FOR_MOBILE_SDK).isEmpty()) {
                        payuHashes.setVasForMobileSdkHash(obj.getString(PayuConstants.VAS_FOR_MOBILE_SDK));
                    }
                    launchSdkUI(payuHashes);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
            if (data != null && resultCode == RESULT_OK && requestCode == PayuConstants.PAYU_REQUEST_CODE) {
                try {
                    JSONObject jsonObject = new JSONObject(data.getStringExtra(PayuConstants.PAYU_RESPONSE));
                    if(jsonObject.getString(PayuConstants.STATUS).equals(PayuConstants.SUCCESS.toLowerCase())){
                        new OneLoadAllProducts().execute();
                        Toast.makeText(MyWalletActivity.this,PayuConstants.SUCCESS, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MyWalletActivity.this,"Transaction Fa", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, getString(R.string.could_not_receive_data), Toast.LENGTH_LONG).show();
            }
    }

    public void launchSdkUI(PayuHashes payuHashes) {
        Intent intent = new Intent(this, PayUBaseActivity.class);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);
         intent.putExtra(PayuConstants.ENV,PayuConstants.STAGING_ENV);
        startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
    }
    private String calculateHash(String hashString) {
        try {
            StringBuilder hash = new StringBuilder();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(hashString.getBytes());
            byte[] mdbytes = messageDigest.digest();
            for (byte hashByte : mdbytes) {
                hash.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
            }
            return hash.toString();
        } catch (Exception e) {
            return "ERROR";
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);
        SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        TextView playcoin = findViewById(R.id.playcoin);
        String playbal = String.valueOf(shred.getInt("balance",0));
        playcoin.setText(playbal);



        walletBalance = (TextView) findViewById(R.id.walletBalance);
        main = (LinearLayout) findViewById(R.id.mainLayout);
        balance = String.valueOf(shred.getInt(TAG_USERBALANCE,0));
        username = shred.getString(TAG_USERNAME,"");
        number = shred.getString(TAG_MOBILE,"");
        walletBalance.setText(""+balance);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        ImageButton notifymw = findViewById(R.id.notify);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        notifymw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
            }
        });
        //Paypal
//        if(URL.paypal) {
//            new HttpRequest().execute();
//        }

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AddMoneyFragment(), "Add Money");
        adapter.addFragment(new WithdrawFragment(), "Withdraw");
        adapter.addFragment(new TransactionsFragment(), "Transactions");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    class OneLoadAllProducts extends AsyncTask<String, String, String> {
        SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MyWalletActivity.this);
            pDialog.setMessage("Loading Please wait...");
            pDialog.setIndeterminate(true);
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


                params.put(TAG_USERID,shred.getInt(TAG_USERID, 0));
                params.put("addamount", amount);
                params.put(TAG_INSTA_ORDERID, txnid);
                params.put("status", 1);
                rq = jsonParser.makeHttpRequest(url, params);

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
                Toast.makeText(MyWalletActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                return;
            }
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                    try {
                        JSONObject ack = new JSONObject(s);

                        String decData = Helper.profileDecrypt(ack.get("Data").toString(), ack.get("Hash").toString());
                        if (!Helper.verify(decData, ack.get("Sign").toString(), JSONParserString.publickey)) {
                            Toast.makeText(MyWalletActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            Log.d("test", String.valueOf(ack));
                            JSONObject json = new JSONObject(decData);
                            // Checking for SUCCESS TAG
                            success = json.getInt(TAG_SUCCESS);
                            msg = json.getString("message");
                            if (success == 1) {
                                // jsonarray found
                                // Getting Array of jsonarray
                                String s = amount;
                                double d = Double.parseDouble(s);
                                int i = (int) d;

                                int bal = shred.getInt(TAG_USERBALANCE, 0) + i;
                                shred.edit().putInt(TAG_USERBALANCE, bal).apply();

                                Intent intent = new Intent(MyWalletActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);


                                Toast.makeText(MyWalletActivity.this, msg, Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(MyWalletActivity.this, msg, Toast.LENGTH_LONG).show();

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

//PayPal
//
//    class OneLoadAllProductsPayPal extends AsyncTask<String, String, String> {
//        SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);
//        /**
//         * Before starting background thread Show Progress Dialog
//         */
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            if (pDialog != null && pDialog.isShowing()) {
//                pDialog = new ProgressDialog(MyWalletActivity.this);
//                pDialog.setMessage("Loading Please wait...");
//                pDialog.setIndeterminate(true);
//                pDialog.setCancelable(false);
//                pDialog.show();
//            }
//        }
//
//        /**
//         * getting All products from url
//         */
//        protected String doInBackground(String... args) {
//            // Building Parameters
//            Map<String, String> params = new HashMap<>();
//            params.put(TAG_USERID, String.valueOf(shred.getInt(TAG_USERID,0)));
//            params.put("addamount", amount);
//            params.put(TAG_INSTA_ORDERID, stringNonce);
//            params.put(TAG_INSTA_TXNID, "111");
//            params.put(TAG_INSTA_PAYMENTID, stringNonce);
//            params.put(TAG_INSTA_TOKEN, "PayPal");
//            params.put("status", "Add Money Success");
//
//            // getting JSON string from URL
//            JSONObject json = jsonParser.makeHttpRequest(url, params);
//
//            // Check your log cat for JSON reponse
////            Log.d("All jsonarray: ", json.toString());
//
//            try {
//                // Checking for SUCCESS TAG
//                success = json.getInt(TAG_SUCCESS);
//                msgs = json.getString("message");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        /**
//         * After completing background task Dismiss the progress dialog
//         **/
//        protected void onPostExecute(String file_url) {
//            // dismiss the dialog after getting all products
//            pDialog.dismiss();
//
//            // updating UI from Background Thread
//            runOnUiThread(new Runnable() {
//                public void run() {
//                /*
//                  Updating parsed JSON data into ListView
//                 */
//                    if (success == 1) {
//                        // jsonarray found
//                        // Getting Array of jsonarray
//                        String s = paypalamount;
//                        double d = Double.parseDouble(s);
//                        int i = (int) d;
//
//                        int bal = shred.getInt(TAG_USERBALANCE,0)+ i;
//                        shred.edit().putInt(TAG_USERBALANCE,bal).apply();
//
//                        Intent intent = new Intent(MyWalletActivity.this, HomeActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//
//
//                        Toast.makeText(MyWalletActivity.this,msgs, Toast.LENGTH_LONG).show();
//
//                    } else {
//                        Toast.makeText(MyWalletActivity.this, msgs, Toast.LENGTH_LONG).show();
//
//                    }
//
//                }
//            });
//
//        }
//
//    }

//Paypal

//    public void onBraintreeSubmit(String email, String phone, String amount, String purpose, String buyername) {
//
//        paypalamount = amount;
//
//        DropInRequest dropInRequest = new DropInRequest()
//                .clientToken(token);
//        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
//    }

//    class OneLoadAllProductsPayPalSendNonceDetails extends AsyncTask<String, String, String> {
//
//        /**
//         * Before starting background thread Show Progress Dialog
//         */
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(MyWalletActivity.this);
//            pDialog.setMessage("Loading Please wait...");
//            pDialog.setIndeterminate(true);
//            pDialog.setCancelable(false);
//            pDialog.show();
//        }
//
//        /**
//         * getting All products from url
//         */
//        protected String doInBackground(String... args) {
//            // Building Parameters
//
//            // getting JSON string from URL
//            String json = jsonParserString.makeHttpRequest(send_payment_details,paramHash);
//
//            // Check your log cat for JSON reponse
////            Log.d("All jsonarray: ", json.toString());
//
//            try {
//                // Checking for SUCCESS TAG
//                if (json.contains("Successful")) {
//                    Toast.makeText(MyWalletActivity.this, "Transaction successful", Toast.LENGTH_LONG).show();
//                } else
//                    Toast.makeText(MyWalletActivity.this, "Transaction failed", Toast.LENGTH_LONG).show();
//                Log.d("mylog", "Final Response: " + json.toString());
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        /**
//         * After completing background task Dismiss the progress dialog
//         **/
//        protected void onPostExecute(String file_url) {
//            // dismiss the dialog after getting all products
//            pDialog.dismiss();
//
//            // updating UI from Background Thread
//            runOnUiThread(new Runnable() {
//                public void run() {
//                /*
//                  Updating parsed JSON data into ListView
//                 */
//                    // Loading jsonarray in Background Thread
//                    new OneLoadAllProductsPayPal().execute();
//
//
//                }
//            });
//
//        }
//
//    }

//    private class HttpRequest extends AsyncTask {
//        ProgressDialog progress;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progress = new ProgressDialog(MyWalletActivity.this, android.R.style.Theme_DeviceDefault_Dialog);
//            progress.setCancelable(false);
//            progress.setMessage("We are contacting our servers for token, Please wait");
//            progress.setTitle("Getting token");
//            progress.show();
//        }
//
//        @Override
//        protected Object doInBackground(Object[] objects) {
//            HttpClient client = new HttpClient();
//            client.get(get_token, new HttpResponseCallback() {
//                @Override
//                public void success(String responseBody) {
//                    Log.d("mylog", responseBody);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                           Toast.makeText(MyWalletActivity.this, "Successfully got token", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    token = responseBody;
//                }
//
//                @Override
//                public void failure(Exception exception) {
//                    final Exception ex = exception;
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            System.out.println("Takedown_paypal_gettoken_failed" + ex.toString());
//                            Toast.makeText(MyWalletActivity.this, "Failed to get token: ", Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
//            });
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Object o) {
//            super.onPostExecute(o);
//            progress.dismiss();
//        }
//    }



