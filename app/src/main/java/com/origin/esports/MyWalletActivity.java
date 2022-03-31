package com.origin.esports;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;

import org.json.JSONObject;

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
    private final JSONParserString jsonParserString = new JSONParserString();

    // url to get all products list
    private static final String url = URL.Payment;

    // JSON Node names
    private static final String TAG_SUCCESS = URL.SUCCESS;

    //user
    private static final String TAG_USERID = URL.USERID;
    private static final String TAG_USERNAME = URL.USERNAME;
    private static final String TAG_EMAIL = URL.EMAIL;
    private static final String TAG_MOBILE = URL.MOBILE;

    //balance
    private static final String TAG_USERBALANCE = URL.BALANCE;

    //instamojo
    private static final String TAG_INSTA_ORDERID = "instaorderid";
    private static final String TAG_INSTA_TXNID = "instatxnid";
    private static final String TAG_INSTA_PAYMENTID = "instapaymentid";
    private static final String TAG_INSTA_TOKEN = "instatoken";

    private String balance;
    private String email;
    private LinearLayout main;
    private String number;
    private TabLayout tabLayout;
    private String username;
    private ViewPager viewPager;
    private TextView walletBalance;


   private int success;
    //paytm

    private String hash;
    private String msg;

    //Paypal
    final int REQUEST_CODE = 1;
    final String get_token = URL.mainurl + "paypal/main.php";
    final String send_payment_details = URL.mainurl + "paypal/checkout.php";
    String token, paypalamount;
    HashMap<String, String> paramHash;
    public String stringNonce;


    PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
    //declare paymentParam object
    PayUmoneySdkInitializer.PaymentParam paymentParam = null;

    String TAG ="mainActivity", txnid ="txt12346", amount ="20", phone ="9144040888",
            prodname ="BlueApp Course", firstname ="kamal",
            merchantId = URL.MerchantId, merchantkey=URL.MerchantKey;  //   first test key only
    public void PaytmAddMoney(String amt,String e,String o,String n) {
        SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);
        email= "ppro8055@gmail.com";
        phone = o;
        amount = amt;
        prodname = "Add Money To Wallet";
        firstname = n ;

        final int min = 1000;
        final int max = 10000;
        final int random = new Random().nextInt((max - min) + 1) + min;
        txnid = "txt"+ shred.getInt(TAG_USERID,0) +random;
        startpay();

    }

    public void startpay(){

        builder.setAmount(amount)                          // Payment amount
                .setTxnId(txnid)                     // Transaction ID
                .setPhone(phone)                   // User Phone number
                .setProductName(prodname)                   // Product Name or description
                .setFirstName(username)                              // User First name
                .setEmail(email)              // User Email ID
                .setsUrl("https://www.payumoney.com/mobileapp/payumoney/success.php")     // Success URL (surl)
                .setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")     //Failure URL (furl)
                .setUdf1("")
                .setUdf2("")
                .setUdf3("")
                .setUdf4("")
                .setUdf5("")
                .setUdf6("")
                .setUdf7("")
                .setUdf8("")
                .setUdf9("")
                .setUdf10("")
                .setIsDebug(false)                              // Integration environment - true (Debug)/ false(Production)
                .setKey(merchantkey)                        // Merchant key
                .setMerchantId(merchantId);


        try {
            paymentParam = builder.build();
            // generateHashFromServer(paymentParam );
            getHashkey();

        } catch (Exception e) {
            Log.e(TAG, " error s "+e.toString());
        }

    }
    public void getHashkey(){
        ServiceWrapper service = new ServiceWrapper(null);
        Call<String> call = service.newHashCall(merchantkey, txnid, amount, prodname,
                username, email);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
          //      Log.e(TAG, "hash res "+response.body());
                String merchantHash= response.body();
                if (merchantHash.isEmpty() || merchantHash.equals("")) {
                    Toast.makeText(MyWalletActivity.this, "Could not generate hash", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "hash empty");
                } else {
                    // mPaymentParams.setMerchantHash(merchantHash);
                    hash = merchantHash;
                    paymentParam.setMerchantHash(merchantHash);
                    // Invoke the following function to open the checkout page.
                    // PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, StartPaymentActivity.this,-1, true);
                    PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, MyWalletActivity.this, R.style.AppTheme_default, false);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "hash error "+ t.toString());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
