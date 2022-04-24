package com.origin.esports.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.origin.esports.EditProfileActivity;
import com.origin.esports.LoginActivity;
import com.origin.esports.MyWalletActivity;
import com.origin.esports.Originconfig.URL;
import com.origin.esports.R;
import com.origin.esports.TopPlayersActivity;

import static android.content.Context.MODE_PRIVATE;


public class ProfileFragment extends Fragment {

    //user
    private static final String TAG_FIRSTNAME = "firstname";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_EMAIL = "email";

    //balance
    private static final String TAG_USERBALANCE = "balance";
    private static final String TAG_WINMONEY = "winmoney";
    //matchdetail
    private static final String TAG_TOTALMATCHPLAYED = "totalmatchplayed";
    private static final String TAG_WONAMOUNT = "wonamount";
    private static final String TAG_KILLS = "kills";

    //Prefrance

    private TextView winamount;
    private CardView aboutUs;
    private CardView privacypolicy;
    private LinearLayout amountWonLayout;
    private TextView appVersion;
    private int balance;
    private CardView customerSupport;
    private String email;
    private CardView importantUpdates;
    private CardView logOut;
    private LinearLayout matchesPlayedLayout;
    private String matches_played;
    private TextView myAmountWon;
    private TextView myBalance;
    private TextView myKills;
    private TextView myMatchesNumber;
    private CardView myProfile;
    private CardView myStatistics;
    private CardView myWallet;
    private TextView myname;
    private TextView myusername;
    private String name;
    private CardView referEarn;
    private CardView shareApp;
    private CardView topPlayers;
    private LinearLayout totalKillsLayout;
    private String total_amount_won;
    private String total_kills;
    private String username;
    private CardView howtojoin;
    private String  myjoinmoney;
    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        // Inflate the layout for this fragment
        View rootViewone = inflater.inflate(R.layout.fragment_profile, container, false);
        SharedPreferences shred = getActivity().getSharedPreferences("userdetails", MODE_PRIVATE);
        myname = (TextView) rootViewone.findViewById(R.id.name);
        myusername = (TextView) rootViewone.findViewById(R.id.myusername);
        myBalance = (TextView) rootViewone.findViewById(R.id.WonAmount);
        myMatchesNumber = (TextView) rootViewone.findViewById(R.id.matchesPlayed);
        myKills = (TextView) rootViewone.findViewById(R.id.myKills);
        myAmountWon = (TextView) rootViewone.findViewById(R.id.amountWon);
        referEarn = (CardView) rootViewone.findViewById(R.id.referCard);
        myProfile = (CardView) rootViewone.findViewById(R.id.profileCard);
        myWallet = (CardView) rootViewone.findViewById(R.id.myWalletCard);
        myStatistics = (CardView) rootViewone.findViewById(R.id.statsCard);
        topPlayers = (CardView) rootViewone.findViewById(R.id.topPlayersCard);
        importantUpdates = (CardView) rootViewone.findViewById(R.id.impUpdates);
        howtojoin = (CardView) rootViewone.findViewById(R.id.howtoJoinCard);
        aboutUs = (CardView) rootViewone.findViewById(R.id.aboutUsCard);
        privacypolicy = (CardView) rootViewone.findViewById(R.id.privacypolicyCard);
        shareApp = (CardView) rootViewone.findViewById(R.id.shareCard);
        logOut = (CardView) rootViewone.findViewById(R.id.logOutCard);
        appVersion = (TextView) rootViewone.findViewById(R.id.appVersion);
        customerSupport = (CardView) rootViewone.findViewById(R.id.customerSupportCard);
        matchesPlayedLayout = (LinearLayout) rootViewone.findViewById(R.id.matchesPlayedLL);
        totalKillsLayout = (LinearLayout) rootViewone.findViewById(R.id.totalKillsLL);
        amountWonLayout = (LinearLayout) rootViewone.findViewById(R.id.amountWonLL);
        winamount = rootViewone.findViewById(R.id.WonAmountRefer);

        username = shred.getString(TAG_USERNAME,"");
        balance = shred.getInt(TAG_USERBALANCE,0);
        myjoinmoney = String.valueOf(shred.getInt(TAG_WINMONEY,0));
        matches_played = String.valueOf(shred.getInt(TAG_TOTALMATCHPLAYED,0));
        total_kills = String.valueOf(shred.getInt(TAG_KILLS,0));
        total_amount_won = String.valueOf(shred.getInt(TAG_WONAMOUNT,0));
        myname.setText(name);
        myusername.setText(username);
        winamount.setText("₹ "+myjoinmoney);
        myBalance.setText(""+balance);
        myMatchesNumber.setText(matches_played);
        myAmountWon.setText("₹"+total_amount_won);
        myKills.setText(total_kills);
        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), EditProfileActivity.class);
                getContext().startActivity(myIntent);
            }
        });
        if(shred.getBoolean("wallet",false)){
            myWallet.setVisibility(View.VISIBLE);
        }
        myWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), MyWalletActivity.class);
                getContext().startActivity(myIntent);
            }
        });
        topPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), TopPlayersActivity.class);
                getContext().startActivity(myIntent);
            }
        });
        howtojoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/RbKqpng"));
                startActivity(browserIntent);
            }
        });
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL.main+"about/"));
                startActivity(browserIntent);
            }
        });
        privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL.privacypolicy));
                startActivity(browserIntent);
            }
        });
        customerSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "abhoibaidya@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "your_subject");
                intent.putExtra(Intent.EXTRA_TEXT, "your_text");
                startActivity(intent);
            }
        });

        shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL.main+"about/"));
                startActivity(browserIntent);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences shred = getActivity().getSharedPreferences("userdetails", MODE_PRIVATE);
                shred.edit().clear().apply();
                getActivity().finish();
                Toast.makeText(getActivity(), "Logged Out Successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        return rootViewone;
    }
}
