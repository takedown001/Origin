package com.origin.esports;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.origin.esports.Originconfig.URL;
import com.origin.esports.fragment.EarnFragment;
import com.origin.esports.fragment.GameFragment;
import com.origin.esports.fragment.ProfileFragment;
import com.origin.esports.helper.BottomNavigationBehavior;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {

    private static final String url = URL.AppUpDater ;

    // JSON Node names
    private static final String TAG_ERROR = URL.ERROR;
    private String newversion;
    private String data = "data";
    private String whatsNewData;
    private String updatedOn;
    private ImageView notify;
    //user
    private static final String TAG_USERID = URL.USERID;

    //app

    private static final String TAG_APP_NEWVERSION = "newversion";

    private static final String TAG_TOKEN = "token";
    private static int backbackexit = 1;
    JSONParserString jsonparser = new JSONParserString();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.gameitem);
        Fragment frag= new GameFragment();
        loadFragment(frag);
        SharedPreferences.Editor editor = shred.edit();
        String UUID = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
        editor.putString("token",UUID);
        editor.apply();
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // attaching bottom sheet behaviour - hide / show on scroll
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());
        //off shift mode
//        navigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        ImageButton walletbal = findViewById(R.id.walletshotcut);
        TextView bal = findViewById(R.id.playcoin);
        String balance = String.valueOf(shred.getInt("balance",0));
        bal.setText(balance);
        ImageButton notifymw = findViewById(R.id.notify);

        new OneLoadAllProducts().execute();
        notifymw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
            }
        });
    }


    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_earn:
                    fragment = new EarnFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.gameitem:
                    fragment = new GameFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_profile:
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    /**
     * loading fragment into FrameLayout
     *
     * @param fragment
     */
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void onBackPressed() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        int seletedItemId = bottomNavigationView.getSelectedItemId();
        if (R.id.gameitem != seletedItemId) {
            setHomeItem(HomeActivity.this);
        } else {
            if (backbackexit >= 2) {

                // Creating alert Dialog with three Buttons

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        HomeActivity.this);

                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.app_name));

                // Setting Dialog Message
                alertDialog.setMessage("Are you sure you want to exit??");

                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.icon);

                // Setting Positive Yes Button
                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        });
                // Setting Positive Yes Button
                alertDialog.setNeutralButton("NO",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                            }
                        });
                // Showing Alert Message
                alertDialog.show();
//					super.onBackPressed();
            } else {
                backbackexit++;
                Toast.makeText(getBaseContext(), "Press again to Exit", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static void setHomeItem(Activity activity) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                activity.findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.gameitem);
    }

    class OneLoadAllProducts extends AsyncTask<Void, Void, String> {

        SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected String doInBackground(Void... voids) {
            //creating request handler object
             JSONObject params = new JSONObject();
             String rq = null;
            try {
                params.put(TAG_TOKEN,(shred.getString(TAG_TOKEN,null)));
                params.put("sign",Helper.AppSignature(HomeActivity.this));
                params.put(TAG_USERID,shred.getInt(TAG_USERID,0));

                rq = jsonparser.makeHttpRequest(url,params);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return  rq;


        }



        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null || s.isEmpty()) {
                Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                return;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        try {
                            JSONObject ack = new JSONObject(s);
                            String decData = Helper.profileDecrypt(ack.get("Data").toString(), ack.get("Hash").toString());
                            if (!Helper.verify(decData, ack.get("Sign").toString(), JSONParserString.publickey)) {
                                Toast.makeText(HomeActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                //converting response to json object
                                JSONObject obj = new JSONObject(decData);
                               Log.d("test",obj.toString());
                                if (!obj.getBoolean(TAG_ERROR))
                                {
                                    shred.edit().putInt("reward",obj.getInt("reward")).apply();
                                    newversion = obj.getString(TAG_APP_NEWVERSION);
                                    whatsNewData = obj.getString(data);
                                   shred.edit().putBoolean("wallet",obj.getBoolean("wallet")).apply();
                                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                    String version = pInfo.versionName;
                                    //           System.out.println("takedown" + "old:" + version + " new:" + newversion);

                                    if (Float.parseFloat(version) < Float.parseFloat(newversion)) {
                                        Intent intent = new Intent(HomeActivity.this, AppUpdaterActivity.class);
                                        intent.putExtra(TAG_APP_NEWVERSION, newversion);
                                        intent.putExtra(data,whatsNewData);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }else{
                                    Toast.makeText(HomeActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }

                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }

            });


        }
    }
}