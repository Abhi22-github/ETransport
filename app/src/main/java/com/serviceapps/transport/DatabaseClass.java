package com.serviceapps.transport;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.serviceapps.transport.models.ProfileDataClass;
import com.serviceapps.transport.utils.FirestoreCallback;

public class DatabaseClass {
    private static final String TAG = "DatabaseClass";

    private FirebaseAuth mAuth;
    private SharedPreferences profilePreferences;
    private SharedPreferences.Editor profileEditor;
    private FirebaseFirestore firestore;

    public DatabaseClass(Context mContext) {
        mAuth = FirebaseAuth.getInstance();
        profilePreferences = mContext.getSharedPreferences("Profile", Context.MODE_PRIVATE);
        profileEditor = profilePreferences.edit();
        firestore = FirebaseFirestore.getInstance();
    }

    public void gettingUserProfileDataFromDatabaseWithoutCallback() {
        try {
            DocumentReference userStoreRef = firestore.collection("Users").document(mAuth.getCurrentUser().getUid());
            userStoreRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        ProfileDataClass userProfileClass = documentSnapshot.toObject(ProfileDataClass.class);
                       // Log.d(TAG, "onComplete: " + userProfileClass.getFirstName());
                        Gson gson = new Gson();
                        String json = gson.toJson(userProfileClass);
                        profileEditor.putString("userProfileJSON", json);
                        profileEditor.commit();
                    } else {

                    }
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "gettingUserProfileDataFromDatabaseWithoutCallback" + e.getMessage());
        }
    }

    public void gettingUserProfileDataFromDatabaseWithCallback(FirestoreCallback firestoreCallback) {
        try {
            DocumentReference userStoreRef = firestore.collection("Users").document(mAuth.getCurrentUser().getUid());
            userStoreRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        try {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            ProfileDataClass userProfileClass = documentSnapshot.toObject(ProfileDataClass.class);
                            Log.d(TAG, "onComplete: " + userProfileClass.getFirstName());
                            Gson gson = new Gson();
                            String json = gson.toJson(userProfileClass);
                            profileEditor.putString("userProfileJSON", json);
                            profileEditor.commit();
                            firestoreCallback.dataGetComplete();
                        } catch (Exception e) {

                        }
                    } else {

                    }
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "gettingUserProfileDataFromDatabaseWithCallback: " + e.getMessage());
        }
    }

    public ProfileDataClass gettingUserProfileDataFromDevice() {
        ProfileDataClass userProfileClass;
        Gson gson = new Gson();
        String json = profilePreferences.getString("userProfileJSON", "");
        userProfileClass = gson.fromJson(json, ProfileDataClass.class);
        return userProfileClass;
    }
}
