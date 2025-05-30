package com.example.gmh_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gmh_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MyCertificateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_certificate);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                startActivity(new Intent(this, TopicsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.navigation_certificates) {
                return true; // current activity
            } else if (id == R.id.navigation_credits) {
                startActivity(new Intent(this, CreditsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });

        // Set selected item to certificates, since we're in MyCertificateActivity
        bottomNavigationView.setSelectedItemId(R.id.navigation_certificates);
    }
}
