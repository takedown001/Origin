package com.origin.esports;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;

import com.onesignal.OneSignal;
import android.content.Context;
import android.widget.Toast;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;


public class SplashScreenActivity extends Activity {

    private static final int SPLASH_SHOW_TIME = 2000;

    //Prefrance


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        MultiDex.install(this);

        new BackgroundSplashTask().execute();

    }

    private boolean checkVPN() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getNetworkInfo(ConnectivityManager.TYPE_VPN).isConnectedOrConnecting();


    }
    /**
     * Async Task: can be used to load DB, images during which the splash screen
     * is shown to user
     */
    private class BackgroundSplashTask extends AsyncTask<Void, Void, Void> {
        SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before making http calls

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                Thread.sleep(SPLASH_SHOW_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Create a new boolean and preference and set it to true
            boolean isFirstStart = shred.getBoolean("firstStart",true);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            if (checkVPN()) {
                Toast.makeText(SplashScreenActivity.this, "Turn Off Your Vpn", Toast.LENGTH_LONG).show();
            } else {
                // If the activity has never started before...
                if (isFirstStart) {

                    // Launch app intro
                    Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(i);

                    // Make a new preferences editor
                    shred.getBoolean("firstStart", false);

                } else {

                    // Create a new boolean and preference and set it to true
                    String isSignedin = shred.getString("username", "user Not Found");

                    if (!isSignedin.equalsIgnoreCase("")) {
                        //user signedin
                        Intent i = new Intent(SplashScreenActivity.this, HomeActivity.class);
                        i.putExtra("loaded_info", " ");
                        startActivity(i);
                    } else {
                        //user not signedin
                        Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        i.putExtra("loaded_info", " ");
                        startActivity(i);
                    }
                }
            }           finish();
        }
    }
}
