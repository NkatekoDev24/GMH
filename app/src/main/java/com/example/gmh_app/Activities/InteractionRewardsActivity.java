package com.example.gmh_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gmh_app.R;

public class InteractionRewardsActivity extends AppCompatActivity {

    private Button startVideos;
    private TextView tvCombinedMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_interaction_rewards);

         startVideos = findViewById(R.id.startVideosButton);

        TextView bonusTextView = findViewById(R.id.bonusTextView);
        bonusTextView.setText(Html.fromHtml(
                "BONUS OPTION! If you collect all four GMH Shields, you will also be rewarded with a <br>" +
                        "Gold GMH Completion Certificate!"
        ));

        startVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InteractionRewardsActivity.this, TopicsActivity.class);
                startActivity(intent);
            }
        });
    }
}