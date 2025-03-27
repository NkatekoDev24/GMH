package com.example.gmh_app.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gmh_app.R;

public class GmhBonusActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "VideoCompletionPrefs";
    private static final String KEY_OUTFLOWS_COMPLETED = "profit";
    private Button btnComplete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_gmh_bonus);

        btnComplete = findViewById(R.id.btn_complete);

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Save completion status in SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KEY_OUTFLOWS_COMPLETED, true); // Mark Outflows as completed
                editor.apply();

                Intent Home = new Intent(GmhBonusActivity.this, TopicsActivity.class);
                startActivity(Home);
            }
        });
    }
}