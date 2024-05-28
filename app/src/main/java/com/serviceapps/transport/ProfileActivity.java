package com.serviceapps.transport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.serviceapps.transport.models.ProfileDataClass;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.shahabazimi.instagrampicker.InstagramPicker;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    //widgets
    private RelativeLayout profileImageButton;
    private CircleImageView profileImage;
    private TextInputEditText firstNameField, lastNameField;
    private MaterialButton saveButton;
    private ProgressBar progressBar;

    //vars
    private Uri imageUri;
    private String downloadUri = "";

    //firebase vars
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private StorageReference profilePicsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initFields();

        initFirebase();

        profileImageButton.setOnClickListener(v -> selectProfileImageFromDevice());

        saveButton.setOnClickListener(v -> checkUserData());
    }

    private void initFields() {
        profileImage = findViewById(R.id.profile_image);
        profileImageButton = findViewById(R.id.profile_image_button);
        firstNameField = findViewById(R.id.first_name);
        lastNameField = findViewById(R.id.last_name);
        saveButton = findViewById(R.id.save_button);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        profilePicsRef = FirebaseStorage.getInstance().getReference().child("User Images");
    }

    private void selectProfileImageFromDevice() {
        InstagramPicker instagramPicker = new InstagramPicker(ProfileActivity.this);
        instagramPicker.show(1, 1, address -> {
            imageUri = Uri.parse(address);
            Log.d(TAG, "onActivityResult: " + imageUri);
            profileImage.setImageURI(imageUri);
        });
    }

    private void checkUserData() {
        showProgressDialog();
        if (firstNameField.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter First Name", Toast.LENGTH_SHORT).show();
            hideProgressDialog();
        } else if (lastNameField.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter Last Name", Toast.LENGTH_SHORT).show();
            hideProgressDialog();
        } else {
            UploadProfileImageToFirebaseStorage();
        }

    }

    private void UploadProfileImageToFirebaseStorage() {
        Log.d(TAG, "UploadProfileImageToFirebaseStorage: before bitmap");
        if (imageUri != null) {
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //here you can choose quality factor in third parameter(ex. i choosen 25)
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] fileInBytes = baos.toByteArray();

            Log.d(TAG, "UploadProfileImageToFirebaseStorage: after bitmap");
            StorageReference fileRef = profilePicsRef.child(mAuth.getCurrentUser().getUid() + ".jpg");
            fileRef.putBytes(fileInBytes)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: profile image saved in storage");

                                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Uri img = uri;
                                        downloadUri = img.toString();
                                        Log.d(TAG, "onSuccess: " + downloadUri);
                                        UploadInformationToDatabase(downloadUri);
                                    }
                                });
                            } else {
                                Log.d(TAG, "onComplete: failed to store profile image in storage " + task.getException());
                                hideProgressDialog();
                            }
                        }
                    });
        } else {
            UploadInformationToDatabase(null);
        }
    }

    private void UploadInformationToDatabase(String downloadUri) {

        String phoneNumberString;
        SharedPreferences preferences = getSharedPreferences("Profile", 0);
        phoneNumberString = preferences.getString("phoneNumber", null);

        DocumentReference userRef = firestore.collection("Users").document(mAuth.getCurrentUser().getUid());
        ProfileDataClass userProfileClass = new ProfileDataClass();
        userProfileClass.setProfileImage(downloadUri);
        userProfileClass.setFirstName(firstNameField.getText().toString());
        userProfileClass.setLastName(lastNameField.getText().toString());
        userProfileClass.setPhoneNumber(phoneNumberString);

        userRef.set(userProfileClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: profile is saved in firestore");
                    sendUserToMainActivity();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                        }
                    }, 500);
                } else {
                    Log.d(TAG, "onComplete: unable to save profile in firestore");
                }
            }
        });
    }


    private void showProgressDialog() {
        progressBar.setVisibility(View.VISIBLE);
        saveButton.setText("");
        saveButton.setEnabled(false);
    }

    private void hideProgressDialog() {
        progressBar.setVisibility(View.GONE);
        saveButton.setText("Save");
        saveButton.setEnabled(true);
    }

    private void sendUserToMainActivity() {
        Intent RegisterIntent = new Intent(ProfileActivity.this, MainActivity.class);
        RegisterIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(RegisterIntent);
        finish();
    }

}