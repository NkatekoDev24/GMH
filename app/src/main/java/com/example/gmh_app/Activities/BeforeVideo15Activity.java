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

public class BeforeVideo15Activity extends AppCompatActivity {

    private TextView congratulationsTextView, part4VideoListTextView, title, income;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_before_video15);

        congratulationsTextView = findViewById(R.id.congratulationsTextView);
        part4VideoListTextView = findViewById(R.id.part4VideoListTextView);
        title = findViewById(R.id.titleTextView);
        income = findViewById(R.id.incomeStatement);
        submitButton = findViewById(R.id.submitButton);

        // Set main congratulatory text
//        congratulationsTextView.setText(Html.fromHtml(
//                "👏👏👏 <b>Congratulations on getting to Video 15, the final video in this four-part series of GMH Videos. </b><br><br>" +
//                        "<b>You can feel very good about completing the series.</b><br><br>" +
//                        "You’ll find this a very satisfying video. As you will see in the complete weekly example at the end of the video, " +
//                        "this is where everything you have learned in these videos flow together. Here they culminate in a complete statement of your revenue, expenses and your profits.<br>" +
//                        "👉 This is also called an <b>Income Statement.</b>"
//        ));

        title.setText(Html.fromHtml("<u>VIDEO 15</u>"));

        // Set Part 4 boxed video list text
        part4VideoListTextView.setText(Html.fromHtml(
                "<b>Part 4. Counting and Recording PROFIT; the risk of customer credit</b><br>" +
                        "Video 12: Calculating Profit correctly.<br>" +
                        "Video 13: Must I use gross profit or net profit for management purposes?<br>" +
                        "Video 14: The risk of customer credit – another hazard.<br>" +
                        "<span style='color:#00ff00;'><b><u>Video 15: Revenue, costs & profits – a complete weekly example with numbers</b></u></span>"
        ));

        income.setText(Html.fromHtml("\uD83D\uDC49This is also called an <b>Income Statement.</b>"));

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
