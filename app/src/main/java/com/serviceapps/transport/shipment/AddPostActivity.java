package com.serviceapps.transport.shipment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.moos.library.HorizontalProgressView;
import com.serviceapps.transport.R;
import com.serviceapps.transport.adapters.ViewPagerAdapter;
import com.serviceapps.transport.utils.CustomViewPager;
import com.serviceapps.transport.utils.DataValidationCallback;

public class AddPostActivity extends AppCompatActivity {
    private static final String TAG = "AddPostActivity";

    //widgets
    private ImageButton backButton;
    private CustomViewPager viewPager;
    private HorizontalProgressView progressBar;
    private TextView nextButton;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        initFields();

        setUpViewPager();
        backButton.setOnClickListener(v -> transitViewPagerToBack());
        nextButton.setOnClickListener(v -> checkDataFromFragment());


        progressBar.setProgress(progressBar.getProgress() + 25);


    }

    private void setUpViewPager() {

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), 1);
        adapter.addFragment(new ShipmentDetailsFragment(), "Shipment Details");
       // adapter.addFragment(new ShipmentStateInfoFragment(), "Shipment State Info");
        adapter.addFragment(new ShipmentAddressFragment(), "Shipment Address");
        adapter.addFragment(new ShipmentPriceFragment(), "Shipment Price");
        adapter.addFragment(new ReceiverDetailsFragment(), "Receiver Info");
        adapter.addFragment(new PostFinalShipmentFragment(),"Finalize Details");
        viewPager.setAdapter(adapter);
        viewPager.setSwipeable(false);

    }


    private void initFields() {
        backButton = findViewById(R.id.back);
        viewPager = findViewById(R.id.view_pager);
        progressBar = findViewById(R.id.progress_bar);
        nextButton = findViewById(R.id.next_button);
    }

    private void checkDataFromFragment() {
        int position = viewPager.getCurrentItem();
        Fragment fragment = adapter.getFragment(position);
        if (fragment != null) {
            switch (position) {
                case 0:
                    ((ShipmentDetailsFragment) fragment).checkAllFields(new DataValidationCallback() {
                        @Override
                        public void dataGetComplete(Boolean i) {
                            if (i) {
                                transitViewPagerToFront();
                            } else {
                                //do nothing
                            }
                        }
                    });
                    break;
                case 1:
                    ((ShipmentAddressFragment) fragment).checkAllFields(new DataValidationCallback() {
                        @Override
                        public void dataGetComplete(Boolean i) {
                            if (i) {
                                transitViewPagerToFront();
                            } else {
                                //do nothing
                            }
                        }
                    });
                    break;
                case 2:
                    transitViewPagerToFront();
                    break;
                case 3:
                    ((ReceiverDetailsFragment) fragment).checkAllFields(new DataValidationCallback() {
                        @Override
                        public void dataGetComplete(Boolean i) {
                            transitViewPagerToFront();
                        }
                    });
                    break;
            }
        }

    }

    public void transitViewPagerToFront() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
        if (progressBar.getProgress() != 100.0) {
            progressBar.setProgress(progressBar.getProgress() + 25);
        }
        nextButtonState();
    }

    private void transitViewPagerToBack() {
        if (viewPager.getCurrentItem() == 0) {
            onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
            if (progressBar.getProgress() != 25.0) {
                progressBar.setProgress(progressBar.getProgress() - 25);
            }

        }
        nextButtonState();
    }

    private void nextButtonState() {
        int position = viewPager.getCurrentItem();
        switch (position) {
            case 0:
                nextButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                if (progressBar.getProgress() != 25.0) {
                    progressBar.setProgress(25);
                }
                break;
            case 1:
                hideSoftKeyboard();
                progressBar.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);
                if (progressBar.getProgress() != 50.0) {
                    progressBar.setProgress(50);
                }
                break;
            case 3:
                hideSoftKeyboard();
                progressBar.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);
                if (progressBar.getProgress() != 100.0) {
                    progressBar.setProgress(100);
                }
                break;
            case 2:
                progressBar.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.GONE);
                if (progressBar.getProgress() != 75.0) {
                    progressBar.setProgress(75);
                }
                break;
            case 4:
                hideSoftKeyboard();
                progressBar.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.GONE);
                break;

        }
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft keyboard
     */
    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

}