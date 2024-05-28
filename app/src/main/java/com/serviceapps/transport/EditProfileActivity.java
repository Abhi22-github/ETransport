package com.serviceapps.transport;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.shahabazimi.instagrampicker.InstagramPicker;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";

    //widgets
    private RelativeLayout profileImageButton;
    private CircleImageView profileImage;
    private TextInputEditText firstNameField, lastNameField;
    private MaterialButton saveButton;
    private ImageButton backButton;
    private ProgressBar progressBar, imageProgressBar;
    private ProfileDataClass profileDataClass;
    private ScrollView scrollView;

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
        setContentView(R.layout.activity_edit_profile);

        initFields();

        initFirebase();

        getProfileDataFromParse();

        profileImageButton.setOnClickListener(v -> selectProfileImageFromDevice());

        saveButton.setOnClickListener(v -> checkUserData());

        backButton.setOnClickListener(v -> onBackPressed());

    }

    private void initFields() {
        profileImage = findViewById(R.id.profile_image);
        profileImageButton = findViewById(R.id.profile_image_button);
        firstNameField = findViewById(R.id.first_name);
        lastNameField = findViewById(R.id.last_name);
        saveButton = findViewById(R.id.save_button);
        backButton = findViewById(R.id.back);
        progressBar = findViewById(R.id.progress_bar);
        imageProgressBar = findViewById(R.id.image_progress_bar);
        //scrollView = findViewById(R.id.scroll_view);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        profilePicsRef = FirebaseStorage.getInstance().getReference().child("User Images");
    }

    private void selectProfileImageFromDevice() {
        InstagramPicker instagramPicker = new InstagramPicker(EditProfileActivity.this);
        instagramPicker.show(1, 1, address -> {
            imageUri = Uri.parse(address);
            Log.d(TAG, "onActivityResult: " + imageUri);
            profileImage.setImageURI(imageUri);
        });
    }

    private void getProfileDataFromParse() {
        DatabaseClass databaseClass = new DatabaseClass(EditProfileActivity.this);
        databaseClass.gettingUserProfileDataFromDatabaseWithoutCallback();

        profileDataClass = databaseClass.gettingUserProfileDataFromDevice();
        firstNameField.setText(profileDataClass.getFirstName());

        lastNameField.setText(profileDataClass.getLastName());
        imageProgressBar.setVisibility(View.VISIBLE);
        Picasso.get().load(profileDataClass.getProfileImage()).into(profileImage, new Callback() {
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

    private void checkUserData() {
        showProgressBar();
        if (firstNameField.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter First Name", Toast.LENGTH_SHORT).show();
            hideProgressBar();
        }
        if (lastNameField.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter Last Name", Toast.LENGTH_SHORT).show();
            hideProgressBar();
        }
        if (!firstNameField.getText().toString().isEmpty() && !lastNameField.getText().toString().isEmpty()) {
            uploadProfileImageToFirebaseStorage();
        }
    }

    private void uploadProfileImageToFirebaseStorage() {
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

                            }
                        }
                    });
        } else {
            UploadInformationToDatabase(profileDataClass.getProfileImage());
        }
    }

    private void UploadInformationToDatabase(String downloadUri) {

        DocumentReference userRef = firestore.collection("Users").document(mAuth.getCurrentUser().getUid());
        ProfileDataClass userProfileClass = new ProfileDataClass();
        userProfileClass.setProfileImage(downloadUri);
        userProfileClass.setFirstName(firstNameField.getText().toString());
        userProfileClass.setLastName(lastNameField.getText().toString());
        userProfileClass.setPhoneNumber(profileDataClass.getPhoneNumber());

        userRef.set(userProfileClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: profile is saved in firestore");
                    onBackPressed();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressBar();
                        }
                    }, 500);
                } else {
                    Log.d(TAG, "onComplete: unable to save profile in firestore");
                    hideProgressBar();
                }
            }
        });
    }

    private void sendUserToProfile() {
        Intent i = new Intent(EditProfileActivity.this, EditProfileActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        saveButton.setText("");
        saveButton.setEnabled(false);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        saveButton.setText("Save");
        saveButton.setEnabled(true);
    }
}