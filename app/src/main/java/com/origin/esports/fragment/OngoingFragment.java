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
import com.origin.esports.MatchDetailsActivity;
import com.origin.esports.Originconfig.URL;
import com.origin.esports.R;
import com.origin.esports.adapter.OngoingAdapter;
import com.origin.esports.data.Ongoing;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class OngoingFragment extends Fragment {

    // Creating JSON Parser object
    private final JSONParserString jsonParser = new JSONParserString();

    private ArrayList<HashMap<String, String>> offersList;

    // url to get all products list
    private static final String url = URL.ONFRAG;

    // JSON Node names
    private static final String TAG_SUCCESS = URL.SUCCESS;
    private static final String TAG_ONGOING = "matchongoing";
    private static final String TAG_GAME = "games";
    //user
    private static final String TAG_USERID = "userid";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_ISPRIVATE = "isprivate";
    private static final String TAG_PRIVATETEXT = "privatetext";
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

    // products JSONArray
    private JSONArray jsonarray = null;

    //Prefrance


    private int success;

    //new
    private Context context;

    private List<Ongoing> ongoingList = new ArrayList<>();
    private RecyclerView recyclerView;
    private OngoingAdapter mAdapter;

    private ShimmerFrameLayout mShimmerViewContainer;
    private LinearLayout noOngoing;
    private LinearLayout participatedLinearLayout;
    private RecyclerView participatedRecyclerView;
    private LinearLayout upcomingLinearLayout;
    private String username;

    public OngoingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();

        // Hashmap for ListView


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {
        SharedPreferences shred = getActivity().getSharedPreferences("userdetails", MODE_PRIVATE);
        // Inflate the layout for this fragment
        View rootViewone = inflater.inflate(R.layout.fragment_ongoing, container, false);

        username = shred.getString(TAG_USERNAME,"");

        mShimmerViewContainer = (ShimmerFrameLayout) rootViewone.findViewById(R.id.shimmer_view_container);

        mShimmerViewContainer.setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView) rootViewone.findViewById(R.id.recyclerView);

        ongoingList = new ArrayList();

        mAdapter = new OngoingAdapter(getActivity(), ongoingList);

        recyclerView.setHasFixedSize(true);
        offersList = new ArrayList<>();

        new OneLoadAllProducts().execute();
        // vertical RecyclerView
        // keep ongoing_list_row.xmlml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);

        // horizontal RecyclerView
        // keep ongoing_list_rowow.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
//        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        // recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 16));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);

        // row click listenerMyDividerItemDecoration
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Ongoing ongoing = ongoingList.get(position);

                Intent in = new Intent(getActivity(), MatchDetailsActivity.class);
                in.putExtra(TAG_MATCHID, ongoing.getMatchID());
                in.putExtra("matchStatus", "ongoing");
                in.putExtra(TAG_GETCHANNEL,ongoing.getSpectateURL());
                startActivity(in);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        participatedRecyclerView = (RecyclerView) rootViewone.findViewById(R.id.recyclerViewParticipated);
        upcomingLinearLayout = (LinearLayout) rootViewone.findViewById(R.id.upcomingLL);
        participatedLinearLayout = (LinearLayout) rootViewone.findViewById(R.id.participatedLL);
        noOngoing = (LinearLayout) rootViewone.findViewById(R.id.noOnGoingLL);

        participatedRecyclerView.setHasFixedSize(true);
        participatedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
        SharedPreferences usrdtl = context.getSharedPreferences("usrdtl", MODE_PRIVATE);
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            JSONObject params = new JSONObject();
            String rq = null;
            try {
                params.put(TAG_USERID, String.valueOf(shred.getInt(TAG_USERID, 0)));
                params.put(TAG_GAME, usrdtl.getString(TAG_GAME, ""));
                // getting JSON string from URL
                rq = jsonParser.makeHttpRequest(url, params);

                JSONObject ack = new JSONObject(rq);

                String decData = Helper.profileDecrypt(ack.get("Data").toString(), ack.get("Hash").toString());
                if (!Helper.verify(decData, ack.get("Sign").toString(), JSONParserString.publickey)) {
                    Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_LONG).show();
                    return null;
                } else {

                    JSONObject json = new JSONObject(decData);
               //     Log.d("test", String.valueOf(json));
                    success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        // jsonarray found
                        // Getting Array of jsonarray
                        jsonarray = json.getJSONArray(TAG_ONGOING);

                        // looping through All jsonarray
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject c = jsonarray.getJSONObject(i);

                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<>();

                            // adding each child node to HashMap key => value
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
                            map.put(TAG_GETCHANNEL, c.getString(TAG_GETCHANNEL));
                            //player join status
                            map.put(TAG_PLAYERJOINSTATUS, c.getString(TAG_PLAYERJOINSTATUS));

                            // adding HashList to ArrayList
                            offersList.add(map);
                        }
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String s) {

            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {


                    if (success == 1) {


                        for (int i = 0; i < offersList.size(); i++) {

                            Ongoing ongoing = new Ongoing();
                            ongoing.setTitle(offersList.get(i).get(TAG_MATCHID));
                            ongoing.setMatchID(offersList.get(i).get(TAG_MATCHID));
                            ongoing.setType(offersList.get(i).get(TAG_TYPE));
                            ongoing.setVersion(offersList.get(i).get(TAG_VERSION));
                            ongoing.setMap(offersList.get(i).get(TAG_MAP));
                            ongoing.setTopImage(offersList.get(i).get(TAG_MAINTOPBANNERIMG));
                            ongoing.setImgURL(offersList.get(i).get(TAG_ICONIMG));
                            ongoing.setMatchType(offersList.get(i).get(TAG_MATCHTYPE));
                            ongoing.setTotalPeopleJoined(Integer.parseInt(offersList.get(i).get(TAG_TOTALPLAYERJOINED)));
                            ongoing.setEntryFee(offersList.get(i).get(TAG_ENTRYFEE));
                            ongoing.setWinPrize(offersList.get(i).get(TAG_WINPRICE));
                            ongoing.setPerKill(offersList.get(i).get(TAG_PERKILL));
                            ongoing.setJoin_status(offersList.get(i).get(TAG_PLAYERJOINSTATUS));
                            ongoing.setTimeDate(offersList.get(i).get(TAG_MATCHSCHEDULE));
                            ongoing.setSpectateURL(offersList.get(i).get(TAG_GETCHANNEL));
                            ongoing.setIsPrivateMatch(Boolean.parseBoolean(offersList.get(i).get(TAG_ISPRIVATE)));
                            ongoing.setprivatetext(offersList.get(i).get(TAG_PRIVATETEXT));
                            ongoingList.add(ongoing);

                            // notify adapter about data set changes
                            // so that it will render the list with new data
                            mAdapter.notifyDataSetChanged();

                        }

                        if (ongoingList.size()<=0) {
                            mShimmerViewContainer.stopShimmer();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            noOngoing.setVisibility(View.VISIBLE);
                            return;
                        }
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        noOngoing.setVisibility(View.GONE);

                    } else {
                        Toast.makeText(context,"Something went wrong. Try again!",Toast.LENGTH_LONG).show();

                    }

                }
            });

        }

    }

}
