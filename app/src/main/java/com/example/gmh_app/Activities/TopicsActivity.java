package com.example.gmh_app.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.gmh_app.Models.VideoModel;
import com.example.gmh_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class TopicsActivity extends AppCompatActivity {

    private CardView cardOrientation, cardInflows, cardOutflows, cardProfit;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "VideoCompletionPrefs";
    private static final String KEY_ORIENTATION_COMPLETED = "orientation";
    private static final String KEY_INFLOWS_COMPLETED = "inflows";
    private static final String KEY_OUTFLOWS_COMPLETED = "outflows";
    private static final String KEY_PROFIT_COMPLETED = "profit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_topics);

        // FAB + Drawer setup
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        FloatingActionButton fabMenu = findViewById(R.id.fab_menu);
        NavigationView navView = findViewById(R.id.nav_view);

        fabMenu.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        // Set navigation item listener
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // Already in TopicsActivity (Home)
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }  else if (id == R.id.nav_help) {
                startActivity(new Intent(this, HelpActivity.class));
            } else if (id == R.id.nav_logout) {
                showLogoutDialog();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Setup CardView references
        cardOrientation = findViewById(R.id.card_orientation);
        cardInflows = findViewById(R.id.card_inflows);
        cardOutflows = findViewById(R.id.card_outflows);
        cardProfit = findViewById(R.id.card_profit);

        // Setup click listeners for cards
        cardOrientation.setOnClickListener(view -> openVideoActivity(getOrientationVideos(), "orientation"));
        cardInflows.setOnClickListener(view -> openVideoActivity(getInflowsVideos(), "inflows"));
        cardOutflows.setOnClickListener(view -> openVideoActivity(getOutflowsVideos(), "outflows"));
        cardProfit.setOnClickListener(view -> openVideoActivity(getProfitVideos(), "profit"));

        // Update UI based on completion status
        updateCardVisibility();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                return true;

            } else if (id == R.id.navigation_certificates) {
                if (areAllVideosWatched()) {
                    startActivity(new Intent(TopicsActivity.this, MyCertificateActivity.class));
                    overridePendingTransition(0, 0);
                } else {
                    showLockedDialog();
                }
                return true;
            } else if (id == R.id.navigation_credits) {
                startActivity(new Intent(TopicsActivity.this, CreditsActivity.class));
                overridePendingTransition(0,0);
            }
            return false;
        });


        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCardVisibility();
    }

    private void updateCardVisibility() {
        boolean isOrientationComplete = sharedPreferences.getBoolean(KEY_ORIENTATION_COMPLETED, true);
        boolean isInflowsComplete = sharedPreferences.getBoolean(KEY_INFLOWS_COMPLETED, true);
        boolean isOutflowsComplete = sharedPreferences.getBoolean(KEY_OUTFLOWS_COMPLETED, true);
        boolean isProfitComplete = sharedPreferences.getBoolean(KEY_PROFIT_COMPLETED, true);

        // Orientation is always enabled
        cardOrientation.setEnabled(true);
        cardOrientation.setAlpha(1.0f);

        // Lock or unlock the rest based on completion status
        updateCardState(cardInflows, isOrientationComplete);
        updateCardState(cardOutflows, isInflowsComplete);
        updateCardState(cardProfit, isOutflowsComplete);

    }

    // Helper method to set card state
    private void updateCardState(CardView card, boolean isEnabled) {
        card.setEnabled(isEnabled);
        card.setAlpha(isEnabled ? 1.0f : 0.5f); // Grey out if locked
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

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Exit", (dialog, which) -> {
                    // Clear shared prefs or session
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    finishAffinity();         // Close all activities
                    System.exit(0);           // Terminate the app
                })
                .setNegativeButton("Cancel", null)
                .show();
    }



    private void openVideoActivity(ArrayList<VideoModel> videoList, String sectionKey) {
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putParcelableArrayListExtra("video_list", videoList);
        intent.putExtra("section_key", sectionKey);
        startActivity(intent);
    }

    // Fetch videos for the "Orientation" card
    private ArrayList<VideoModel> getOrientationVideos() {
        ArrayList<VideoModel> videos = new ArrayList<>();
        videos.add(new VideoModel("Before Video 1: Intro & Overview of Part 1", true, 1));
        videos.add(new VideoModel("WATCH VIDEO 1: Introduction: Why good money habits?", "android.resource://" + getPackageName() + "/" + R.raw.video1));
        videos.add(new VideoModel("After Video 1: Feedback & Comments", true, 2));
        videos.add(new VideoModel("Before Video 2: User registration & Overview", true, 3));
        videos.add(new VideoModel("WATCH VIDEO 2: Making a profit – and not a loss", "android.resource://" + getPackageName() + "/" + R.raw.video2));
        videos.add(new VideoModel("After Video 2: Feedback & Comments", true, 4));
        videos.add(new VideoModel("Before Video 3: Your business features", true, 5));
        videos.add(new VideoModel("WATCH VIDEO 3: Profit in good & bad weeks: Good decisions & avoiding losses", "android.resource://" + getPackageName() + "/" + R.raw.video3));
        videos.add(new VideoModel("After Video 3: Feedback & Comments", true, 6));
        videos.add(new VideoModel("Before Video 4: Your business practices", true, 7));
        videos.add(new VideoModel("WATCH VIDEO 4: The Separation Rule – Most important for hazard avoidance", "android.resource://" + getPackageName() + "/" + R.raw.video4));
        videos.add(new VideoModel("After Video 4: Feedback & Comments", true, 8));
        videos.add(new VideoModel("End of part 1", true, 9));
        return videos;
    }

    // Fetch videos for the "Inflows" card
    private ArrayList<VideoModel> getInflowsVideos() {
        ArrayList<VideoModel> videos = new ArrayList<>();
        videos.add(new VideoModel("Before Video 5: Intro and Overview of Part 2 & Your money inflow practices", true, 10));
        videos.add(new VideoModel("WATCH VIDEO 5: Daily steps to count money inflows correctly – and avoid financial hazards", "android.resource://" + getPackageName() + "/" + R.raw.video5));
        videos.add(new VideoModel("After Video 5: Feedback & Comments", true, 11));
        videos.add(new VideoModel("Before Video 6: More money-inflow practices", true, 12));
        videos.add(new VideoModel("WATCH VIDEO 6: More hazards when counting daily money inflows", "android.resource://" + getPackageName() + "/" + R.raw.video6));
        videos.add(new VideoModel("After Video 6: Feedback & Comments", true, 13));
        videos.add(new VideoModel("Before Video 7: Introduction", true, 14));
        videos.add(new VideoModel("WATCH VIDEO 7: Correcting daily calculations of money inflows for purchases and hazards", "android.resource://" + getPackageName() + "/" + R.raw.video7));
        videos.add(new VideoModel("After Video 7: Feedback & Comments", true, 15));
        videos.add(new VideoModel("Before Video 8: Introduction on Transactions", true, 16));
        videos.add(new VideoModel("WATCH VIDEO 8: Using transaction values to calculate money inflows for a day and week", "android.resource://" + getPackageName() + "/" + R.raw.video8));
        videos.add(new VideoModel("After Video 8: Feedback & Comments. Assessing Part 2.", true, 17));
        videos.add(new VideoModel("Overall part 2", true, 18));
        videos.add(new VideoModel("End of part 2", true, 19));
        return videos;
    }

    // Fetch videos for the "Outflows" card
    private ArrayList<VideoModel> getOutflowsVideos() {
        ArrayList<VideoModel> videos = new ArrayList<>();
        videos.add(new VideoModel("Before Video 9: Intro & Overview of Part 3; more about your business", true, 20));
        videos.add(new VideoModel("WATCH VIDEO 9: Correctly counting and recording money outflows: Variable Costs versus Fixed Costs", "android.resource://" + getPackageName() + "/" + R.raw.video9));
        videos.add(new VideoModel("After Video 9: Feedback & Comments", true, 21));
        videos.add(new VideoModel("Before Video 10: Key Points on Stock Purchases", true, 22));
        videos.add(new VideoModel("WATCH VIDEO 10: Money outflows: Correctly counting and recording stock purchases and other variable costs", "android.resource://" + getPackageName() + "/" + R.raw.video10));
        videos.add(new VideoModel("After Video 10: Feedback & Comments", true, 23));
        videos.add(new VideoModel("Before Video 11: Introduction & Your business premises", true, 24));
        videos.add(new VideoModel("WATCH VIDEO 11: More money outflows: Fixed Costs / Monthly Basic Costs (Overhead Costs)", "android.resource://" + getPackageName() + "/" + R.raw.video11));
        videos.add(new VideoModel("After Video 11: Feedback & Comments. Assessing Part 3.", true, 25));
        videos.add(new VideoModel("Overall Part 3", true, 26));
        videos.add(new VideoModel("End of part 3", true, 27));
        return videos;
    }

    // Fetch videos for the "Profit" card
    private ArrayList<VideoModel> getProfitVideos() {
        ArrayList<VideoModel> videos = new ArrayList<>();
        videos.add(new VideoModel("Before Video 12: Introduction and Overview of Part 4.", true, 28));
        videos.add(new VideoModel("WATCH VIDEO 12: Calculating Profit correctly", "android.resource://" + getPackageName() + "/" + R.raw.video12));
        videos.add(new VideoModel("After Video 12: Feedback & Comments: your profits", true, 29));
        videos.add(new VideoModel("Before Video 13: Introduction and Your business finances.", true, 30));
        videos.add(new VideoModel("WATCH VIDEO 13: Must I use Gross profit or Net profit for management purposes?", "android.resource://" + getPackageName() + "/" + R.raw.video13));
        videos.add(new VideoModel("After Video 13: Feedback & Comments", true, 31));
        videos.add(new VideoModel("Before Video 14: Introduction and Your customer credit practices.", true, 32));
        videos.add(new VideoModel("WATCH VIDEO 14: The risk of consumer credit - another hazard", "android.resource://" + getPackageName() + "/" + R.raw.video14));
        videos.add(new VideoModel("After Video 14: Feedback & Comments: customer credit", true, 33));
        videos.add(new VideoModel("Before Video 15: Introduction and Income Statements.", true, 34));
        videos.add(new VideoModel("WATCH VIDEO 15: Revenue, costs & profits – a complete weekly example with numbers.", "android.resource://" + getPackageName() + "/" + R.raw.video15));
        videos.add(new VideoModel("After Video 15: Feedback & Comments: your Income Statement. \n" +
                "                            Assessing the entire GMH Video Series.", true, 35));
        videos.add(new VideoModel("Overall Part 4", true, 36));
        videos.add(new VideoModel("End of Part 4", true, 37));
        videos.add(new VideoModel("Certificate", true, 38));
        return videos;
    }
}