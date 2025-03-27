package com.example.gmh_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gmh_app.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_welcome);

        // Set the logo image
        ImageView logoImage = findViewById(R.id.logoImage);
        logoImage.setImageResource(R.drawable.logo);

        // Set the texts for each TextView
        TextView title = findViewById(R.id.title);
        TextView intro = findViewById(R.id.intro);
        TextView details = findViewById(R.id.details);
        TextView circumstances = findViewById(R.id.circumstances);
        TextView intendedUsers = findViewById(R.id.intendedUsers);
        TextView plainLanguage = findViewById(R.id.plainLanguage); // This is the new text view for the "Approach:"
        TextView videoGroupsTitle = findViewById(R.id.videoGroupsTitle);
        TextView videoGroups = findViewById(R.id.videoGroups);
        TextView conclusion = findViewById(R.id.conclusion);

        Button btnNext = findViewById(R.id.nextButton);

        // Set the content
        title.setText("Welcome to the Good Money Habits (GMH) Videos");
        intro.setText(Html.fromHtml("<b>The aim of the GMH Videos is to help you make more money and grow your business by adopting</b> good habits in the handling and recording of money."));
        details.setText(Html.fromHtml("This covers <b>money inflows</b> (income, revenue) and <b>money outflows</b> (expenses, costs) – and the correct calculation and use of <b>profit</b>."));
        circumstances.setText(Html.fromHtml("<b>Context: The circumstances, needs, problems, and ambitions of <b>small or micro-businesses (often informal) in townships</b>"));
        intendedUsers.setText(Html.fromHtml("<b>Intended users:</b><b> Owners and managers</b> of small and micro-enterprises in townships, especially if you haven’t had any training in bookkeeping or management."));

        // Set "Approach:" in bold and the rest unbold
        plainLanguage.setText(Html.fromHtml("<b>Approach:</b> These videos are in <b>plain language</b> – no technical jargon or complex accounting."));

        // Updated to use <br> for line breaks instead of \n
        videoGroupsTitle.setText("This GMH Series has 4 groups of videos (with 3-5 minutes per video):");
        videoGroups.setText(Html.fromHtml(
                "<b>Part 1:</b> <b>BASICS:</b> Why Good Money Habits (4 videos)<br>" +
                        "<b>Part 2:</b> Counting and recording money <b>INFLOWS</b> (revenue) (4 videos)<br>" +
                        "<b>Part 3:</b> Counting and recording money <b>OUTFLOWS</b> (3 videos)<br>" +
                        "<b>Part 4:</b> Counting and recording <b>PROFIT</b> & The Risk of Customer Credit (4 videos)"
        ));

        conclusion.setText("We hope you will learn many new and useful things – and find it enjoyable.");

        // Set up button to move to the next activity
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, InteractionRewardsActivity.class);
                startActivity(intent);
            }
        });
    }
}
