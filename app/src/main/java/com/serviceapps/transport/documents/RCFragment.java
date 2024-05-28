package com.serviceapps.transport.documents;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.realpacific.clickshrinkeffect.ClickShrinkEffect;
import com.serviceapps.transport.R;

import ir.shahabazimi.instagrampicker.InstagramPicker;


public class RCFragment extends Fragment {
    private static final String TAG = "RCFragment";

    //widgets
    private RelativeLayout rcFrontSide,rcBackSide;
    private ImageView frontImage,backImage;
    private LinearLayout frontImageLabel,backImageLabel;

    //vars
    private View view;
    private Uri frontImageUri,backImageUri;
    private String downloadUri = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_rc, container, false);
        initFields();

        rcFrontSide.setOnClickListener(v -> pickFrontSideOfID());
        rcBackSide.setOnClickListener(v -> pickBackSideOfID());
        return view;
    }

    private void initFields() {
        rcFrontSide = view.findViewById(R.id.front_side_rc);
        new ClickShrinkEffect(rcFrontSide);
        frontImage = view.findViewById(R.id.front_image);
        frontImageLabel = view.findViewById(R.id.front_image_label);
        rcBackSide = view.findViewById(R.id.back_side_rc);
        new ClickShrinkEffect(rcBackSide);
        backImage = view.findViewById(R.id.back_image);
        backImageLabel = view.findViewById(R.id.back_image_label);
    }

    private void pickFrontSideOfID() {
        InstagramPicker instagramPicker = new InstagramPicker(getActivity());
        instagramPicker.show(1, 1, address -> {
            frontImageUri = Uri.parse(address);
            Log.d(TAG, "onActivityResult: " + frontImageUri);
            frontImage.setImageURI(frontImageUri);
            frontImageLabel.setVisibility(View.GONE);
        });
    }
    private void pickBackSideOfID(){
        InstagramPicker instagramPicker = new InstagramPicker(getActivity());
        instagramPicker.show(1, 1, address -> {
            backImageUri = Uri.parse(address);
            Log.d(TAG, "onActivityResult: " + backImageUri);
            backImage.setImageURI(backImageUri);
            backImageLabel.setVisibility(View.GONE);
        });
    }
}