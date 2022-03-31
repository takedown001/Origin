package com.origin.esports.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.origin.esports.Helper;
import com.origin.esports.JSONParserString;
import com.origin.esports.LoginActivity;
import com.origin.esports.MatchDetailsActivity;
import com.origin.esports.Originconfig.URL;
import com.origin.esports.R;
import com.origin.esports.adapter.PlayAdapter;
import com.origin.esports.data.Play;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.scottyab.rootbeer.RootBeer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class PlayFragment extends Fragment {

    // Creating JSON Parser object
    private final JSONParserString jsonParser = new JSONParserString();

    private ArrayList<HashMap<String, String>> offersList;
    private ArrayList<HashMap<String, String>> offersListUser;

    // url to get all products list
    private static final String url = URL.PLAYFRAG;

    // JSON Node names
    private static final String TAG_SUCCESS = URL.SUCCESS;
    private static final String TAG_MATCH = "match";
    private static final String TAG_USER = "user";

    //user
    private static final String TAG_USERID = URL.USERID;
    private static final String TAG_FIRSTNAME = URL.FIRSTNAME;
    private static final String TAG_LASTNAME = URL.LASTNAME;
    private static final String TAG_USERNAME = URL.USERNAME;
    private static final String TAG_PUBGUSERNAME = URL.PUBGUSERNAME;
    private static final String TAG_TOKEN = "token";
    private static final String TAG_WEAPON = "weapon";

    //balance
    private static final String TAG_USERBALANCE = URL.BALANCE;
    private static final String TAG_WINMONEY =URL.WINMONEY;
    //playerjoinstatus
    private static final String TAG_PLAYERJOINSTATUS = "playerjoinstatus";
    private static final String TAG_ISPRIVATE = "isprivate";
    private static final String TAG_PRIVATETEXT = "privatetext";
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
    private static final String TAG_ISBAN ="isban";
    private String s2,s1;
    //matchdetail
    private static final String TAG_TOTALMATCHPLAYED = "totalmatchplayed";
    private static final String TAG_WONAMOUNT = "wonamount";
    private static final String TAG_KILLS = "kills";
    private String ban;
    // products JSONArray
    private JSONArray jsonarray = null;
    private JSONArray jsonarrayuser = null;

    //Prefrance


    private int success;
    private  String msg;
    //new
    private Context context;

    private List<Play> playList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PlayAdapter mAdapter;
    private String game;
    private ShimmerFrameLayout mShimmerViewContainer;
    private LinearLayout noMatchesLL;
    private  boolean root;
    private static final String TAG_GAME = "games";
    public PlayFragment() {
        // Required empty public constructor
    }

    public static PlayFragment newInstance(String param1, String param2) {
        PlayFragment fragment = new PlayFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
        SharedPreferences usrdtl = context.getSharedPreferences("usrdtl", MODE_PRIVATE);
        game = usrdtl.getString(TAG_GAME,"Coming Soon");
        // Hashmap for ListView

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootViewone = inflater.inflate(R.layout.fragment_play, container, false);

        mShimmerViewContainer = (ShimmerFrameLayout) rootViewone.findViewById(R.id.shimmer_view_container);
        noMatchesLL = (LinearLayout) rootViewone.findViewById(R.id.noMatchesLL);

        mShimmerViewContainer.setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView) rootViewone.findViewById(R.id.recyclerView);

        playList = new ArrayList();

        mAdapter = new PlayAdapter(getActivity(), playList);

        recyclerView.setHasFixedSize(true);
        offersList = new ArrayList<>();
        offersListUser = new ArrayList<>();
        new OneLoadAllProducts().execute();
        // vertical RecyclerView
        // keep match_result_list_row.xmlrow.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        // horizontal RecyclerView
        // keep match_result_list_rowist_row.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
//        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        // recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 16));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);

        // row click listenerMyDividerItemDecoration
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Play play = playList.get(position);

                Intent in = new Intent(getActivity(), MatchDetailsActivity.class);
                in.putExtra(TAG_MATCHID, play.getMatchID());
                in.putExtra("matchStatus", "upcoming");
                startActivity(in);
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));

        return rootViewone;
    }

    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmer();
    }

    public void onPause() {
        mShimmerViewContainer.stopShimmer();
        super.onPause();
    }

    /**
     * Prepares sample data to provide data set to adapter
     */

    class OneLoadAllProducts extends AsyncTask<String, String, String> {
        SharedPreferences shred = getActivity().getSharedPreferences("userdetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = shred.edit();

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            JSONObject params = new JSONObject();
            String rq = null;
            try {
                params.put(TAG_USERID, shred.getInt(TAG_USERID, 0));
                params.put(TAG_GAME, game);
                rq = jsonParser.makeHttpRequest(url, params);
                JSONObject ack = new JSONObject(rq);
                String decData = Helper.profileDecrypt(ack.get("Data").toString(), ack.get("Hash").toString());
                if (!Helper.verify(decData, ack.get("Sign").toString(), JSONParserString.publickey)) {
                    return rq;
                } else {

                    JSONObject json = new JSONObject(decData);
                    // Checking for SUCCESS TAG
                    success = json.getInt(TAG_SUCCESS);
                    msg = json.getString("message");
                    //        Log.d("success", String.valueOf(success));
                    if (success == 1) {
                        // jsonarray found
                        // Getting Array of jsonarray
                        jsonarray = json.getJSONArray(TAG_MATCH);
                        jsonarrayuser = json.getJSONArray(TAG_USER);

                        // looping through All jsonarray
                        for (int i = 0; i < jsonarrayuser.length(); i++) {
                            JSONObject c = jsonarrayuser.getJSONObject(i);

                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<>();
//                            map.put(TAG_FIRSTNAME, c.getString(TAG_FIRSTNAME));
                            // adding each child node to HashMap key => value
                            map.put(TAG_PUBGUSERNAME, c.getString(TAG_PUBGUSERNAME));
                            map.put(TAG_ISBAN, c.getString(TAG_ISBAN));
                            //balance
                            map.put(TAG_USERBALANCE, c.getString(TAG_USERBALANCE));
                            map.put(TAG_WINMONEY, c.getString(TAG_WINMONEY));

                            //matchdetail
                            map.put(TAG_TOTALMATCHPLAYED, c.getString(TAG_TOTALMATCHPLAYED));
                            map.put(TAG_WONAMOUNT, c.getString(TAG_WONAMOUNT));
                            map.put(TAG_KILLS, c.getString(TAG_KILLS));


                            // adding HashList to ArrayList
                            offersListUser.add(map);
                        }


                        // looping through All jsonarray
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject c = jsonarray.getJSONObject(i);

                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<>();

                            // adding each child node to HashMap key => value
                            //match
                            map.put(TAG_ISPRIVATE, c.getString(TAG_ISPRIVATE));
                            map.put(TAG_PRIVATETEXT, c.getString(TAG_PRIVATETEXT));
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
                            map.put(TAG_WEAPON, c.getString(TAG_WEAPON));

                            //player join status
                            map.put(TAG_PLAYERJOINSTATUS, c.getString(TAG_PLAYERJOINSTATUS));

                            // adding HashList to ArrayList
                            offersList.add(map);
                        }
                    }
                }
            }catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return rq;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products

            if(getActivity() == null)
                return;
            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /*
                      Updating parsed JSON data into ListView
                     */

                    if (success == 1) {
                        // jsonarray found
                        // Getting Array of jsonarray

                        /*
                          Updating parsed JSON data into ListView
                         */
                        for (int i = 0; i < offersListUser.size(); i++) {

                            // preference and set username for session
                            editor.putString(TAG_FIRSTNAME, offersListUser.get(i).get(TAG_FIRSTNAME));
                            editor.putString(TAG_LASTNAME, offersListUser.get(i).get(TAG_LASTNAME));
                            editor.putString(TAG_PUBGUSERNAME, offersListUser.get(i).get(TAG_PUBGUSERNAME));



                            editor.putString(TAG_ISBAN,offersListUser.get(i).get(TAG_ISBAN));
                            //balance
                            editor.putInt(TAG_USERBALANCE, Integer.parseInt(offersListUser.get(i).get(TAG_USERBALANCE)));
                            editor.putInt(TAG_WINMONEY, Integer.parseInt(offersListUser.get(i).get(TAG_WINMONEY)));
                            editor.putString("isroot", String.valueOf(root));

                            //matchdetail
                            editor.putInt(TAG_TOTALMATCHPLAYED, Integer.parseInt(offersListUser.get(i).get(TAG_TOTALMATCHPLAYED)));
                            editor.putInt(TAG_WONAMOUNT, Integer.parseInt(offersListUser.get(i).get(TAG_WONAMOUNT)));
                            editor.putInt(TAG_KILLS, Integer.parseInt(offersListUser.get(i).get(TAG_KILLS)));
                            editor.apply();
                        }

                        ban = shred.getString(TAG_ISBAN,"NO");
                  //      Log.d("isban", ban);

//                        if (root) {
//                            getActivity().finish();
//                            Toast.makeText(getActivity()," Cheats Detected ,Please Reset Your Device", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(getActivity(), LoginActivity.class));
                      //  }
//                    else {
                            if (ban.equals("YES")) {
                                shred.edit().clear().apply();
                                getActivity().finish();
                                Toast.makeText(getActivity(), "You Have Been Banned", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                            } else {
                        /*
                          Updating parsed JSON data into ListView
                         */
                                for (int i = 0; i < offersList.size(); i++) {

                                    Play play = new Play();
                                    play.setTitle(offersList.get(i).get(TAG_MATCHID));
                                    play.setMatchID(offersList.get(i).get(TAG_MATCHID));
                                    play.setType(offersList.get(i).get(TAG_TYPE));
                                    play.setVersion(offersList.get(i).get(TAG_VERSION));
                                    play.setMap(offersList.get(i).get(TAG_MAP));
                                    play.setTopImage(offersList.get(i).get(TAG_MAINTOPBANNERIMG));
                                    play.setImgURL(offersList.get(i).get(TAG_ICONIMG));
                                    play.setMatchType(offersList.get(i).get(TAG_MATCHTYPE));
                                    play.setTotalplayer(Integer.parseInt(offersList.get(i).get(TAG_TOTALPLAYER)));
                                    play.setTotalPeopleJoined(Integer.parseInt(offersList.get(i).get(TAG_TOTALPLAYERJOINED)));
                                    s1 =offersList.get(i).get(TAG_PRIVATETEXT);
                                    s2 = offersList.get(i).get(TAG_WEAPON);
                                    play.setIsPrivateMatch(Boolean.parseBoolean(offersList.get(i).get(TAG_ISPRIVATE)));

                                    play.setprivateText(s1 + s2);
                                    int parseInt = Integer.parseInt(offersList.get(i).get(TAG_TOTALPLAYER)) - Integer.parseInt(offersList.get(i).get(TAG_TOTALPLAYERJOINED));
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("Only ");
                                    stringBuilder.append(String.valueOf(parseInt));
                                    stringBuilder.append(" spots left");
                                    play.setSpots(stringBuilder.toString());

                                    String totalplayerjoined = offersList.get(i).get(TAG_TOTALPLAYERJOINED);
                                    StringBuilder stringBuildersize = new StringBuilder();
                                    stringBuildersize.append(totalplayerjoined);
                                    stringBuildersize.append("/" + Integer.parseInt(offersList.get(i).get(TAG_TOTALPLAYER)));
                                    play.setSize(stringBuildersize.toString());

                                    play.setEntryFee(offersList.get(i).get(TAG_ENTRYFEE));
                                    play.setWinPrize(offersList.get(i).get(TAG_WINPRICE));
                                    play.setPerKill(offersList.get(i).get(TAG_PERKILL));
                                    play.setJoin_status(offersList.get(i).get(TAG_PLAYERJOINSTATUS));
                                    play.setTimeDate(offersList.get(i).get(TAG_MATCHSCHEDULE));

                                    playList.add(play);

                                    // notify adapter about data set changes
                                    // so that it will render the list with new data
                                    mAdapter.notifyDataSetChanged();

                                }

                                if (playList.size() <= 0) {
                                    mShimmerViewContainer.stopShimmer();
                                    mShimmerViewContainer.setVisibility(View.GONE);
                                    noMatchesLL.setVisibility(View.VISIBLE);
                                    return;
                                }

                                mShimmerViewContainer.stopShimmer();
                                mShimmerViewContainer.setVisibility(View.GONE);
                                noMatchesLL.setVisibility(View.GONE);
                            }
//                        }

                    } else {
                        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();

                    }

                }
            });

        }

    }

}
