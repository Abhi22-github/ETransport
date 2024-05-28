package com.serviceapps.transport.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.realpacific.clickshrinkeffect.ClickShrinkEffect;
import com.serviceapps.transport.DatabaseClass;
import com.serviceapps.transport.EditProfileActivity;
import com.serviceapps.transport.R;
import com.serviceapps.transport.VehicleInfoPage;
import com.serviceapps.transport.models.ProfileDataClass;
import com.serviceapps.transport.utils.FirestoreCallback;
import com.serviceapps.transport.verification.VerifyNumber;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class AccountFragment extends Fragment {
    private static final String TAG = "AccountFragment";

    //widgets
    private LinearLayout registerVehicleLayout;
    private TextView editProfile, userName, userNumber;
    private LinearLayout aboutUs;
    private CircleImageView profileImage;
    private ProgressBar imageProgressBar;

    //vars
    private View view;

    //firebase vars
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_account, container, false);

        initFields();

        initFirebase();


        new ClickShrinkEffect(registerVehicleLayout);

        registerVehicleLayout.setOnClickListener(v -> sendUserToInfo());

        editProfile.setOnClickListener(v -> sendUserToProfile());

        aboutUs.setOnClickListener(v -> sendToLogin());

        return view;
    }


    private void initFields() {
        registerVehicleLayout = view.findViewById(R.id.register_your_vehicle_layout);
        editProfile = view.findViewById(R.id.edit_profile);
        aboutUs = view.findViewById(R.id.about_us);
        userName = (TextView) view.findViewById(R.id.user_name);
        userNumber = (TextView) view.findViewById(R.id.user_number);
        profileImage = view.findViewById(R.id.profile_image);
        imageProgressBar = view.findViewById(R.id.image_progress_bar);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        getProfileDataFromDevice();
    }

    private void getProfileDataFromDevice() {
        try {

            final ProfileDataClass[] profileDataClass = {new ProfileDataClass()};
            DatabaseClass databaseClass = new DatabaseClass(getContext());
            databaseClass.gettingUserProfileDataFromDatabaseWithCallback(new FirestoreCallback() {
                @Override
                public void dataGetComplete() {
                    profileDataClass[0] = databaseClass.gettingUserProfileDataFromDevice();
                    userNumber.setText(profileDataClass[0].getPhoneNumber());
                    userName.setText(profileDataClass[0].getFirstName() + " " + profileDataClass[0].getLastName());
                    userNumber.setText(profileDataClass[0].getPhoneNumber());
                    imageProgressBar.setVisibility(View.VISIBLE);
                    Picasso.get().load(profileDataClass[0].getProfileImage()).into(profileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            imageProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            imageProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
            });


        } catch (Exception e) {
            Log.d(TAG, "getProfileDataFromDevice: " + e.getMessage());
        }

    }


    private void sendUserToInfo() {
        Intent i = new Intent(getContext(), VehicleInfoPage.class);
        startActivity(i);
    }

    private void sendUserToProfile() {
        Intent i = new Intent(getContext(), EditProfileActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void sendToLogin() {
        Intent i = new Intent(getContext(), VerifyNumber.class);
        startActivity(i);
    }
}