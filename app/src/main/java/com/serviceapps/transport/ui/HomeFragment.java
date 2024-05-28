package com.serviceapps.transport.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.realpacific.clickshrinkeffect.ClickShrinkEffect;
import com.serviceapps.transport.R;
import com.serviceapps.transport.shipment.AddPostActivity;


public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    //widgets
    private LinearLayout createShipment, searchTransporter;

    //vars
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initFields();

        createShipment.setOnClickListener(v -> sendUserToAddPostActivity());
        searchTransporter.setOnClickListener(v -> sendUserToSearchTransportActivity());
        return view;
    }

    private void initFields() {
        createShipment = view.findViewById(R.id.create_shipment_view);
        searchTransporter = view.findViewById(R.id.search_transporter_view);
        new ClickShrinkEffect(createShipment);
        new ClickShrinkEffect(searchTransporter);
    }

    public void sendUserToAddPostActivity() {
        Intent i = new Intent(getContext(), AddPostActivity.class);
        startActivity(i);
    }
    public void sendUserToSearchTransportActivity() {
        Intent i = new Intent(getContext(), SearchTransportActivity.class);
        startActivity(i);
    }
}