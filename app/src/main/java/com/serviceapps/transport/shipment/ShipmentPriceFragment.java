package com.serviceapps.transport.shipment;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.serviceapps.transport.R;


public class ShipmentPriceFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ShipmentPriceFragment";

    public ShipmentPriceFragment() {
        // Required empty public constructor
    }

    //widgets
    private RelativeLayout firstPrice, secondPrice, thirdPrice;

    //vars
    private View view;
    private String priceRange;
    private SharedPreferences sharedPreferences;
    private Editor editor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_shipment_price, container, false);
        initFields();

        //price selectors
        firstPrice.setOnClickListener(this);
        secondPrice.setOnClickListener(this);
        thirdPrice.setOnClickListener(this);

        return view;
    }

    private void initFields() {
        firstPrice = view.findViewById(R.id.first_price);
        secondPrice = view.findViewById(R.id.second_price);
        thirdPrice = view.findViewById(R.id.third_price);

        //vars
        sharedPreferences = getContext().getSharedPreferences("Shipment",0);
        editor = sharedPreferences.edit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.first_price:
                priceRange = "₹ 200 - 1000";
                break;
            case R.id.second_price:
                priceRange = "₹ 1000 - 7000";
                break;
            case R.id.third_price:
                priceRange = "₹ 7000+";
                break;
        }
        editor.putString("price",priceRange);
        editor.apply();
        ((AddPostActivity) getActivity()).transitViewPagerToFront();
    }
}