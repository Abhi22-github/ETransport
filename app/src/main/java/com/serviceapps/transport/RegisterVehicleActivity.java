package com.serviceapps.transport;

import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

import com.badoualy.stepperindicator.StepperIndicator;
import com.google.android.material.button.MaterialButton;
import com.serviceapps.transport.adapters.ViewPagerAdapter;
import com.serviceapps.transport.documents.DLFragment;
import com.serviceapps.transport.documents.IDFragment;
import com.serviceapps.transport.documents.RCFragment;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

public class RegisterVehicleActivity extends AppCompatActivity {
    private static final String TAG = "RegisterVehicleActivity";

    //widgets
    private ViewPager viewPager;
    private ImageButton backButton;
    private MaterialButton saveButton,prevButton;
    private WormDotsIndicator dotsIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_vehicle);
        initFields();

        backButton.setOnClickListener(v -> onBackPressed());

        setUpViewPager();

        disableSwipeOnViewPager();

        saveButton.setOnClickListener(v -> transitViewPagerToFront());

        prevButton.setOnClickListener(v -> transitViewPagerToBack());
    }

    private void initFields() {

        viewPager = findViewById(R.id.view_pager);
        backButton = findViewById(R.id.back);
        saveButton = findViewById(R.id.save_button);
        prevButton = findViewById(R.id.back_button);
        dotsIndicator = findViewById(R.id.dots_indicator);
    }

    private void setUpViewPager() {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), 1);
        adapter.addFragment(new IDFragment(), "ID");
        adapter.addFragment(new RCFragment(), "RC");
        adapter.addFragment(new DLFragment(), "DL");
        viewPager.setAdapter(adapter);
        dotsIndicator.setViewPager(viewPager);
    }

    private void disableSwipeOnViewPager() {
        View touchView = findViewById(R.id.view_pager);
        touchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private void transitViewPagerToFront() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
    }

    private void transitViewPagerToBack(){
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
    }
}