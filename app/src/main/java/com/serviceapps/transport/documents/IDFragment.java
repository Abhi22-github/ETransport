package com.serviceapps.transport.documents;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.realpacific.clickshrinkeffect.ClickShrinkEffect;
import com.serviceapps.transport.R;

import ir.shahabazimi.instagrampicker.InstagramPicker;


public class IDFragment extends Fragment {
    private static final String TAG = "IDFragment";

    //widgets
    private RelativeLayout idFrontSide,idBackSide;
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
        view = inflater.inflate(R.layout.fragment_id, container, false);
        initFields();

        idFrontSide.setOnClickListener(v -> pickFrontSideOfID());
        idBackSide.setOnClickListener(v -> pickBackSideOfID());
        return view;
    }

    private void initFields() {
        idFrontSide = view.findViewById(R.id.front_side_id);
        new ClickShrinkEffect(idFrontSide);
        frontImage = view.findViewById(R.id.front_image);
        frontImageLabel = view.findViewById(R.id.front_image_label);
        idBackSide = view.findViewById(R.id.back_side_id);
        new ClickShrinkEffect(idBackSide);
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