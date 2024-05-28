package com.serviceapps.transport.shipment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.serviceapps.transport.R;
import com.serviceapps.transport.utils.DataValidationCallback;


public class ShipmentDetailsFragment extends Fragment implements TextWatcher {
    private static final String TAG = "ShipmentDetailsFragment";

    public ShipmentDetailsFragment() {
        // Required empty public constructor
    }

    //widgets
    private TextInputEditText weight, height, quantity;
    private AutoCompleteTextView weightUnit, heightUnit;
    private TextInputLayout weightLayout, heightLayout, quantityLayout;

    //vars
    private View view;
    private String[] weightArray = {"kg", "ton"};
    private String[] heightArray = {"ft", "m"};
    private SharedPreferences sharedPreferences;
    private Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_shipment_details, container, false);
        initFields();
        //setting up adapter on units
        setUpAdapterOnUnitFields();

        //setting text watcher on both fields
        weight.addTextChangedListener(this);
        height.addTextChangedListener(this);
        return view;
    }

    private void initFields() {
        quantity = view.findViewById(R.id.quantity_field);
        quantityLayout = view.findViewById(R.id.quantity_layout);
        height = view.findViewById(R.id.height_field);
        heightLayout = view.findViewById(R.id.height_layout);
        weight = view.findViewById(R.id.weight_field);
        weightLayout = view.findViewById(R.id.weight_layout);
        weightUnit = view.findViewById(R.id.weight_units_field);
        heightUnit = view.findViewById(R.id.height_units_field);

        //vars initialization
        sharedPreferences =  getContext().getSharedPreferences("Shipment",0);
        editor = sharedPreferences.edit();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpAdapterOnUnitFields() {

        //weight adapter
        ArrayAdapter<String> weightAdapter = new ArrayAdapter<String>
                (getContext(), R.layout.select_dialog_item_layout, weightArray);

        //height adapter
        ArrayAdapter<String> heightAdapter = new ArrayAdapter<String>
                (getContext(), R.layout.select_dialog_item_layout, heightArray);

        //setting threshold and adapters
        weightUnit.setAdapter(weightAdapter);
        weightUnit.setThreshold(0);

        heightUnit.setAdapter(heightAdapter);
        heightUnit.setThreshold(0);

        weightUnit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                weightUnit.showDropDown();
                return false;
            }
        });

        heightUnit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                heightUnit.showDropDown();
                return false;
            }
        });

    }

    public void checkAllFields(DataValidationCallback dataValidationCallback) {
        if (weight.getText().toString().isEmpty() || height.getText().toString().isEmpty()) {
            if (weight.getText().toString().isEmpty()) {
                weightLayout.setErrorEnabled(true);
                weightLayout.setBoxStrokeWidth(4);
                weightLayout.setError("enter shipment weight");

            }
            if (height.getText().toString().isEmpty()) {
                heightLayout.setErrorEnabled(true);
                heightLayout.setBoxStrokeWidth(4);
                heightLayout.setError("enter shipment height");
            }
            dataValidationCallback.dataGetComplete(false);
        }else {
            editor.putString("weight",weight.getText().toString());
            editor.putString("weightUnit",weightUnit.getText().toString());
            editor.putString("height",height.getText().toString());
            editor.putString("heightUnit",heightUnit.getText().toString());
            editor.putString("quantity",quantity.getText().toString());
            editor.apply();
            dataValidationCallback.dataGetComplete(true);
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!weight.getText().toString().isEmpty()) {
            weightLayout.setErrorEnabled(false);
            weightLayout.setBoxStrokeWidth(1);

        }
        if (!height.getText().toString().isEmpty()) {
            heightLayout.setErrorEnabled(false);
            heightLayout.setBoxStrokeWidth(1);

        }
    }
}