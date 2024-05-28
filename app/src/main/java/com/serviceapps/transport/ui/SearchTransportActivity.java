package com.serviceapps.transport.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.realpacific.clickshrinkeffect.ClickShrinkEffect;
import com.serviceapps.transport.R;

public class SearchTransportActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SearchTransportActivity";

    //widgets
    private LinearLayout threeWheelerLayout, miniTruckLayout, deliveryVanLayout, truckLayout, otherLayout;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_transport);
        initFields();

        threeWheelerLayout.setOnClickListener(this);
        miniTruckLayout.setOnClickListener(this);
        deliveryVanLayout.setOnClickListener(this);
        truckLayout.setOnClickListener(this);
        otherLayout.setOnClickListener(this);

        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void initFields() {
        threeWheelerLayout = findViewById(R.id.three_wheeler_layout);
        miniTruckLayout = findViewById(R.id.mini_truck_layout);
        deliveryVanLayout = findViewById(R.id.delivery_van_layout);
        truckLayout = findViewById(R.id.truck_layout);
        otherLayout = findViewById(R.id.other_layout);
        backButton = findViewById(R.id.back);

        new ClickShrinkEffect(threeWheelerLayout);
        new ClickShrinkEffect(miniTruckLayout);
        new ClickShrinkEffect(deliveryVanLayout);
        new ClickShrinkEffect(truckLayout);
        new ClickShrinkEffect(otherLayout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.three_wheeler_layout:
                threeWheelerLayout.setBackground(getDrawable(R.drawable.selected_general_border_layout));
               // pickerLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                miniTruckLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                deliveryVanLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                truckLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                otherLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                break;
            case R.id.mini_truck_layout:
                threeWheelerLayout.setBackground(getDrawable(R.drawable.general_border_layout));
               // pickerLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                miniTruckLayout.setBackground(getDrawable(R.drawable.selected_general_border_layout));
                deliveryVanLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                truckLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                otherLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                break;
            case R.id.delivery_van_layout:
                threeWheelerLayout.setBackground(getDrawable(R.drawable.general_border_layout));
               // pickerLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                miniTruckLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                deliveryVanLayout.setBackground(getDrawable(R.drawable.selected_general_border_layout));
                truckLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                otherLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                break;
            case R.id.truck_layout:
                threeWheelerLayout.setBackground(getDrawable(R.drawable.general_border_layout));
               // pickerLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                miniTruckLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                deliveryVanLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                truckLayout.setBackground(getDrawable(R.drawable.selected_general_border_layout));
                otherLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                break;
            case R.id.other_layout:
                threeWheelerLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                //pickerLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                miniTruckLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                deliveryVanLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                truckLayout.setBackground(getDrawable(R.drawable.general_border_layout));
                otherLayout.setBackground(getDrawable(R.drawable.selected_general_border_layout));
                break;
        }
    }
}