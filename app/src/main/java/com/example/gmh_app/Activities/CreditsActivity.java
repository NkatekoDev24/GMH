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

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_credits);

        creditsLayout = findViewById(R.id.creditsLayout);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().findItem(R.id.navigation_certificates).setVisible(false);
        bottomNavigationView.setSelectedItemId(R.id.navigation_credits);

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

        addText("GMH App Credits & Acknowledgements", 20, true);
        addText("The development of the App was made possible through the collaborative efforts of multiple teams, each playing a critical role in its development and execution. We extend our deepest appreciation to:", 14, false);

        addBoldLabelWithValue("App design and development", "Interdisciplinary Centre for Digital Futures (ICDF), University of the Free State");
        addBoldLabelWithValue("Project management", "Herkulaas M.V.E Combrinck");
        addBoldLabelWithValue("Design and testing", "Herkulaas M.V.E Combrinck; Nicholas Coetzee");
        addBoldLabelWithValue("Development planning", "Herkulaas M.V.E Combrinck; Nkateko Nkuna");
        addBoldLabelWithValue("Front- & back-end development, UI/UX design", "Nkateko Nkuna; Nicholas Coetzee");
        addBoldLabelWithValue("Database management and testing", "Nkateko Nkuna");
        addBoldLabelWithValue("Testing and quality assurance", "Priscilla Keche; Lurgasho H. Minnie; Ayandeji Ayantokun");
        addBoldLabelWithValue("App conceptualisation and script", "© Frederick C.V.N. Fourie");
        addBoldLabelWithValue("Gamification design, avatars and shields", "© Frederick C.V.N. Fourie; Annelize Booysen-Wolthers");
        addBoldLabelWithValue("Credits for the videos in the app", "See credits at the end of individual videos.");

        addText("© This app in its entirety is protected by the South African Copyright Act 98 of 1978 and other relevant South African and international legislation.", 12, true);
    }

    private boolean areAllVideosWatched() {
        SharedPreferences prefs = getSharedPreferences("VideoPrefs", MODE_PRIVATE);
        return prefs.getBoolean("all_videos_watched", false);
    }

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

    private void addBoldLabelWithValue(String label, String value) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        TextView labelView = new TextView(this);
        labelView.setText(label + ":  ");
        labelView.setTypeface(null, Typeface.BOLD);
        labelView.setTextSize(14);

        TextView valueView = new TextView(this);
        valueView.setText(value);
        valueView.setTextSize(14);

        row.addView(labelView);
        row.addView(valueView);
        row.setPadding(0, 16, 0, 0);
        creditsLayout.addView(row);
    }
}
