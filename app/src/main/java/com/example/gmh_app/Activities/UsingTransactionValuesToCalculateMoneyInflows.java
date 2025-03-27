package com.example.gmh_app.Activities;

import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gmh_app.R;

public class UsingTransactionValuesToCalculateMoneyInflows extends AppCompatActivity {

    Button btnNext;
    TextView tvCombinedToc, titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_using_transaction_values_to_calculate_money_inflows);

        btnNext = findViewById(R.id.nextButton);
        tvCombinedToc = findViewById(R.id.tvCombinedToc);
        titleTextView = findViewById(R.id.titleTextView);

        tvCombinedToc.setText(Html.fromHtml(
                "<b>Part 2. Counting and Recording Money INFLOWS</b><br>" +
                        "Video 5: Daily steps to count Money Inflows correctly – and avoid financial hazards.<br>" +
                        "Video 6: More hazards when counting daily money inflows.<br>" +
                        "Video 7: Correcting daily calculations of money inflows for purchases and hazards.<br>" +
                        "<span style='color:#00ff00;'><b><u>Video 8: Using transaction values to calculate money inflows for a day and week.</b></u></span>"
        ));

        titleTextView.setText(Html.fromHtml("<u>VIDEO 8</u>"));

        btnNext.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
    }
}
