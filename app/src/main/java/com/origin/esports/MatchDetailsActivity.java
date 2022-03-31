package com.origin.esports;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.origin.esports.Originconfig.URL;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.facebook.shimmer.ShimmerFrameLayout;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MatchDetailsActivity extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    private final JSONParserString jsonParser = new JSONParserString();

    private ArrayList<HashMap<String, String>> offersListMatch;
    private ArrayList<HashMap<String, String>> offersListParticipants;

    // url to get all products list
    private static final String url = URL.Matchparticipants;

    // JSON Node names
    private static final String TAG_SUCCESS = URL.SUCCESS;
    private static final String TAG_MATCH = "match";

    //user
    private static final String TAG_USERID = URL.USERID;
    private static final String TAG_FIRSTNAME = URL.FIRSTNAME;
    private static final String TAG_LASTNAME = URL.LASTNAME;
    private static final String TAG_USERNAME = URL.USERNAME;
    private static final String TAG_PUBGUSERNAME = URL.PUBGUSERNAME;
    //playerjoinstatus
    private static final String TAG_PLAYERJOINSTATUS = "playerjoinstatus";

    //match
    private static final String TAG_MATCHID = "matchid";
    private static final String TAG_ROOMID = "roomid";
    private static final String TAG_ROOMPASSWORD = "roompassword";
    private static final String TAG_TYPE = "type";
    private static final String TAG_VERSION = "version";
    private static final String TAG_MAP = "map";
    private static final String TAG_MAINTOPBANNERIMG = "maintopbannerimg";
    private static final String TAG_ICONIMG = "iconimg";
    private static final String TAG_MATCHTYPE = "matchtype";
    private static final String TAG_TOTALPLAYER = "totalplayer";
    private static final String TAG_TOTALPLAYERJOINED = "totalplayerjoined";
    private static final String TAG_ENTRYFEE = "entryfee";
    private static final String TAG_WINPRICE = "winprice";
    private static final String TAG_PERKILL = "perkill";
    private static final String TAG_JOINSTATUS = "joinstatus";
    private static final String TAG_MATCHSTATUS = "matchstatus";
    private static final String TAG_MATCHSCHEDULE = "matchschedule";
    private static final String TAG_LOG_ENTDATE = "log_entdate";
    private static final String TAG_GETCHANNEL = "ytchannel";
    //rules
    private static final String TAG_RULES = "rules";


    // products JSONArray
    private JSONArray jsonarray = null;

    // products JSONArrayRules
    private JSONArray jsonarrayrules = null;

    //Prefrance


    private int success;

    private String playerjoinstatus = "";
    private String JoinStatus = "";
    private TextView fee;
    private Button joinButton;
    private NonScrollListView listRulesDetails;
    private LinearLayout loadBtnLL;
    private Button loadParticipants;
    private NonScrollListView lvParticipants;
    private ShimmerFrameLayout mShimmerViewContainer;
    private TextView map;
    private String matchEntryFee;
    private String matchID;
    private String matchMap;
    private String matchPerKill;
    private String matchStartTime;
    private String matchStatus;
    private String matchTitle;
    private String matchTopImage;
    private String matchType;
    private String matchVersion;
    private String matchWinPrize;
    private TextView matchtype;
    private NestedScrollView nestedScrollView;
    private TextView noParticipants;
    private TextView perKillPrize;
    private Button playButton;
    private String privateStatus;
    private TextView refreshLV;
    private String roomID;
    private RelativeLayout roomIDPassRL;
    private TextView roomIDPassUpcoming;
    private RelativeLayout roomIDPasswordsRL;
    private String roomPass;
    private TextView room_ID;
    private TextView room_Password;
    private ArrayList<String> rulesList;
    private Button spectateButton;
    private LinearLayout spectatePlayButton;
    private TextView startTime;
    private TextView title;
    private ImageView topImage;
    private String totalPlayer;
    private String totalParticipants;
    private TextView type;
    private String typeSolo;
    private ArrayList<String> unames;
    private String username;
    private TextView version;
    private TextView winPrize;
    private String getchannelurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);
        SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = shred.edit();
        username = shred.getString(TAG_USERNAME, "");

        matchID = getIntent().getStringExtra(TAG_MATCHID);
        matchStatus = getIntent().getStringExtra("matchStatus");
        getchannelurl = getIntent().getStringExtra("ytchannel");
        // Hashmap for ListView
        offersListMatch = new ArrayList<>();
        offersListParticipants = new ArrayList<>();

        title = (TextView) findViewById(R.id.title);
        type = (TextView) findViewById(R.id.type);
        version = (TextView) findViewById(R.id.version);
        map = (TextView) findViewById(R.id.map);
        matchtype = (TextView) findViewById(R.id.matchType);
        fee = (TextView) findViewById(R.id.fee);
        winPrize = (TextView) findViewById(R.id.winnerPrize);
        perKillPrize = (TextView) findViewById(R.id.perKillPrize);
        loadParticipants = (Button) findViewById(R.id.loadBtn);
        loadBtnLL = (LinearLayout) findViewById(R.id.LLloadBtn);
        refreshLV = (TextView) findViewById(R.id.refreshLVBtn);
        startTime = (TextView) findViewById(R.id.startdate);
        noParticipants = (TextView) findViewById(R.id.noParticipantsText);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScroll);
        joinButton = (Button) findViewById(R.id.JoinButton);
        spectateButton = (Button) findViewById(R.id.spectateButton);
        playButton = (Button) findViewById(R.id.playButton);
        roomIDPassUpcoming = (TextView) findViewById(R.id.RoomIDPassUpcoming);
        spectatePlayButton = (LinearLayout) findViewById(R.id.SpectatePlayLL);
        roomIDPassRL = (RelativeLayout) findViewById(R.id.matchIDPassLL);
        roomIDPasswordsRL = (RelativeLayout) findViewById(R.id.roomIDPassRL);
        room_ID = (TextView) findViewById(R.id.roomIDValue);
        room_Password = (TextView) findViewById(R.id.roomPassValue);
        lvParticipants = (NonScrollListView) findViewById(R.id.listParticipants);
        listRulesDetails = (NonScrollListView) findViewById(R.id.listRules);
        mShimmerViewContainer = (ShimmerFrameLayout) findViewById(R.id.aboutShimmer);
        topImage = (ImageView) findViewById(R.id.matchImage);

        unames = new ArrayList();
        rulesList = new ArrayList();
        lvParticipants.setDivider(null);
        listRulesDetails.setDivider(null);

        // Loading match details in Background Thread
        new GetAllMatchDetails().execute();

        loadParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Loading jsonarray in Background Thread
                new GetMatchParticipants().execute();

                loadBtnLL.setVisibility(View.GONE);
                lvParticipants.setVisibility(View.VISIBLE);
                refreshLV.setVisibility(View.VISIBLE);
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchDetailsActivity.this, JoinMatchConfirmationActivity.class);
                intent.putExtra("matchType", matchType);
                intent.putExtra("matchID", matchID);
                intent.putExtra("entryFee", matchEntryFee);
                intent.putExtra("JoinStatus", JoinStatus);
                startActivity(intent);
            }
        });
        spectateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage("com.google.android.youtube");
                try {
                    if (intent != null) {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(getchannelurl));
                        startActivity(intent);
                    } else {
                        // Bring user to the market or let them choose an app?
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse("market://details?id=" + "com.google.android.youtube"));
                        if (intent != null) {
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage("com.pubg.imobile");
                try {
                    if (intent != null) {
                        startActivity(intent);
                    } else {
                        // Bring user to the market or let them choose an app?
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse("market://details?id=" + "com.pubg.imobile"));
                        if (intent != null) {
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class GetAllMatchDetails extends AsyncTask<String, String, String> {
        SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = shred.edit();

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MatchDetailsActivity.this);
            pDialog.setMessage("Loading...");
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
                params.put(TAG_MATCHID, matchID);

                rq = jsonParser.makeHttpRequest(URL.MatchDetails, params);


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
                Toast.makeText(MatchDetailsActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                return;
            }

            runOnUiThread(new Runnable() {
                public void run() {

                    try {
                        JSONObject ack = new JSONObject(s);

                        String decData = Helper.profileDecrypt(ack.get("Data").toString(), ack.get("Hash").toString());
                        if (!Helper.verify(decData, ack.get("Sign").toString(), JSONParserString.publickey)) {
                            Toast.makeText(MatchDetailsActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            JSONObject json = new JSONObject(decData);
                            Log.d("test", String.valueOf(json));
                            success = json.getInt(TAG_SUCCESS);


                            jsonarray = json.getJSONArray(TAG_MATCH);

                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject c = jsonarray.getJSONObject(i);

//                    // Storing each json item in variable

                                // creating new HashMap
                                HashMap<String, String> map = new HashMap<>();

                                // adding each child node to HashMap key => value

                                //player join status
                                map.put(TAG_PLAYERJOINSTATUS, json.getString(TAG_PLAYERJOINSTATUS));

                                //match
                                map.put(TAG_MATCHID, c.getString(TAG_MATCHID));
                                map.put(TAG_ROOMID, c.getString(TAG_ROOMID));
                                map.put(TAG_ROOMPASSWORD, c.getString(TAG_ROOMPASSWORD));
                                map.put(TAG_TYPE, c.getString(TAG_TYPE));
                                map.put(TAG_VERSION, c.getString(TAG_VERSION));
                                map.put(TAG_MAP, c.getString(TAG_MAP));
                                map.put(TAG_MAINTOPBANNERIMG, c.getString(TAG_MAINTOPBANNERIMG));
                                map.put(TAG_ICONIMG, c.getString(TAG_ICONIMG));
                                map.put(TAG_MATCHTYPE, c.getString(TAG_MATCHTYPE));
                                map.put(TAG_TOTALPLAYER, c.getString(TAG_TOTALPLAYER));
                                map.put(TAG_TOTALPLAYERJOINED, c.getString(TAG_TOTALPLAYERJOINED));
                                map.put(TAG_ENTRYFEE, c.getString(TAG_ENTRYFEE));
                                map.put(TAG_WINPRICE, c.getString(TAG_WINPRICE));
                                map.put(TAG_PERKILL, c.getString(TAG_PERKILL));
                                map.put(TAG_JOINSTATUS, c.getString(TAG_JOINSTATUS));
                                map.put(TAG_MATCHSTATUS, c.getString(TAG_MATCHSTATUS));
                                map.put(TAG_MATCHSCHEDULE, c.getString(TAG_MATCHSCHEDULE));
                                map.put(TAG_LOG_ENTDATE, c.getString(TAG_LOG_ENTDATE));
                                //              map.put(TAG_GETCHANNEL,c.getString(TAG_GETCHANNEL));
                                // adding HashList to ArrayList
                                offersListMatch.add(map);
                            }

                            // jsonarray found
                            // Getting Array of jsonarray
                            //    Log.d("rules",json.toString());
                            jsonarrayrules = json.getJSONArray(TAG_RULES);

                            // looping through All jsonarray
                            for (int i = 0; i < jsonarrayrules.length(); i++) {
                                JSONObject c = jsonarrayrules.getJSONObject(i);

                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("•  ");
                                stringBuilder2.append(c.getString(TAG_RULES));
                                rulesList.add(String.valueOf(stringBuilder2));
                            }
                        }

                        if (success == 1) {
                            // jsonarray found
                            // Getting Array of jsonarray

                        /*
                          Updating parsed JSON data into ListView
                         */
                            for (int i = 0; i < offersListMatch.size(); i++) {

                                // preference and set username for session
                                playerjoinstatus = offersListMatch.get(i).get(TAG_PLAYERJOINSTATUS);
                                JoinStatus = offersListMatch.get(i).get(TAG_JOINSTATUS);
                                roomID = offersListMatch.get(i).get(TAG_ROOMID);
                                roomPass = offersListMatch.get(i).get(TAG_ROOMPASSWORD);
                                typeSolo = offersListMatch.get(i).get(TAG_TYPE);
                                matchType = offersListMatch.get(i).get(TAG_MATCHTYPE);
                                matchTitle = offersListMatch.get(i).get(TAG_MATCHID);

//                            String toUpperCase = jSONObject.getString("time").toUpperCase();
                                //Input date in String format
                                String input = offersListMatch.get(i).get(TAG_MATCHSCHEDULE);
                                //Date/time pattern of input date
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                //Date/time pattern of desired output date
                                DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy");
                                DateFormat outputtimeformat = new SimpleDateFormat("hh:mm aa");
                                Date date;
                                String output = null;
                                try {
                                    //Conversion of input String to date
                                    date = df.parse(input);
                                    //old date format to new date format
                                    output = outputformat.format(date) + " at " + outputtimeformat.format(date);
                                } catch (ParseException pe) {
                                    pe.printStackTrace();
                                }

                                matchStartTime = output;

                                matchVersion = offersListMatch.get(i).get(TAG_VERSION);
                                matchMap = offersListMatch.get(i).get(TAG_MAP);
                                matchWinPrize = offersListMatch.get(i).get(TAG_WINPRICE);
                                matchPerKill = offersListMatch.get(i).get(TAG_PERKILL);
                                matchEntryFee = offersListMatch.get(i).get(TAG_ENTRYFEE);
                                matchTopImage = offersListMatch.get(i).get(TAG_MAINTOPBANNERIMG);
                                totalPlayer = offersListMatch.get(i).get(TAG_TOTALPLAYER);
                                totalParticipants = offersListMatch.get(i).get(TAG_TOTALPLAYERJOINED);
                                privateStatus = offersListMatch.get(i).get(TAG_MATCHSTATUS);

                                title.setText("Match#" + matchTitle);
                                startTime.setText(matchStartTime);
                                type.setText(typeSolo);
                                version.setText(matchVersion);
                                map.setText(matchMap);
                                matchtype.setText(matchType);

                                StringBuilder stringBuilderwinPrize = new StringBuilder();
                                stringBuilderwinPrize.append("₹");
                                stringBuilderwinPrize.append(matchWinPrize);
                                winPrize.setText(stringBuilderwinPrize.toString());

                                StringBuilder stringBuilderperKillPrize = new StringBuilder();
                                stringBuilderperKillPrize.append("₹");
                                stringBuilderperKillPrize.append(matchPerKill);
                                perKillPrize.setText(stringBuilderperKillPrize.toString());

                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("₹ ");
                                stringBuilder2.append(matchEntryFee);
                                fee.setText(stringBuilder2.toString());

                                if (matchTopImage.contains("png") || matchTopImage.contains("jpg")) {
                                    String img = URL.mainimg + matchTopImage;
                                    Glide.with(MatchDetailsActivity.this).load(img).placeholder(R.drawable.wp).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(topImage);
                                }

                                //rajan
                                int voidR = totalParticipants.isEmpty() ? 0 : Integer.parseInt(totalParticipants);


                                if (matchStatus.equalsIgnoreCase("upcoming")) {

                                    if (playerjoinstatus.equalsIgnoreCase("0")) {
                                        //player not joined
                                        joinButton.setVisibility(View.VISIBLE);
                                        playButton.setVisibility(View.GONE);

                                        //id pass layout make gone
                                        roomIDPassRL.setVisibility(View.GONE);

                                        System.out.println("Rajan_voidR" + voidR);
                                        System.out.println("Rajan_totalPlayer" + totalPlayer);
                                        if (voidR < Integer.parseInt(totalPlayer)) {

                                        } else {
                                            joinButton.setText("Match Full");
                                            joinButton.setBackgroundColor(Color.parseColor("#757575"));
                                            joinButton.setClickable(false);
                                        }

                                    } else if (playerjoinstatus.equalsIgnoreCase("1")) {
                                        //player joined
                                        joinButton.setVisibility(View.GONE);
                                        spectatePlayButton.setVisibility(View.VISIBLE);
                                        spectateButton.setVisibility(View.GONE);
                                        playButton.setVisibility(View.VISIBLE);

                                        //id pass layout make visible
                                        roomIDPassRL.setVisibility(View.VISIBLE);
                                        if (roomID.equalsIgnoreCase("null") || roomID == null) {
                                            roomIDPassUpcoming.setVisibility(View.VISIBLE);
                                            roomIDPasswordsRL.setVisibility(View.GONE);

                                        } else {

                                            roomIDPassUpcoming.setVisibility(View.GONE);
                                            roomIDPasswordsRL.setVisibility(View.VISIBLE);
                                            room_ID.setText(roomID);
                                            room_Password.setText(roomPass);

//                                      RoomDetailsAlertDialog(MatchDetailsActivity.this);
                                        }

                                    }

                                } else if (matchStatus.equalsIgnoreCase("ongoing")) {

                                    if (playerjoinstatus.equalsIgnoreCase("0")) {
                                        //player not joined
                                        joinButton.setVisibility(View.GONE);
                                        spectatePlayButton.setVisibility(View.VISIBLE);
                                        spectateButton.setVisibility(View.VISIBLE);
                                        playButton.setVisibility(View.GONE);

                                        //id pass layout make gone
                                        roomIDPassRL.setVisibility(View.GONE);

                                    } else if (playerjoinstatus.equalsIgnoreCase("1")) {
                                        //player joined
                                        joinButton.setVisibility(View.GONE);
                                        spectatePlayButton.setVisibility(View.VISIBLE);
                                        spectateButton.setVisibility(View.GONE);
                                        playButton.setVisibility(View.VISIBLE);

                                        //id pass layout make visible
                                        roomIDPassRL.setVisibility(View.VISIBLE);

                                        if (roomID.equalsIgnoreCase("null") || roomID == null) {
                                            roomIDPassUpcoming.setVisibility(View.VISIBLE);
                                            roomIDPasswordsRL.setVisibility(View.GONE);
                                        } else {
                                            roomIDPassUpcoming.setVisibility(View.GONE);
                                            roomIDPasswordsRL.setVisibility(View.VISIBLE);
                                            room_ID.setText(roomID);
                                            room_Password.setText(roomPass);

                                        }

                                    }

                                    if (playerjoinstatus.equalsIgnoreCase("0") && voidR < Integer.parseInt(totalPlayer)) {
                                        //player not joined

                                    } else if (playerjoinstatus.equalsIgnoreCase("1")) {
                                        //player joined

                                    }

                                } else if (matchStatus.equalsIgnoreCase("done")) {

                                    if (playerjoinstatus.equalsIgnoreCase("0") && voidR < Integer.parseInt(totalPlayer)) {
                                        //player not joined

                                    } else if (playerjoinstatus.equalsIgnoreCase("1")) {
                                        //player joined

                                    }

                                }

                                // array as a third parameter.
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MatchDetailsActivity.this, R.layout.participantslistview, rulesList);
                                listRulesDetails.setAdapter(arrayAdapter);
                                mShimmerViewContainer.stopShimmer();
                                mShimmerViewContainer.setVisibility(View.GONE);

                            }

//                        Toast.makeText(MatchDetailsActivity.this,"Something went wrong. Try again!",Toast.LENGTH_LONG).show();

                        } else {
                            System.out.println("Rjn_user_not_created");
                            Toast.makeText(MatchDetailsActivity.this, "Something went wrong. Try again!", Toast.LENGTH_LONG).show();

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

    class GetMatchParticipants extends AsyncTask<String, String, String> {
        SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MatchDetailsActivity.this);
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
                params.put(TAG_USERID, String.valueOf(shred.getInt(TAG_USERID, 0)));
                params.put(TAG_MATCHID, matchID);
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
                Toast.makeText(MatchDetailsActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                return;
            }
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        JSONObject ack = new JSONObject(s);

                        String decData = Helper.profileDecrypt(ack.get("Data").toString(), ack.get("Hash").toString());
                        if (!Helper.verify(decData, ack.get("Sign").toString(), JSONParserString.publickey)) {
                            Toast.makeText(MatchDetailsActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                            return;
                        } else {

                            JSONObject json = new JSONObject(decData);
                            // Checking for SUCCESS TAG
                            success = json.getInt(TAG_SUCCESS);
                            Log.d("test", String.valueOf(json));
                            // jsonarray found
                            // Getting Array of jsonarray
                            jsonarray = json.getJSONArray("participants");

                            // looping through All jsonarray
                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject c = jsonarray.getJSONObject(i);

                                // creating new HashMap
                                HashMap<String, String> map = new HashMap<>();

                                // adding each child node to HashMap key => value
                                map.put(TAG_USERID, c.getString(TAG_USERID));
//                                map.put(TAG_FIRSTNAME, c.getString(TAG_FIRSTNAME));
//                                map.put(TAG_LASTNAME, c.getString(TAG_LASTNAME));
                                map.put(TAG_PUBGUSERNAME, c.getString(TAG_PUBGUSERNAME));
                                map.put(TAG_MATCHID, c.getString(TAG_MATCHID));


                                // adding HashList to ArrayList
                                offersListParticipants.add(map);
                            }


                            if (success == 1) {
                                // jsonarray found
                                // Getting Array of jsonarray

                                if (offersListParticipants.size() == 0) {
                                    noParticipants.setVisibility(View.VISIBLE);
                                    lvParticipants.setVisibility(View.GONE);
                                }

                        /*
                          Updating parsed JSON data into ListView
                         */
                                for (int i = 0; i < offersListParticipants.size(); i++) {

                                    unames.add(offersListParticipants.get(i).get(TAG_PUBGUSERNAME));
                                }

                                ArrayAdapter<String> arrayAdapterparticipants = new ArrayAdapter<>(MatchDetailsActivity.this, R.layout.participantslistview, unames);
                                lvParticipants.setAdapter(arrayAdapterparticipants);

                            } else {
                                Toast.makeText(MatchDetailsActivity.this, "Something went wrong. Try again!", Toast.LENGTH_LONG).show();
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
