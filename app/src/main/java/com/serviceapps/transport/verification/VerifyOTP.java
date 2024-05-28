package com.serviceapps.transport.verification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.serviceapps.transport.MainActivity;
import com.serviceapps.transport.ProfileActivity;
import com.serviceapps.transport.R;

import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class VerifyOTP extends AppCompatActivity {
    private static final String TAG = "VerifyOTP";

    //widgets
    private OtpTextView otpTextView;
    private MaterialButton verifyButton;
    private ProgressBar progressBar;
    private TextView resendTimer;

    //local variables
    private static String phoneNumber;
    private String OTPString;
    private static CountDownTimer countDownTimer;
    private String codeFromSystem;

    //Firebase vars
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        getValues();
        initFields();

        initFirebase();

        otpListener();

        sendVerificationCodeToUser();

        resendTimer.setOnClickListener(v -> resendVerificationCode(phoneNumber, resendingToken));

    }

    private void getValues() {
        phoneNumber = getIntent().getStringExtra("phoneNumber");
    }


    private void initFields() {
        otpTextView = findViewById(R.id.otp_view);
        verifyButton = findViewById(R.id.verify_button);
        progressBar = findViewById(R.id.progress_bar);
        resendTimer = findViewById(R.id.resend_timer);
        verifyButton.setEnabled(false);
        verifyButton.getBackground().setAlpha(100);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    private void otpListener() {
        otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {
                verifyButton.setEnabled(true);
                verifyButton.getBackground().setAlpha(255);
            }
        });

    }

    private void sendVerificationCodeToUser() {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(45L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(VerifyOTP.this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(VerifyOTP.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                startTimer(45 * 1000);
            }
        });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeFromSystem = s;
                    resendingToken = forceResendingToken;
                    verifyButton.setOnClickListener(v -> verifyCredentials(s));
                }

                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        // progressBar.setVisibility(View.VISIBLE);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Toast.makeText(VerifyOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    sendUserToRegisterOTPActivity();
                }
            };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeFromSystem, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void verifyCredentials(String verifyId) {
        OTPString = otpTextView.getOTP();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifyId, OTPString);
        signInWithPhoneAuthCredential(credential);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        showProgressDialog();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((AppCompatActivity) VerifyOTP.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //for storing number in device
                            SharedPreferences preferences = getSharedPreferences("Profile", MODE_PRIVATE);
                            Editor editor = preferences.edit();
                            editor.putString("phoneNumber", phoneNumber);
                            editor.apply();

                            //verifying the user
                            verifyUserExistence();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(VerifyOTP.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                                hideProgressDialog();
                                otpListener();
                            }
                            hideProgressDialog();
                        }

                    }


                });
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
                        sendUserToMainActivity();
                    } else {
                        sendUserToProfileActivity();
                    }
                } else {
                    // progressBar.setVisibility(View.GONE);
                    Toast.makeText(VerifyOTP.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Failed with: ", task.getException());
                }
                hideProgressDialog();
            }
        });

    }

    public void resendVerificationCode(String phoneNumber,
                                       PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                VerifyOTP.this,           //a reference to an activity if this method is in a custom service
                mCallbacks,
                token);
        startTimer(45 * 1000);// resending with token got at previous call's `callbacks` method `onCodeSent`
    }


    //Start Countdown method
    private void startTimer(int noOfMinutes) {
        countDownTimer = new CountDownTimer(noOfMinutes, 1000) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                //Convert milliseconds into hour,minute and seconds
                String hms = String.format("%01d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                resendTimer.setText(hms);//set text
            }

            public void onFinish() {

                resendTimer.setText("Resend OTP");
                resendTimer.setEnabled(true);
            }
        }.start();

    }

    private void showProgressDialog() {
        progressBar.setVisibility(View.VISIBLE);
        verifyButton.setText("");
        verifyButton.setEnabled(false);
    }

    private void hideProgressDialog() {
        progressBar.setVisibility(View.GONE);
        verifyButton.setText("Send OTP");
        verifyButton.setEnabled(true);
    }

    private void sendUserToRegisterOTPActivity() {
        Intent RegisterIntent = new Intent(VerifyOTP.this, VerifyNumber.class);
        RegisterIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(RegisterIntent);
        finish();
    }

    private void sendUserToMainActivity() {
        Intent RegisterIntent = new Intent(VerifyOTP.this, MainActivity.class);
        RegisterIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(RegisterIntent);
        finish();
    }

    private void sendUserToProfileActivity() {
        Intent RegisterIntent = new Intent(VerifyOTP.this, ProfileActivity.class);
        RegisterIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(RegisterIntent);
        finish();
    }

}