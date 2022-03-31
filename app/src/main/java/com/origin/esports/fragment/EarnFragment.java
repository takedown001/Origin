package com.origin.esports.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.origin.esports.Helper;
import com.origin.esports.HomeActivity;
import com.origin.esports.Originconfig.URL;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.origin.esports.R;
import com.origin.esports.JSONParserString;
import static android.content.Context.MODE_PRIVATE;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EarnFragment extends Fragment {

    // JSON Node names
    private static final String TAG_ERROR = "error";
    private ProgressDialog pDialog;
    private static final String url = URL.Payment;

    //Prefrance
    private String msg,txnid;
    private int success;
    private final int reward= 1;
    private static final String TAG_INSTA_ORDERID = "instaorderid";
    private Context context;
    private TextView referCode;
    private TextView watcads;
    private static final String TAG_USERID = URL.USERID;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USERNAME = URL.USERNAME;

    //balance
    private static final String TAG_USERBALANCE = URL.BALANCE;
    private Button referNow;
    AdRequest adRequest;
    JSONParserString jsonParser = new JSONParserString();
    private RewardedInterstitialAd mRewardedAd;
    public EarnFragment() {
        // Required empty public constructor
    }

    public static EarnFragment newInstance(String param1, String param2) {
        EarnFragment fragment = new EarnFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        SharedPreferences  shred = this.getActivity().getSharedPreferences("userdetails", Context.MODE_PRIVATE);
        // Inflate the layout for this fragment
        View rootViewone = inflater.inflate(R.layout.fragment_earn, container, false);
        referNow = (Button) rootViewone.findViewById(R.id.referButton);
        watcads = (TextView) rootViewone.findViewById(R.id.watchads);
        referCode = (TextView) rootViewone.findViewById(R.id.referCode);
        referCode.setText(shred.getString(TAG_USERNAME,"Not Found"));
        adRequest = new AdRequest.Builder().build();
        referNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+getActivity().getPackageName().toString()));
                startActivity(browserIntent);
            }
        });
        watcads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRewardedAd != null) {
                    mRewardedAd.show(getActivity(), new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            new OneLoadAllProducts().execute();
                        }
                    });

                }else{
                    Toast.makeText(getActivity(),"Ads Not Available",Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(shred.getInt("reward",0)<=1){
            loadreward();
        }else{
            try {
                watcads.setText(time());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return rootViewone;
    }
    public String time () throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String time1 = "23:59";
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        Date date1 = format.parse(time1);
        Date date2 = format.parse(currentTime);
        long millis = date1.getTime() - date2.getTime();
        int hours = (int) (millis / (1000 * 60 * 60));
        int mins = (int) ((millis / (1000 * 60)) % 60);

        return hours + " Hrs : " + mins+" Min";
    }
    public void loadreward (){

        RewardedInterstitialAd.load(getActivity(),
                        "ca-app-pub-2563787493982341/8793237396",
                adRequest,  new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onRewardedInterstitialAdFailedToLoad(int i) {
                        super.onRewardedInterstitialAdFailedToLoad(i);
                        watcads.setText("Ads Not Available ! Please Try Again Later");
                    }

                    @Override
                    public void onRewardedInterstitialAdLoaded(@NonNull RewardedInterstitialAd rewardedInterstitialAd) {
                        super.onRewardedInterstitialAdLoaded(rewardedInterstitialAd);
                        mRewardedAd = rewardedInterstitialAd;
                    }
                });
    }

    class OneLoadAllProducts extends AsyncTask<String, String, String> {
        SharedPreferences shred =getActivity().getSharedPreferences("userdetails", MODE_PRIVATE);

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
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
                params.put(TAG_INSTA_ORDERID, txnid);
                params.put("status",2);
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
                Toast.makeText(context, "Server Error", Toast.LENGTH_LONG).show();
                return;
            }
            // updating UI from Background Thread
           getActivity().runOnUiThread(new Runnable() {
                public void run() {

                    try {
                        JSONObject ack = new JSONObject(s);

                        String decData = Helper.profileDecrypt(ack.get("Data").toString(), ack.get("Hash").toString());
                        if (!Helper.verify(decData, ack.get("Sign").toString(), JSONParserString.publickey)) {
                            Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            JSONObject json = new JSONObject(decData);
                            // Checking for SUCCESS TAG
                            success = json.getInt(TAG_SUCCESS);
                            msg = json.getString("message");
                            if (success == 1) {
                                int bal = shred.getInt(TAG_USERBALANCE, 0) + reward;
                                shred.edit().putInt(TAG_USERBALANCE, bal).apply();
                                Intent intent = new Intent(context, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Toast.makeText(context, "One Coin Added To the Wallet", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

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
