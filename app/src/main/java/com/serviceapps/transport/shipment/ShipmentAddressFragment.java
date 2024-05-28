package com.serviceapps.transport.shipment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.serviceapps.transport.R;
import com.serviceapps.transport.utils.DataValidationCallback;
import com.serviceapps.transport.utils.MySupportMapFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.serviceapps.transport.utils.Constants.ERROR_DIALOG_REQUEST;
import static com.serviceapps.transport.utils.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;


public class ShipmentAddressFragment extends Fragment {
    private static final String TAG = "ShipmentAddressFragment";

    public ShipmentAddressFragment() {
        // Required empty public constructor
    }

    //widgets
    private TextInputEditText pickupAddress, dropAddress;
    private TextInputLayout pickupLayout, dropLayout;


    //global vars
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 22;
    private static final float DEFAULT_ZOOM = 14f;
    public static LatLng currentLocationLatlng;
    private NestedScrollView scrollView;

    //vars
    private View view;
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private int radius = 2;
    private HashMap<String, Marker> hashMapMarker = new HashMap<>();
    private MarkerOptions options;
    private ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();
    private SharedPreferences sharedPreferences;
    private Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_shipment_address, container, false);
        //initialize Firebase
        initFields();

        //check for whether map permission is given or not
        getLocationPermission();

        return view;
    }

    /**
     * initialization methods
     */
    private void initFields() {
        scrollView = view.findViewById(R.id.scroll_view);
        pickupAddress = view.findViewById(R.id.starting_point_field);
        dropAddress = view.findViewById(R.id.ending_point_field);
        pickupLayout = view.findViewById(R.id.pickup_address_layout);
        dropLayout = view.findViewById(R.id.drop_address_layout);

        //vars
        sharedPreferences = getContext().getSharedPreferences("Shipment", 0);
        editor = sharedPreferences.edit();
    }


    @Override
    public void onStart() {
        super.onStart();
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                initMap();
            } else {
                getLocationPermission();
            }
        }
    }

    private void getLocationPermission() {
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        //check if both permission are granted
        if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;

                //if both permission are granted then initialize the map
                initMap();
            }
        } // if not ask for permissions dialogue
        else {
            ActivityCompat.requestPermissions(getActivity(),
                    permission,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initMap() {
        //get the map fragment from xml file

        MySupportMapFragment mapFragment;
        mapFragment = (MySupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.setListener(new MySupportMapFragment.OnTouchListener() {
                @Override
                public void onTouch() {
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
            });
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("LongLogTag")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG, "onMapReady: map is ready");
                mMap = googleMap;
                //if all permissions are granted the get device current location
                if (mLocationPermissionGranted) {
                    Log.d(TAG, "onMapReady: displaying current location");
                    getDeviceLocation();
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        //display msg to user that you have to give permission to access map
                        return;
                    }
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    mMap.getUiSettings().setMapToolbarEnabled(true);
                    mMap.getUiSettings().isZoomControlsEnabled();

                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            if (markerPoints.size() > 1) {
                                markerPoints.clear();
                                mMap.clear();
                            }

                            // Adding new item to the ArrayList
                            markerPoints.add(latLng);


                            // Creating MarkerOptions
                            options = new MarkerOptions();

                            // Setting the position of the marker
                            options.position(latLng);

                            if (markerPoints.size() == 1) {
                                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                String addressFromLocation = getAddressFromLocation(markerPoints.get(0));
                                pickupAddress.setText(addressFromLocation);
                            } else if (markerPoints.size() == 2) {
                                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                String addressFromLocation = getAddressFromLocation(markerPoints.get(1));
                                dropAddress.setText(addressFromLocation);
                            }

                            try {
                                // Add new marker to the Google Map Android API V2
                                mMap.addMarker(options);
                            } catch (Exception e) {

                            }

                        }
                    });


                }
            }
        });
    }

    private String getAddressFromLocation(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String Address;
        Address = address.replace("Unnamed Road, ", "");
        return Address;
    }


    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the current device current location");

        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            if (mLocationPermissionGranted) {
                Task location = mfusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "onComplete: location found");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())
                                    , DEFAULT_ZOOM);
                            currentLocationLatlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());


                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getContext(), "unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException" + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving camera to lat" + latLng.latitude + "lng" + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            //if LOCATION_PERMISSION_REQUEST_CODE is equal to requestcode
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    // check if both permissions are granted
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    // if both permissions are granted initialize our map
                    initMap();
                }
            }
        }
    }

    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable((Activity) getContext());

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog((Activity) getContext(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getContext(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            askUserToTurnOnLocation();
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    initMap();
                } else {
                    getLocationPermission();
                }
            }
        }
        getLocationPermission();
    }

    private void askUserToTurnOnLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(getContext()).checkLocationSettings(builder.build());


        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        (Activity) getContext(),
                                        LocationRequest.PRIORITY_HIGH_ACCURACY);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });
    }

    public void checkAllFields(DataValidationCallback dataValidationCallback) {
        if (pickupAddress.getText().toString().isEmpty() || dropAddress.getText().toString().isEmpty()) {
            if (pickupAddress.getText().toString().isEmpty()) {

            }
            if (dropAddress.getText().toString().isEmpty()) {

            }
            dataValidationCallback.dataGetComplete(false);
        } else {
            editor.putString("pickupAddress", pickupAddress.getText().toString());
            editor.putString("dropAddress", dropAddress.getText().toString());
            editor.putString("pickupAddressLatitude",String.valueOf(markerPoints.get(0).latitude));
            editor.putString("pickupAddressLongitude",String.valueOf(markerPoints.get(0).longitude));
            editor.putString("dropAddressLatitude",String.valueOf(markerPoints.get(1).latitude));
            editor.putString("dropAddressLongitude",String.valueOf(markerPoints.get(1).longitude));
            editor.apply();
            dataValidationCallback.dataGetComplete(true);
        }
    }
}