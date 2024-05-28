package com.serviceapps.transport.verification;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.serviceapps.transport.R;

public class VerifyNumber extends AppCompatActivity {
    private static final String TAG = "VerifyNumber";

    //widgets
    private MaterialButton sendOTPButton;
    private ProgressBar progressBar;
    private TextInputEditText phoneNumberField;

    //variables
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_number);

        initFields();

        sendOTPButton.setOnClickListener(v -> checkNumber());

        ButtonChangeState();
    }

    private void initFields() {
        sendOTPButton = findViewById(R.id.next_button);
        progressBar = findViewById(R.id.progress_bar);
        phoneNumberField = findViewById(R.id.phone_number_field);
        sendOTPButton.setEnabled(false);
        sendOTPButton.getBackground().setAlpha(100);
    }

    private void checkNumber() {
        phoneNumber = phoneNumberField.getText().toString().trim();
        showProgressDialog();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
        } else {
            sendUserToVerifyOTP(phoneNumber);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgressDialog();
            }
        }, 100);

    }

    private void ButtonChangeState() {
        phoneNumberField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() < 10) {
                    sendOTPButton.setEnabled(false);
                    sendOTPButton.getBackground().setAlpha(100);
                } else {
                    sendOTPButton.setEnabled(true);
                    sendOTPButton.getBackground().setAlpha(255);
                }
            }
        });
    }

    private void showProgressDialog() {
        progressBar.setVisibility(View.VISIBLE);
        sendOTPButton.setText("");
        sendOTPButton.setEnabled(false);
    }

    private void hideProgressDialog() {
        progressBar.setVisibility(View.GONE);
        sendOTPButton.setText("Send OTP");
        sendOTPButton.setEnabled(true);
    }

    private void sendUserToVerifyOTP(String phoneNumber) {
        Intent i = new Intent(VerifyNumber.this, VerifyOTP.class);
        i.putExtra("phoneNumber", "+91"+phoneNumber);
        startActivity(i);
    }
}