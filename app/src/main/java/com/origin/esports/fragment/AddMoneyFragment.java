package com.origin.esports.fragment;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.origin.esports.MyWalletActivity;
import com.origin.esports.Originconfig.URL;
import com.origin.esports.R;

import static android.content.Context.MODE_PRIVATE;


public class AddMoneyFragment extends Fragment {

    //user
    private static final String TAG_FIRSTNAME = URL.FIRSTNAME;
    private static final String TAG_USERNAME = URL.USERNAME;
    private static final String TAG_EMAIL = URL.EMAIL;
    private static final String TAG_MOBILE = URL.MOBILE;

    private Button addmoney;
    private EditText amount;
    private String email;
    private TextView errorMessage;
    private String name;
    private String number;
    private final String paymentGateway="paytm";
    private String username;

    //Prefrance


    public LinearLayout paytmln, paypalln, instamojoln;
    public RadioButton paytm, paypal, instamojo;

    public AddMoneyFragment() {
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
        View rootViewone = inflater.inflate(R.layout.fragment_addmoney, container, false);
        SharedPreferences shred = getActivity().getSharedPreferences("userdetails", MODE_PRIVATE);
        amount = (EditText) rootViewone.findViewById(R.id.amountEditText);
        addmoney = (Button) rootViewone.findViewById(R.id.addButton);
        errorMessage = (TextView) rootViewone.findViewById(R.id.errorMessage);

        paytmln = (LinearLayout) rootViewone.findViewById(R.id.paytmln);
        paypalln = (LinearLayout) rootViewone.findViewById(R.id.paypalln);
        instamojoln = (LinearLayout) rootViewone.findViewById(R.id.instamojoln);

        if(!URL.paytm) {
            paytmln.setVisibility(View.GONE);
        }
        if(!URL.paypal) {
            paypalln.setVisibility(View.GONE);
        }
        if(!URL.instamojo) {
            instamojoln.setVisibility(View.GONE);
        }

        paytm = (RadioButton) rootViewone.findViewById(R.id.radio0);
        paypal = (RadioButton) rootViewone.findViewById(R.id.radio01);
        instamojo = (RadioButton) rootViewone.findViewById(R.id.radio02);

        username = shred.getString(TAG_USERNAME,"");
        email = shred.getString(TAG_EMAIL,"");
        name = shred.getString(TAG_FIRSTNAME,"");
        number = shred.getString(TAG_MOBILE,"");
//        paymentGateway = prf.getString("paymentGateway", "paytm");

        addmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String obj = amount.getText().toString();
                if (!obj.isEmpty()) {
                    int amt = Integer.parseInt(obj);
                    if (amt < 20) {
                        errorMessage.setVisibility(View.VISIBLE);
                    } else if (amt > 20 || amt == 20) {
                        errorMessage.setVisibility(View.GONE);

//                        if (paymentGateway.equals("instamojo")) {
//                            ((MyWalletActivity) getActivity()).callInstamojoPay(email, number, obj, "Add Money to Wallet", name);
//                        }

                        if (paytm.isChecked()) {
                            ((MyWalletActivity)(getActivity())).PaytmAddMoney(obj,email,number,name);

//                        } else if (paypal.isChecked()){
//                            ((MyWalletActivity) getActivity()).onBraintreeSubmit(email, number, obj, "Add Money to Wallet", name);
//                        } else if (instamojo.isChecked()){
//                            ((MyWalletActivity) getActivity()).callInstamojoPay(email, number, obj, "Add Money to Wallet", name);
                        } else {
                            Toast.makeText(getActivity(), "Select Any Payment Gateway", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (obj.isEmpty()) {
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("Enter minimum Rs 20");
                    errorMessage.setTextColor(Color.parseColor("#ff0000"));
                }
            }
        });

        paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paytm.setChecked(true);
                paypal.setChecked(false);
                instamojo.setChecked(false);
            }
        });

        paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paytm.setChecked(false);
                paypal.setChecked(true);
                instamojo.setChecked(false);
            }
        });

        instamojo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paytm.setChecked(false);
                paypal.setChecked(false);
                instamojo.setChecked(true);
            }
        });

        return rootViewone;
    }

}
