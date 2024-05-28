package com.serviceapps.transport;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.serviceapps.transport.ui.AccountFragment;
import com.serviceapps.transport.ui.HomeFragment;
import com.serviceapps.transport.ui.MapFragment;
import com.serviceapps.transport.verification.VerifyNumber;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    //widgets
    private BottomNavigationView bottomNavigationView;
    private FrameLayout mainContentLayout;
    private ConstraintLayout constraintLayout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    //vars
    private Fragment active;
    final FragmentManager fm = getSupportFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFields();
        initFirebase();
        bottomNavigationBarSetup();
    }

    private void initFields() {
        bottomNavigationView = findViewById(R.id.main_activity_bottom_navigation_fragment_holder);
        mainContentLayout = findViewById(R.id.main_activity_frame_layout_fragment_container);
        constraintLayout = findViewById(R.id.constraints_layout);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    private void bottomNavigationBarSetup() {
        final Fragment homeFragment = new HomeFragment();
       // final Fragment mapFragment = new MapFragment();
        final Fragment accountFragment = new AccountFragment();
        active = homeFragment;

        fm.beginTransaction().add(R.id.main_activity_frame_layout_fragment_container, accountFragment, "3").hide(accountFragment).commitAllowingStateLoss();
      //  fm.beginTransaction().add(R.id.main_activity_frame_layout_fragment_container, mapFragment, "2").hide(mapFragment).commitAllowingStateLoss();
        fm.beginTransaction().add(R.id.main_activity_frame_layout_fragment_container, homeFragment, "1").commitAllowingStateLoss();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.bottom_navigation_home:
                        fm.beginTransaction().hide(active).show(homeFragment).commitAllowingStateLoss();
                        active = homeFragment;

                        break;
                  //  case R.id.bottom_navigation_add:
                  //      fm.beginTransaction().hide(active).show(mapFragment).commitAllowingStateLoss();
                   //     active = mapFragment;


                    //    break;
                    case R.id.bottom_navigation_person:
                        fm.beginTransaction().hide(active).show(accountFragment).commitAllowingStateLoss();
                        active = accountFragment;
                        break;

                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isOnline()) {
            //databaseClass.savingUserTokenInDatabase();
            checkUser();
        } else {
            NoInternetConnectionSnackbar();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private void NoInternetConnectionSnackbar() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "No internet connection", Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(getResources().getColor(R.color.green))
                .setAction("Refresh", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isOnline()) {
                            checkUser();
                        } else {
                            NoInternetConnectionSnackbar();
                        }
                    }
                });
        snackbar.show();
    }

    public void checkUser() {
        if (isOnline()) {
            Log.d(TAG, "onStart: ");

            //showLoadingProgressbar();
            if (mAuth.getCurrentUser() == null) {
                //if not present then create account
                sendUserToRegisterOTPActivity();

            } else {
                Log.d(TAG, "onStart: checking if profile is completed");
                //else check if profile progress is completed or not
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        verifyUserExistence();
                    }
                };

                Thread thread = new Thread(runnable);
                thread.start();
            }
        } else {

        }
    }

    private void sendUserToRegisterOTPActivity() {
        Intent registerOTPActivity = new Intent(MainActivity.this, VerifyNumber.class);
        registerOTPActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(registerOTPActivity);
        finish();
    }

    private void verifyUserExistence() {
        String currentUserID = mAuth.getCurrentUser().getUid();
        DocumentReference userStoreRef = firestore.collection("Users").document(currentUserID);
        userStoreRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                    } else {
                       // progressBar.setVisibility(View.GONE);
                        mainContentLayout.setVisibility(View.VISIBLE);
                        sendUserToUserUserProfileActivity();
                    }
                } else {
                   // progressBar.setVisibility(View.GONE);
                    mainContentLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });


    }

    private void sendUserToUserUserProfileActivity() {
        Intent userNameIntent = new Intent(MainActivity.this, ProfileActivity.class);
        userNameIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(userNameIntent);
        finish();
    }

}