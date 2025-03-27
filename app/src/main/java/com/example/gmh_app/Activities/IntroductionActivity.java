package com.example.gmh_app.Activities;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gmh_app.R;

public class IntroductionActivity extends AppCompatActivity {

    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_introduction);

        // Watch Next Button setup
        btnNext = findViewById(R.id.btnWatchNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Directly finish the activity and navigate to TopicsActivity
                setResult(RESULT_OK);
                finish();
            }
        });

        // TextView for Combined Message setup with HTML formatting
//        TextView tvCombinedMessage = findViewById(R.id.tvCombinedMessage);
        TextView tvCombineToc = findViewById(R.id.tvCombinedToc);
        TextView tvParts = findViewById(R.id.tvPart);
        TextView video1 = findViewById(R.id.video1);


//        String formattedText = " " + "<b>You will be tempted to watch all the videos in one go, because they are really good!</b><br><br>" +
//                "<b>However</b>, <b>we RECOMMEND that you space the four parts (15 videos) over at least 4 weeks.</b><br><br>" +
//                "This will give you enough time to watch some videos again. Also, you can try out, in your own business, new things that you are learning.";
//        tvCombinedMessage.setText(Html.fromHtml(formattedText));

        tvParts.setText(Html.fromHtml("<u>PART 1 START PAGE</u>"));

        tvCombineToc.setText(Html.fromHtml(
                " " +
                "<b>Part 1. BASICS: Why Good Money Habits – and the Separation rule</b><br>" +
                "<span style='color:#00ff00;'><b><u>Video 1: Introduction – Why good money habits?</u></b></span><br>" +
                        "Video 2: Making a profit – and not a loss.<br>" +
                        "Video 3: Profit in good & bad weeks: Good decisions & avoiding losses.<br>" +
                        "Video 4: The Separation Rule – Most important for hazard avoidance."
        ));

        video1.setText(Html.fromHtml("<u>VIDEO 1</u>"));

    }


//    @Override
//    public void onBackPressed() {
//        // Navigate back to VideoActivity without creating a new instance
//        super.onBackPressed();
//        setResult(RESULT_CANCELED); // Optional: Indicate cancellation
//        finish(); // Finish the activity and return to the previous one in the stack
//    }
}
