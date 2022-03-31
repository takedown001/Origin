package com.origin.esports;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.origin.esports.Originconfig.URL;
import com.origin.esports.adapter.TopPlayerAdapter;
import com.origin.esports.data.TopPlayer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TopPlayersActivity extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object


    private ArrayList<HashMap<String, String>> offersList;

    // url to get all products list
    private static final String url = URL.TopPlayer;

    // JSON Node names
    private static final String TAG_ERROR = URL.ERROR;

    JSONObject obj = new JSONObject();
    //user
    private static final String TAG_USERID = URL.USERID;
    private static final String TAG_FIRSTNAME = URL.FIRSTNAME;
    private static final String TAG_LASTNAME = URL.LASTNAME;
    private static final String TAG_USERNAME = URL.USERNAME;

    //playerjoinstatus
    private static final String TAG_PLAYERRRANK = "playerrrank";


    //matchdetail
    private static final String TAG_WONAMOUNT = URL.WONAMOUNT;

    // products JSONArray
    private JSONArray jsonarray = null;
    JSONParserString requestHandler = new JSONParserString();
    //Prefrance


    private boolean error;
    private TextView noplayer ;
    private List<TopPlayer> playList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TopPlayerAdapter mAdapter;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_players);
        SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);

        username = shred.getString(TAG_USERNAME, "NOT FOUND");
        noplayer= findViewById(R.id.noPlayers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle((CharSequence) "Top Players");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Hashmap for ListView
        offersList = new ArrayList<>();

        playList = new ArrayList();

        recyclerView = (RecyclerView) findViewById(R.id.topPlayersListRecyclerView);

        mAdapter = new TopPlayerAdapter(playList);

        recyclerView.setHasFixedSize(true);
        new OneLoadAllProducts().execute();

        // vertical RecyclerView
        // keep top_player_list_row.xmlw.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        // horizontal RecyclerView
        // keep top_player_list_rowt_row.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        // recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 16));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Prepares sample data to provide data set to adapter
     */

    class OneLoadAllProducts extends AsyncTask<Void, Void, String> {
        SharedPreferences shred = getSharedPreferences("userdetails", MODE_PRIVATE);
        SharedPreferences playerrank = getSharedPreferences("playerrank", MODE_PRIVATE);
        SharedPreferences.Editor editor = playerrank.edit();

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TopPlayersActivity.this);
            pDialog.setMessage("Loading Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         */
        @Override
        protected String doInBackground(Void... voids) {
            // Building Parameters
            JSONObject params = new JSONObject();
            String rq = null;
            try {
                params.put(TAG_USERID, shred.getInt(TAG_USERID, -1));
                rq = requestHandler.makeHttpRequest(url, params);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return rq;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            if (s == null || s.isEmpty()) {
                Toast.makeText(TopPlayersActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                return;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        JSONObject ack = new JSONObject(s);

                        String decData = Helper.profileDecrypt(ack.get("Data").toString(), ack.get("Hash").toString());
                        if (!Helper.verify(decData, ack.get("Sign").toString(), JSONParserString.publickey)) {
                            Toast.makeText(TopPlayersActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                            return;
                        } else {

                            obj = new JSONObject(decData);
                 //           Log.d("test", String.valueOf(obj));
                            if (!obj.getBoolean(TAG_ERROR)) {

                                // adding each child node to HashMap key => value
                                jsonarray = obj.getJSONArray("Topplayer");

                                // looping through All jsonarray
                                for (int i = 0; i < jsonarray.length(); i++) {
                                    JSONObject c = jsonarray.getJSONObject(i);

//                    // Storing each json item in variable

                                    // creating new HashMap
                                    HashMap<String, String> map = new HashMap<>();

                                    // adding each child node to HashMap key => value
                                    //match
                           //         map.put(TAG_USERID, c.getString(TAG_USERID));
                                 //   map.put(TAG_FIRSTNAME, c.getString(TAG_FIRSTNAME));
                                //    map.put(TAG_LASTNAME, c.getString(TAG_LASTNAME));
                                    map.put(TAG_USERNAME, c.getString(TAG_USERNAME));
                                    map.put(TAG_WONAMOUNT, c.getString(TAG_WONAMOUNT));
                                    map.put(TAG_PLAYERRRANK, c.getString(TAG_PLAYERRRANK));

                                    // adding HashList to ArrayList
                                    offersList.add(map);
                                }

                                    if (offersList.size() == 0) {
                                        noplayer.setVisibility(View.VISIBLE);
                                    }

                                    for (int i = 0; i < offersList.size(); i++) {

                                        TopPlayer topplayer = new TopPlayer();
                                        topplayer.setPlayerName(offersList.get(i).get(TAG_USERNAME));
                                        topplayer.setPlayerWinning(offersList.get(i).get(TAG_WONAMOUNT));
                                       topplayer.setPosition(offersList.get(i).get(TAG_PLAYERRRANK));

                                        playList.add(topplayer);

                                        // notify adapter about data set changes
                                        // so that it will render the list with new data
                                        mAdapter.notifyDataSetChanged();

                                    }


                            } else {
                                // no jsonarray found
                                Toast.makeText(TopPlayersActivity.this, "Something went wrong. Try again!", Toast.LENGTH_LONG).show();
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
