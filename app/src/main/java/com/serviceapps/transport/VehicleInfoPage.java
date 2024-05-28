package com.serviceapps.transport;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.realpacific.clickshrinkeffect.ClickShrinkEffect;

import ir.shahabazimi.instagrampicker.InstagramPicker;

public class VehicleInfoPage extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "VehicleInfoPage";

    //widgets
    private RelativeLayout uploadDocumentLayout;
    private LinearLayout threeWheelerLayout, miniTruckLayout, deliveryVanLayout, truckLayout, otherLayout;
    private ImageButton backButton;
    private LinearLayout imageLabel;
    private RelativeLayout addImageButton;
    private ImageView vehicleImage;

    //vars
    private Uri imageUri;
    private String downloadUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_info_page);
        initFields();

        backButton.setOnClickListener(v -> onBackPressed());

        uploadDocumentLayout.setOnClickListener(v -> sendUserToUploadDocument());

        threeWheelerLayout.setOnClickListener(this);
        miniTruckLayout.setOnClickListener(this);
        deliveryVanLayout.setOnClickListener(this);
        truckLayout.setOnClickListener(this);
        otherLayout.setOnClickListener(this);

        addImageButton.setOnClickListener(v -> selectVehicleImageFromDevice());
    }

    private void initFields() {
        uploadDocumentLayout = findViewById(R.id.upload_document_layout);
        backButton = findViewById(R.id.back);
        threeWheelerLayout = findViewById(R.id.three_wheeler_layout);
        miniTruckLayout = findViewById(R.id.mini_truck_layout);
        deliveryVanLayout = findViewById(R.id.delivery_van_layout);
        truckLayout = findViewById(R.id.truck_layout);
        otherLayout = findViewById(R.id.other_layout);
        backButton = findViewById(R.id.back);
        imageLabel = findViewById(R.id.vehicle_image_label);
        addImageButton = findViewById(R.id.add_image);
        vehicleImage = findViewById(R.id.vehicle_image);

        new ClickShrinkEffect(uploadDocumentLayout);
        new ClickShrinkEffect(threeWheelerLayout);
        new ClickShrinkEffect(miniTruckLayout);
        new ClickShrinkEffect(deliveryVanLayout);
        new ClickShrinkEffect(truckLayout);
        new ClickShrinkEffect(otherLayout);
        new ClickShrinkEffect(addImageButton);
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

    private void selectVehicleImageFromDevice() {
        InstagramPicker instagramPicker = new InstagramPicker(VehicleInfoPage.this);
        instagramPicker.show(1, 1, address -> {
            imageUri = Uri.parse(address);
            Log.d(TAG, "onActivityResult: " + imageUri);
            vehicleImage.setImageURI(imageUri);
            imageLabel.setVisibility(View.GONE);
        });
    }

    private void sendUserToUploadDocument() {
        Intent i = new Intent(VehicleInfoPage.this, RegisterVehicleActivity.class);
        startActivity(i);
    }
}