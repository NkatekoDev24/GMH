package com.example.gmh_app.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.gmh_app.Models.VideoModel;
import com.example.gmh_app.R;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCardVisibility();
    }

    private void updateCardVisibility() {
        boolean isOrientationComplete = sharedPreferences.getBoolean(KEY_ORIENTATION_COMPLETED, false);
        boolean isInflowsComplete = sharedPreferences.getBoolean(KEY_INFLOWS_COMPLETED, false);
        boolean isOutflowsComplete = sharedPreferences.getBoolean(KEY_OUTFLOWS_COMPLETED, false);
        boolean isProfitComplete = sharedPreferences.getBoolean(KEY_PROFIT_COMPLETED, false);

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
        videos.add(new VideoModel("WATCH VIDEO 1", "android.resource://" + getPackageName() + "/" + R.raw.video1));
        videos.add(new VideoModel("After Video 1: Feedback & Comments", true, 2));
        videos.add(new VideoModel("Before Video 2: User registration & Overview", true, 3));
        videos.add(new VideoModel("WATCH VIDEO 2", "android.resource://" + getPackageName() + "/" + R.raw.video2));
        videos.add(new VideoModel("After Video 2: Feedback & Comments", true, 4));
        videos.add(new VideoModel("Before Video 3: Your business features", true, 5));
        videos.add(new VideoModel("WATCH VIDEO 3", "android.resource://" + getPackageName() + "/" + R.raw.video3));
        videos.add(new VideoModel("After Video 3: Feedback & Comments", true, 6));
        videos.add(new VideoModel("Before Video 4: Your business practices", true, 7));
        videos.add(new VideoModel("WATCH VIDEO 4", "android.resource://" + getPackageName() + "/" + R.raw.video4));
        videos.add(new VideoModel("After Video 4: Feedback & Comments", true, 8));
        return videos;
    }

    // Fetch videos for the "Inflows" card
    private ArrayList<VideoModel> getInflowsVideos() {
        ArrayList<VideoModel> videos = new ArrayList<>();
        videos.add(new VideoModel("Before Video 5: Intro and Overview of Part 2 & Your money inflow practices", true, 9));
        videos.add(new VideoModel("WATCH VIDEO 5", "android.resource://" + getPackageName() + "/" + R.raw.video5));
        videos.add(new VideoModel("After Video 5: Feedback & Comments", true, 10));
        videos.add(new VideoModel("Before Video 6: More money-inflow practices", true, 11));
        videos.add(new VideoModel("WATCH VIDEO 6", "android.resource://" + getPackageName() + "/" + R.raw.video6));
        videos.add(new VideoModel("After Video 6: Feedback & Comments", true, 12));
        videos.add(new VideoModel("Before Video 7: Introduction", true, 13));
        videos.add(new VideoModel("WATCH VIDEO 7", "android.resource://" + getPackageName() + "/" + R.raw.video7));
        videos.add(new VideoModel("After Video 7: Feedback & Comments", true, 14));
        videos.add(new VideoModel("Before Video 8: Introduction on Transactions", true, 15));
        videos.add(new VideoModel("WATCH VIDEO 8", "android.resource://" + getPackageName() + "/" + R.raw.video8));
        videos.add(new VideoModel("After Video 8: Feedback & Comments. Assessing Part 2.", true, 16));
        return videos;
    }

    // Fetch videos for the "Outflows" card
    private ArrayList<VideoModel> getOutflowsVideos() {
        ArrayList<VideoModel> videos = new ArrayList<>();
        videos.add(new VideoModel("Before Video 9: Intro & Overview of Part 3; more about your business", true, 17));
        videos.add(new VideoModel("WATCH VIDEO 9", "android.resource://" + getPackageName() + "/" + R.raw.video9));
        videos.add(new VideoModel("After Video 9: Feedback & Comments", true, 18));
        videos.add(new VideoModel("Before Video 10: Key Points on Stock Purchases", true, 19));
        videos.add(new VideoModel("WATCH VIDEO 10", "android.resource://" + getPackageName() + "/" + R.raw.video10));
        videos.add(new VideoModel("After Video 10: Feedback & Comments", true, 20));
        videos.add(new VideoModel("Before Video 11: Introduction & Your business premises", true, 21));
        videos.add(new VideoModel("WATCH VIDEO 11", "android.resource://" + getPackageName() + "/" + R.raw.video11));
        videos.add(new VideoModel("After Video 11: Feedback & Comments. Assessing Part 3.", true, 22));
        return videos;
    }

    // Fetch videos for the "Profit" card
    private ArrayList<VideoModel> getProfitVideos() {
        ArrayList<VideoModel> videos = new ArrayList<>();
        videos.add(new VideoModel("Before Video 12: Introduction and Overview of Part 4.", true, 23));
        videos.add(new VideoModel("WATCH VIDEO 12", "android.resource://" + getPackageName() + "/" + R.raw.video12));
        videos.add(new VideoModel("After Video 12: Feedback & Comments: your profits", true, 24));
        videos.add(new VideoModel("Before Video 13: Introduction and Your business finances.", true, 25));
        videos.add(new VideoModel("WATCH VIDEO 13", "android.resource://" + getPackageName() + "/" + R.raw.video13));
        videos.add(new VideoModel("After Video 13: Feedback & Comments", true, 26));
        videos.add(new VideoModel("Before Video 14: Introduction and Your customer credit practices.", true, 27));
        videos.add(new VideoModel("WATCH VIDEO 14", "android.resource://" + getPackageName() + "/" + R.raw.video14));
        videos.add(new VideoModel("After Video 14: Feedback & Comments: customer credit", true, 28));
        videos.add(new VideoModel("Before Video 15: Introduction and Income Statements.", true, 29));
        videos.add(new VideoModel("WATCH VIDEO 15", "android.resource://" + getPackageName() + "/" + R.raw.video15));
        videos.add(new VideoModel("After Video 15: Feedback & Comments: your Income Statement. \n" +
                "                            Assessing the entire GMH Video Series.", true, 30));
        return videos;
    }
}