// PayUMoneySdk: Success -- payuResponse{"id":225642,"mode":"CC","status":"success","unmappedstatus":"captured","key":"9yrcMzso","txnid":"223013","transaction_fee":"20.00","amount":"20.00","cardCategory":"domestic","discount":"0.00","addedon":"2018-12-31 09:09:43","productinfo":"a2z shop","firstname":"kamal","email":"kamal.bunkar07@gmail.com","phone":"9144040888","hash":"b22172fcc0ab6dbc0a52925ebbd0297cca6793328a8dd1e61ef510b9545d9c851600fdbdc985960f803412c49e4faa56968b3e70c67fe62eaed7cecacdfdb5b3","field1":"562178","field2":"823386","field3":"2061","field4":"MC","field5":"167227964249","field6":"00","field7":"0","field8":"3DS","field9":" Verification of Secure Hash Failed: E700 -- Approved -- Transaction Successful -- Unable to be determined--E000","payment_source":"payu","PG_TYPE":"AXISPG","bank_ref_no":"562178","ibibo_code":"VISA","error_code":"E000","Error_Message":"No Error","name_on_card":"payu","card_no":"401200XXXXXX1112","is_seamless":1,"surl":"https://www.payumoney.com/sandbox/payment/postBackParam.do","furl":"https://www.payumoney.com/sandbox/payment/postBackParam.do"}
//PayUMoneySdk: Success -- merchantResponse438104
// on successfull txn
        //  request code 10000 resultcode -1
        //tran {"status":0,"message":"payment status for :438104","result":{"postBackParamId":292490,"mihpayid":"225642","paymentId":438104,"mode":"CC","status":"success","unmappedstatus":"captured","key":"9yrcMzso","txnid":"txt12345","amount":"20.00","additionalCharges":"","addedon":"2018-12-31 09:09:43","createdOn":1546227592000,"productinfo":"a2z shop","firstname":"kamal","lastname":"","address1":"","address2":"","city":"","state":"","country":"","zipcode":"","email":"kamal.bunkar07@gmail.com","phone":"9144040888","udf1":"","udf2":"","udf3":"","udf4":"","udf5":"","udf6":"","udf7":"","udf8":"","udf9":"","udf10":"","hash":"0e285d3a1166a1c51b72670ecfc8569645b133611988ad0b9c03df4bf73e6adcca799a3844cd279e934fed7325abc6c7b45b9c57bb15047eb9607fff41b5960e","field1":"562178","field2":"823386","field3":"2061","field4":"MC","field5":"167227964249","field6":"00","field7":"0","field8":"3DS","field9":" Verification of Secure Hash Failed: E700 -- Approved -- Transaction Successful -- Unable to be determined--E000","bank_ref_num":"562178","bankcode":"VISA","error":"E000","error_Message":"No Error","cardToken":"","offer_key":"","offer_type":"","offer_availed":"","pg_ref_no":"","offer_failure_reason":"","name_on_card":"payu","cardnum":"401200XXXXXX1112","cardhash":"This field is no longer supported in postback params.","card_type":"","card_merchant_param":null,"version":"","postUrl":"https:\/\/www.payumoney.com\/mobileapp\/payumoney\/success.php","calledStatus":false,"additional_param":"","amount_split":"{\"PAYU\":\"20.0\"}","discount":"0.00","net_amount_debit":"20","fetchAPI":null,"paisa_mecode":"","meCode":"{\"vpc_Merchant\":\"TESTIBIBOWEB\"}","payuMoneyId":"438104","encryptedPaymentId":null,"id":null,"surl":null,"furl":null,"baseUrl":null,"retryCount":0,"merchantid":null,"payment_source":null,"pg_TYPE":"AXISPG"},"errorCode":null,"responseCode":null}---438104

        // Result Code is -1 send from Payumoney activity
 //       Log.e("StartPaymentActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra( PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE );

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if(transactionResponse.getTransactionStatus().equals( TransactionResponse.TransactionStatus.SUCCESSFUL )){

                    //Success Transaction
                    Toast.makeText(MyWalletActivity.this,"Transaction Successful",Toast.LENGTH_LONG).show();
                    new OneLoadAllProducts().execute();
                } else{
                    //Failure Transaction
                    Toast.makeText(MyWalletActivity.this,"Transaction failed",Toast.LENGTH_LONG).show();

                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();
                Log.e(TAG, "tran "+payuResponse+"---"+ merchantResponse);
            } /* else if (resultModel != null && resultModel.getError() != null) {
                Log.d(TAG, "Error response : " + resultModel.getError().getTransactionResponse());
            } else {
                Log.d(TAG, "Both objects are null!");
            }*/
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
        // Call the function callInstamojo to start payment here


        walletBalance = (TextView) findViewById(R.id.walletBalance);
        main = (LinearLayout) findViewById(R.id.mainLayout);
        balance = String.valueOf(shred.getInt(TAG_USERBALANCE,0));
        username = shred.getString(TAG_USERNAME,"");
        email = shred.getString(TAG_EMAIL,"");
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



