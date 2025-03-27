package com.example.gmh_app.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gmh_app.R;

public class EndofPart2Activity extends AppCompatActivity {

    private static final String PREFS_NAME = "VideoCompletionPrefs";
    private static final String KEY_INFLOWS_COMPLETED = "inflows";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_endof_part2);

        // Initialize the Continue Button
        Button btnContinue = findViewById(R.id.btnContinue);

        // Set click listener for the Continue button
        btnContinue.setOnClickListener(view -> {
            // Save completion status in SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_INFLOWS_COMPLETED, true); // Mark Inflows as completed
            editor.apply();

            // Navigate back to TopicsActivity
            Intent intent = new Intent(EndofPart2Activity.this, TopicsActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
