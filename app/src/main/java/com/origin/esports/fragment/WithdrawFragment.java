package com.origin.esports.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.origin.esports.Originconfig.URL;
import com.origin.esports.R;
import com.origin.esports.WithdrawMoneyActivity;


import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;


public class WithdrawFragment extends Fragment {



    public CardView paytm, paypal, googlepay, phonepay;
    private TextView winamount;
    private String  myjoinmoney;

    private static final String TAG_WINMONEY = URL.WINMONEY;

    public WithdrawFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootViewone = inflater.inflate(R.layout.fragment_withdraw, container, false);
        SharedPreferences shred = getActivity().getSharedPreferences("userdetails", MODE_PRIVATE);
        paytm = (CardView) rootViewone.findViewById(R.id.paytm);
        paypal = (CardView) rootViewone.findViewById(R.id.paypal);
        googlepay = (CardView) rootViewone.findViewById(R.id.googlepay);
        phonepay = (CardView) rootViewone.findViewById(R.id.phonepay);
        winamount = (TextView) rootViewone.findViewById(R.id.witham);
        myjoinmoney = String.valueOf(shred.getInt(TAG_WINMONEY,0));
        winamount.setText("₹"+myjoinmoney);
        paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getActivity(), WithdrawMoneyActivity.class);
                in.putExtra("mode", "paytm");
                startActivity(in);
            }
        });

        paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getActivity(), WithdrawMoneyActivity.class);
                in.putExtra("mode", "paypal");
                startActivity(in);
            }
        });

        googlepay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getActivity(), WithdrawMoneyActivity.class);
                in.putExtra("mode", "googlepay");
                startActivity(in);
            }
        });

        phonepay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getActivity(), WithdrawMoneyActivity.class);
                in.putExtra("mode", "phonepay");
                startActivity(in);
            }
        });

        return rootViewone;
    }

}
