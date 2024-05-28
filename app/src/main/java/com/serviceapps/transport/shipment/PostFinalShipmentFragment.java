package com.serviceapps.transport.shipment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.serviceapps.transport.MainActivity;
import com.serviceapps.transport.R;
import com.serviceapps.transport.adapters.ShipmentDataClass;


public class PostFinalShipmentFragment extends Fragment {
    private static final String TAG = "PostFinalShipmentFragme";

    public PostFinalShipmentFragment() {
        // Required empty public constructor
    }

    //widgets
    private TextInputLayout receiverNameLayout, receiverContactLayout, weightLayout, heightLayout, quantityLayout, pickupAddressLayout, dropAddressLayout;
    private TextInputEditText receiverName, receiverContact, weight, height, quantity, pickupAddress, dropAddress;
    private AutoCompleteTextView weightUnit, heightUnit;
    private MaterialButton postButton;
    private TextView quantityLabel;
    private Spinner priceSpinner;
private ProgressBar progressBar;

    //vars
    private View view;
    private SharedPreferences sharedPreferences;
    private String nameString, contactString, weightString, weightUnitString, heightString, heightUnitString, quantityString, pickupAddressString, dropAddressString;
    String[] price = {"₹ 200 - 1000", "₹ 1000 - 7000", "₹ 7000+"};

    //firebase vars
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_post_final_shipment, container, false);
        initFields();

        getShipmentData();

        ArrayAdapter aa = new ArrayAdapter(getContext(), R.layout.select_dialog_item_layout, price);
        //Setting the ArrayAdapter data on the Spinner
        priceSpinner.setAdapter(aa);

        postButton.setOnClickListener(v -> postShipmentToDatabase());
        return view;
    }

    private void initFields() {
        receiverName = view.findViewById(R.id.receiver_name);
        receiverNameLayout = view.findViewById(R.id.receiver_name_layout);
        receiverContact = view.findViewById(R.id.receiver_number);
        receiverContactLayout = view.findViewById(R.id.receiver_number_layout);
        weightLayout = view.findViewById(R.id.weight_layout);
        weight = view.findViewById(R.id.weight_field);
        weightUnit = view.findViewById(R.id.weight_units_field);
        heightLayout = view.findViewById(R.id.height_layout);
        height = view.findViewById(R.id.height_field);
        heightUnit = view.findViewById(R.id.height_units_field);
        quantityLayout = view.findViewById(R.id.quantity_layout);
        quantity = view.findViewById(R.id.quantity_field);
        pickupAddressLayout = view.findViewById(R.id.pickup_address_layout);
        pickupAddress = view.findViewById(R.id.pickup_address);
        dropAddressLayout = view.findViewById(R.id.drop_address_layout);
        dropAddress = view.findViewById(R.id.drop_address);
        postButton = view.findViewById(R.id.post_button);
        quantityLabel = view.findViewById(R.id.quantity_label);
        priceSpinner = view.findViewById(R.id.spinner);
        progressBar = view.findViewById(R.id.progress_bar);

        //vars
        sharedPreferences = getContext().getSharedPreferences("Shipment", 0);

        //firebase vars
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    private void getShipmentData() {
        try {
            nameString = sharedPreferences.getString("name", null);
            contactString = sharedPreferences.getString("number", null);
            weightString = sharedPreferences.getString("weight", null);
            weightUnitString = sharedPreferences.getString("weightUnit", null);
            heightString = sharedPreferences.getString("height", null);
            heightUnitString = sharedPreferences.getString("heightUnit", null);
            quantityString = sharedPreferences.getString("quantity", null);
            pickupAddressString = sharedPreferences.getString("pickupAddress", null);
            dropAddressString = sharedPreferences.getString("dropAddress", null);
        } catch (Exception e) {

        }
        receiverName.setText(nameString);
        receiverContact.setText(contactString);
        weight.setText(weightString);
        height.setText(heightString);
        if (quantityString == null || quantityString.isEmpty()) {
            quantityLayout.setVisibility(View.GONE);
            quantityLabel.setVisibility(View.GONE);
        } else {
            quantityLayout.setVisibility(View.VISIBLE);
            quantityLabel.setVisibility(View.VISIBLE);
            quantity.setText(quantityString);
        }
        pickupAddress.setText(pickupAddressString);
        dropAddress.setText(dropAddressString);

    }

    private void postShipmentToDatabase() {
        progressBar.setVisibility(View.VISIBLE);
        ShipmentDataClass shipmentDataClass = new ShipmentDataClass();
        shipmentDataClass.setReceiverName(receiverName.getText().toString().trim());
        shipmentDataClass.setReceiverNumber(receiverContact.getText().toString().trim());
        shipmentDataClass.setWeight(weight.getText().toString().trim());
        shipmentDataClass.setWeightUnit(weightUnit.getText().toString().trim());
        shipmentDataClass.setHeight(height.getText().toString().trim());
        shipmentDataClass.setHeightUnit(heightUnit.getText().toString().trim());
        shipmentDataClass.setQuantity(quantity.getText().toString().trim());
        shipmentDataClass.setPriceRange(priceSpinner.getSelectedItem().toString().trim());
        shipmentDataClass.setPickupAddress(pickupAddress.getText().toString().trim());
        shipmentDataClass.setDropAddress(dropAddress.getText().toString().trim());
        GeoPoint pickUpLocation =
                new GeoPoint(Double.parseDouble(sharedPreferences.getString("pickupAddressLatitude", null)),
                        Double.parseDouble(sharedPreferences.getString("pickupAddressLongitude", null)));
        GeoPoint dropLocation =
                new GeoPoint(Double.parseDouble(sharedPreferences.getString("dropAddressLatitude", null)),
                        Double.parseDouble(sharedPreferences.getString("dropAddressLongitude", null)));
        shipmentDataClass.setPickupLocation(pickUpLocation);
        shipmentDataClass.setDropLocation(dropLocation);

        String path = firestore.collection("Shipments").document().getId();
        firestore.collection("Shipments").document(path).set(shipmentDataClass)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: data is saved in database successfully");
                            sendUserToMainActivity();
                        }else {
                            Log.d(TAG, "onComplete: error whle saving shipment data "+ task.getException());
                        }
                    }
                });
    }

    private void sendUserToMainActivity() {
        Intent i = new Intent(getContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        progressBar.setVisibility(View.GONE);
    }
}