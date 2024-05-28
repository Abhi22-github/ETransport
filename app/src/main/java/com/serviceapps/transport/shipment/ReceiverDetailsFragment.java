package com.serviceapps.transport.shipment;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.serviceapps.transport.R;
import com.serviceapps.transport.utils.DataValidationCallback;


public class ReceiverDetailsFragment extends Fragment implements TextWatcher {
    private static final String TAG = "ReceiverDetailsFragment";

    public ReceiverDetailsFragment() {
        // Required empty public constructor
    }

    //widgets
    private TextInputLayout receiverNameLayout,receiverNumberLayout;
    private TextInputEditText receiverName,receiverNumber;

    //vars
    private View view;
    private SharedPreferences sharedPreferences;
    private Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_receiver_details_, container, false);
        initFields();

        //name,number textWatcher
        receiverName.addTextChangedListener(this);
        receiverNumber.addTextChangedListener(this);

        return view;
    }

    private void initFields() {
        receiverNameLayout = view.findViewById(R.id.receiver_name_layout);
        receiverNumberLayout = view.findViewById(R.id.receiver_number_layout);
        receiverName = view.findViewById(R.id.receiver_name);
        receiverNumber = view.findViewById(R.id.receiver_number);

        //vars
        sharedPreferences = getContext().getSharedPreferences("Shipment",0);
        editor = sharedPreferences.edit();
    }

    public  void checkAllFields(DataValidationCallback dataValidationCallback){
        if(receiverName.getText().toString().isEmpty() || receiverNumber.getText().toString().isEmpty()){
            if(receiverName.getText().toString().isEmpty()){
                receiverNameLayout.setErrorEnabled(true);
                receiverNameLayout.setBoxStrokeWidth(4);
                receiverNameLayout.setError("enter receiver name");
            }
            if(receiverNumber.getText().toString().isEmpty()){
                receiverNumberLayout.setErrorEnabled(true);
                receiverNumberLayout.setBoxStrokeWidth(4);
                receiverNumberLayout.setError("enter receiver number");
            }
        }else {
            if(receiverNumber.getText().toString().length()<10 || receiverNumber.getText().toString().length()>10){
                receiverNumberLayout.setErrorEnabled(true);
                receiverNumberLayout.setBoxStrokeWidth(4);
                receiverNumberLayout.setError("enter valid number");
            }else {
                editor.putString("name",receiverName.getText().toString());
                editor.putString("number",receiverNumber.getText().toString());
                editor.apply();
                dataValidationCallback.dataGetComplete(true);
            }
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
        if (!receiverName.getText().toString().isEmpty()) {
            receiverNameLayout.setErrorEnabled(false);
            receiverNameLayout.setBoxStrokeWidth(1);

        }
        if (!receiverNumber.getText().toString().isEmpty()) {
            receiverNumberLayout.setErrorEnabled(false);
            receiverNumberLayout.setBoxStrokeWidth(1);

        }
    }
}