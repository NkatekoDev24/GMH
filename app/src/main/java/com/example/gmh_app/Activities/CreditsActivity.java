package com.example.gmh_app.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.graphics.Typeface;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gmh_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CreditsActivity extends AppCompatActivity {

    private LinearLayout creditsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_credits);

        creditsLayout = findViewById(R.id.creditsLayout);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Hide the certificates tab
        bottomNavigationView.getMenu().findItem(R.id.navigation_certificates).setVisible(false);

        // Set the selected item to credits
        bottomNavigationView.setSelectedItemId(R.id.navigation_credits);

        // Navigation handling
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                startActivity(new Intent(CreditsActivity.this, TopicsActivity.class));
                overridePendingTransition(0, 0);
                return true;

            } else if (id == R.id.navigation_credits) {
                return true;
            }

            return false;
        });


        bottomNavigationView.setSelectedItemId(R.id.navigation_credits);

        addText("GMH APP CREDITS/ACKNOWLEDGEMENTS", 20, true);

        addText("This project was made possible through the collaborative efforts of multiple teams, each playing a critical role in its development and execution. We extend our deepest appreciation to:", 16, false);

        addText("App design & development", 18, true);
        addText("Interdisciplinary Centre for Digital Futures (ICDF), University of Free State", 16, false);

        addText("Development planning, Front & back-end development, UI/UX Design, Database management and Testing", 16, false);
        addText("Nkateko Nkuna", 16, true);

        addText("Front & back-end development, UI/UX Design and Testing", 16, false);
        addText("Nicholas Coetzee", 16, true);

        addText("Project management, Development planning, Design and Testing", 16, false);
        addText("Herkulaas M.V.E Combrink", 16, true);

        addText("Testing and Quality Assurance", 16, false);
        addText("Priscilla Keche, Lurgasho H. Minnie, Ayandeji Ayantokun", 16, true);

        addText("Conceptualisation and script", 18, true);
        addText("Frederick C.V.N. Fourie", 16, true);

        addText("Gamification design and badges", 18, true);
        addText("Frederick C.V.N. Fourie, Annelize Booysen-Wolthers", 16, true);

        addText("\u00A9 This app in its entirety is protected by the South African Copyright Act 98 of 1978 and other relevant South African and international legislation.", 14, false);
    }

    // Check flag from SharedPreferences
    private boolean areAllVideosWatched() {
        SharedPreferences prefs = getSharedPreferences("VideoPrefs", MODE_PRIVATE);
        return prefs.getBoolean("all_videos_watched", false);
    }

    // Show dialog box if videos are not completed
    private void showLockedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Locked")
                .setMessage("Complete all videos to unlock the Certificates section.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void addText(String text, float size, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(size);
        tv.setPadding(0, 16, 0, 0);
        tv.setLineSpacing(1.2f, 1.2f);
        if (bold) {
            tv.setTypeface(null, Typeface.BOLD);
        }
        creditsLayout.addView(tv);
    }
}
