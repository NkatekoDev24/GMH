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

public class BeforeVideo10Activity extends AppCompatActivity {

    Button btnNext;
    TextView tvCombinedToc, titleTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_before_video10);

        btnNext = findViewById(R.id.nextButton);
        tvCombinedToc = findViewById(R.id.tvCombinedToc);
        titleTextView = findViewById(R.id.titleTextView);

        tvCombinedToc.setText(Html.fromHtml(
                "<b>Part 3. Counting and Recording Money OUTFLOWS (EXPENSES)</b><br>" +
                        "Video 9: Correctly counting and recording Money Outflows: Variable costs versus Fixed costs.<br>" +
                        "<span style='color:#00ff00;'><b><u>Video 10: Correctly counting and recording stock purchases and other variable costs.</b></u></span><br>" +
                        "Video 11: Fixed costs / Monthly basic costs / Overhead costs."
        ));

        titleTextView.setText(Html.fromHtml("<u>VIDEO 10</u>"));

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        navigateToTopicsActivity();
//    }
//
//    private void navigateToTopicsActivity() {
//        Intent intent = new Intent(this, TopicsActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        finish();
//    }
}