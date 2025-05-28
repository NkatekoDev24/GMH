package com.example.gmh_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.gmh_app.R;

public class CorrectMoneyInflows extends AppCompatActivity {

    Button btnNext;
    private TextView tvCombinedToc, titleTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_correct_money_inflows);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        btnNext = findViewById(R.id.nextButton);
        tvCombinedToc = findViewById(R.id.tvCombinedToc);
        titleTextView = findViewById(R.id.titleTextView);

        tvCombinedToc.setText(Html.fromHtml(
                "<b>Part 2. Counting and Recording Money INFLOWS</b><br>" +
                        "Video 5: Daily steps to count Money Inflows correctly – and avoid financial hazards.<br>" +
                        "Video 6: More hazards when counting daily money inflows.<br>" +
                        "<span style='color:#00ff00;'><b><u>Video 7: Correcting daily calculations of money inflows for purchases and hazards.</b></u></span><br>" +
                        "Video 8: Using transaction values to calculate money inflows for a day and week."
        ));

        titleTextView.setText(Html.fromHtml("<u>VIDEO 7</u>"));

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        } else if (id == R.id.action_home) {
            startActivity(new Intent(CorrectMoneyInflows.this, TopicsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(CorrectMoneyInflows.this, HelpActivity.class));
            overridePendingTransition(0,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
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