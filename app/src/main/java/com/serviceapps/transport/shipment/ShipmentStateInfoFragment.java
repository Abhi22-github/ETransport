package com.serviceapps.transport.shipment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.serviceapps.transport.EditProfileActivity;
import com.serviceapps.transport.R;
import com.serviceapps.transport.utils.DataValidationCallback;

import ir.shahabazimi.instagrampicker.InstagramPicker;

public class ShipmentStateInfoFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ShipmentStateInfoFragme";

    public ShipmentStateInfoFragment() {
        // Required empty public constructor
    }

    //widgets
    private RelativeLayout yesLayout, noLayout,imageViewLayout;
    private MaterialButton addImageButton;
    private ImageView imageView;

    //vars
    private View view;
    private Boolean liquidProducts = false;
    private SharedPreferences sharedPreferences;
    private Editor editor;
    private Uri imageUri;
    private String downloadUri = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_shipment_state_info, container, false);

        initFields();

        //yes,no selector
        yesLayout.setOnClickListener(this);
        noLayout.setOnClickListener(this);

        addImageButton.setOnClickListener(v ->selectProfileImageFromDevice());

        return view;
    }

    private void initFields() {
        yesLayout = view.findViewById(R.id.yes_layout);
        noLayout = view.findViewById(R.id.no_layout);
        addImageButton = view.findViewById(R.id.select_image_button);
        imageViewLayout = view.findViewById(R.id.image_layout);
        imageView = view.findViewById(R.id.image_view);

        //vars
        sharedPreferences = getContext().getSharedPreferences("Shipment", 0);
        editor = sharedPreferences.edit();
    }

    private void selectProfileImageFromDevice() {
        InstagramPicker instagramPicker = new InstagramPicker(getActivity());
        instagramPicker.show(1, 1, address -> {
            imageUri = Uri.parse(address);
            Log.d(TAG, "onActivityResult: " + imageUri);
            imageViewLayout.setVisibility(View.VISIBLE);
            imageView.setImageURI(imageUri);
            addImageButton.setText("Change Image");
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yes_layout:
                yesLayout.setBackground(getActivity().getDrawable(R.drawable.selected_general_border_layout));
                noLayout.setBackground(getActivity().getDrawable(R.drawable.liquid_products_neutral_background));
                liquidProducts = true;
                break;
            case R.id.no_layout:
                yesLayout.setBackground(getActivity().getDrawable(R.drawable.liquid_products_neutral_background));
                noLayout.setBackground(getActivity().getDrawable(R.drawable.selected_general_border_layout));
                liquidProducts = false;
                break;
        }
    }

    public void checkAllFields(DataValidationCallback dataValidationCallback) {
        editor.putBoolean("liquid", liquidProducts);
        editor.apply();
        dataValidationCallback.dataGetComplete(true);
    }


}