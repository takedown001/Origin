package com.origin.esports.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.origin.esports.Helper;
import com.origin.esports.JSONParserString;
import com.origin.esports.LoginActivity;
import com.origin.esports.MyGameActivity;
import com.origin.esports.Originconfig.URL;
import com.origin.esports.R;
import com.origin.esports.adapter.GameAdapter;
import com.origin.esports.data.Game;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.scottyab.rootbeer.RootBeer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class GameFragment extends Fragment {

    private final JSONParserString jsonParser = new JSONParserString();
    private ArrayList<HashMap<String, String>> offersListUser;
    private ArrayList<HashMap<String, String>> offersList;
    private static final String url = URL.GAMEFrag;
    private static final String TAG_SUCCESS = URL.SUCCESS;

    //user
    private static final String TAG_USERID = URL.USERID;
    private static final String TAG_FIRSTNAME = URL.FIRSTNAME;
    private static final String TAG_LASTNAME = URL.LASTNAME;
    private static final String TAG_USERNAME = URL.USERNAME;
    private static final String GAMEUSERNAME = URL.PUBGUSERNAME;
    private static final String TAG_TOKEN = "token";
    private static final String TAG_GAMETITLE = "gametitle";
    private static final String TAG_GAMEIMG = "gameimg";

    //matchdetail
    private static final String TAG_TOTALMATCHPLAYED = "totalmatchplayed";
    private static final String TAG_WONAMOUNT = "wonamount";
    private static final String TAG_KILLS = "kills";
    private static final String TAG_USER = "user";

    //balance
    private static final String TAG_USERBALANCE = "balance";
    private static final String TAG_WINMONEY ="winmoney";
    private LinearLayout noMatchesLL;
    private static final String TAG_ISBAN = "isban";
    private static final String TAG_GAME = "games";
    private boolean ban;
    private JSONArray jsonarray = null;
    private JSONArray jsonarrayuser = null;
    private int success;
    private String msg;
    private ShimmerFrameLayout mShimmerViewContainer;
    private boolean root;
    private Context context;
    private RecyclerView recyclerView;
    private GameAdapter mAdapter;
    public GameFragment() {
        // Required empty public co
    }
    private List<Game> GameList = new ArrayList<>();

    public static GameFragment newInstance(String param1, String param2) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        // Inflate the layout for this fragment
        View rootViewone = inflater.inflate(R.layout.fragment_play, container, false);
        mShimmerViewContainer = (ShimmerFrameLayout) rootViewone.findViewById(R.id.shimmer_view_container);

        mShimmerViewContainer.setVisibility(View.VISIBLE);
        recyclerView = (RecyclerView) rootViewone.findViewById(R.id.recyclerView);
        GameList = new ArrayList();
        mAdapter = new GameAdapter(getActivity(), GameList);
        noMatchesLL = (LinearLayout) rootViewone.findViewById(R.id.noMatchesLL);
        recyclerView.setHasFixedSize(true);
        offersListUser = new ArrayList<>();
        offersList = new ArrayList<>();
        RootBeer rootBeer = new RootBeer(context);
        root = rootBeer.isRooted();
        new OneLoadAllProducts().execute();

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
                Game game = GameList.get(position);
                if (game.getTitle().equals("Coming Soon")){
                    Toast.makeText(context,"Coming Soon",Toast.LENGTH_LONG).show();
                }else{
                    Intent in = new Intent(getActivity(), MyGameActivity.class);
                    in.putExtra(TAG_GAME,game.getTitle());
                    startActivity(in);
                }
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

    class OneLoadAllProducts extends AsyncTask<String, String, String> {
        SharedPreferences shred = getActivity().getSharedPreferences("userdetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = shred.edit();
        @Override
        protected String doInBackground(String... strings) {
            JSONObject params = new JSONObject();
            String rq = null;
            try {
                params.put(TAG_USERID,shred.getInt(TAG_USERID, 0));
                params.put("rootcheck",  root);
                params.put(TAG_TOKEN, shred.getString(TAG_TOKEN, null));
                params.put("deviceid",Helper.getDeviceId(getActivity()));
                rq  = jsonParser.makeHttpRequest(url, params);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Check your log cat for JSON reponse
          //    Log.d("All jsonarray: ", json.toString());



            return rq;
        }

        protected void onPostExecute(String s) {
            // dismiss the dialog after getting all products

            if (getActivity() == null)
                return;
            if (s == null || s.isEmpty()) {
                Toast.makeText(context, "Server Error", Toast.LENGTH_LONG).show();
                return;
            }
            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {

                    try {
                        JSONObject ack = new JSONObject(s);
                        // Log.d("test", String.valueOf(ack));
                        String decData = Helper.profileDecrypt(ack.get("Data").toString(), ack.get("Hash").toString());
                        if (!Helper.verify(decData, ack.get("Sign").toString(), JSONParserString.publickey)) {
                            Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            JSONObject json = new JSONObject(decData);

                            // Checking for SUCCESS TAG
                            success = json.getInt(TAG_SUCCESS);
                            jsonarrayuser = json.getJSONArray(TAG_USER);
                            jsonarray = json.getJSONArray(TAG_GAME);
                            msg = json.getString("message");
                            //        Log.d("success", String.valueOf(success));
                            if (success == 1) {
                                // looping through All jsonarray
                                for (int i = 0; i < jsonarrayuser.length(); i++) {
                                    JSONObject c = jsonarrayuser.getJSONObject(i);

                                    // creating new HashMap
                                    HashMap<String, String> map = new HashMap<>();

                                    // adding each child node to HashMap key => value
//                                    map.put(TAG_FIRSTNAME, c.getString(TAG_FIRSTNAME));
//                                    map.put(TAG_LASTNAME, c.getString(TAG_LASTNAME));
                                    map.put(GAMEUSERNAME, c.getString(GAMEUSERNAME));
                                    map.put(TAG_ISBAN, c.getString(TAG_ISBAN));
                                    //balance
                                    map.put(TAG_USERBALANCE, c.getString(TAG_USERBALANCE));
                                    map.put(TAG_WINMONEY, c.getString(TAG_WINMONEY));

                                    //matchdetail
                                    map.put(TAG_TOTALMATCHPLAYED, c.getString(TAG_TOTALMATCHPLAYED));
                                    map.put(TAG_WONAMOUNT, c.getString(TAG_WONAMOUNT));
                                    map.put(TAG_KILLS, c.getString(TAG_KILLS));

                                    offersListUser.add(map);
                                }

                                for (int i = 0; i < jsonarray.length(); i++) {

                                    JSONObject c = jsonarray.getJSONObject(i);

                                    // creating new HashMap
                                    HashMap<String, String> map = new HashMap<>();

                                    //gamedetails
                                    map.put(TAG_GAMETITLE, c.getString(TAG_GAMETITLE));
                                    map.put(TAG_GAMEIMG, c.getString(TAG_GAMEIMG));
                                    // adding HashList to ArrayList
                                    offersList.add(map);

                                }

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (success == 1 ) {
                        // jsonarray found
                        // Getting Array of jsonarray
                        //  Updating parsed JSON data into ListView

                        for (int i = 0; i < offersListUser.size(); i++) {

                            // preference and set username for session
//                            editor.putString(TAG_FIRSTNAME, offersListUser.get(i).get(TAG_FIRSTNAME));
//                            editor.putString(TAG_LASTNAME, offersListUser.get(i).get(TAG_LASTNAME));
                            editor.putString(GAMEUSERNAME, offersListUser.get(i).get(GAMEUSERNAME));


                            editor.putBoolean(TAG_ISBAN, Boolean.parseBoolean(offersListUser.get(i).get(TAG_ISBAN)));
                            //balance
                            editor.putInt(TAG_USERBALANCE, Integer.parseInt(offersListUser.get(i).get(TAG_USERBALANCE)));
                            editor.putInt(TAG_WINMONEY, Integer.parseInt(offersListUser.get(i).get(TAG_WINMONEY)));
                            editor.putBoolean("isroot", root);

                            //matchdetail
                            editor.putInt(TAG_TOTALMATCHPLAYED, Integer.parseInt(offersListUser.get(i).get(TAG_TOTALMATCHPLAYED)));
                            editor.putInt(TAG_WONAMOUNT, Integer.parseInt(offersListUser.get(i).get(TAG_WONAMOUNT)));

                            editor.putInt(TAG_KILLS, Integer.parseInt(offersListUser.get(i).get(TAG_KILLS)));
                            editor.apply();
                        }
                        ban = shred.getBoolean(TAG_ISBAN,false);
                        //      Log.d("isban", ban);

//                        if (root) {
//                            getActivity().finish();
//                            Toast.makeText(getActivity()," Cheats Detected ,Please Reset Your Device", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(getActivity(), LoginActivity.class));
                        //  }
//                    else {
                        if (ban) {

                            shred.edit().clear().apply();
                            getActivity().finish();
                            Toast.makeText(getActivity(), "You Have Been Banned", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                        } else {
                            for (int i = 0; i < offersList.size(); i++) {
                                Game game = new Game();
                                game.setTitle(offersList.get(i).get(TAG_GAMETITLE));
                                game.setTopImage(offersList.get(i).get(TAG_GAMEIMG));
                                GameList.add(game);
                                mAdapter.notifyDataSetChanged();
                            }
                            if (GameList.size() <= 0) {
                                mShimmerViewContainer.stopShimmer();
                                mShimmerViewContainer.setVisibility(View.GONE);
                                noMatchesLL.setVisibility(View.VISIBLE);
                                return;
                            }
                            mShimmerViewContainer.stopShimmer();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            noMatchesLL.setVisibility(View.GONE);
                        }
                    }
                    else if (success ==2){
                        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}

