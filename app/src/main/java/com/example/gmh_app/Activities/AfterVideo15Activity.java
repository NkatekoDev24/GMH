package com.example.gmh_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.gmh_app.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AfterVideo15Activity extends AppCompatActivity {

    private static final String TAG = "AfterVideo15Activity";

    private RatingBar ratingVideo, ratingClarity, ratingUsefulness;
    private RadioGroup rgIncomeStatementAbility, rgExistingIncomeStatement, rgRecentIncomeStatement, rgRealizationIncomeStatement;
    private EditText etLesson, etComments;
    private ImageView btnBack;
    private Button btnSubmit;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_after_video15);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback After Video 15");
        databaseReference.keepSynced(true);

        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        ratingVideo = findViewById(R.id.rating_video);
        ratingClarity = findViewById(R.id.rating_clarity);
        ratingUsefulness = findViewById(R.id.rating_usefulness);
        rgIncomeStatementAbility = findViewById(R.id.rg_income_statement_ability);
        rgExistingIncomeStatement = findViewById(R.id.rg_existing_income_statement);
        rgRecentIncomeStatement = findViewById(R.id.rg_recent_income_statement);
        rgRealizationIncomeStatement = findViewById(R.id.rg_realization_income_statement);
        etLesson = findViewById(R.id.et_lesson);
        etComments = findViewById(R.id.et_comments);
        btnSubmit = findViewById(R.id.btn_submit);
        btnBack = findViewById(R.id.btn_back);

        btnSubmit.setOnClickListener(v -> validateAndSubmitFeedback());
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setResult(RESULT_CANCELED);
//                finish();
//            }
//        });
    }

    private void validateAndSubmitFeedback() {
        float videoRating = ratingVideo.getRating();
        float clarityRating = ratingClarity.getRating();
        float usefulnessRating = ratingUsefulness.getRating();
        String lessonLearned = etLesson.getText().toString().trim();
        String comments = etComments.getText().toString().trim();

        int selectedIncomeStatementAbilityId = rgIncomeStatementAbility.getCheckedRadioButtonId();
        int selectedExistingIncomeStatementId = rgExistingIncomeStatement.getCheckedRadioButtonId();
        int selectedRecentIncomeStatementId = rgRecentIncomeStatement.getCheckedRadioButtonId();
        int selectedRealizationIncomeStatementId = rgRealizationIncomeStatement.getCheckedRadioButtonId();

        List<String> errors = new ArrayList<>();

        if (videoRating == 0) errors.add("Please rate the video.");
        if (clarityRating == 0) errors.add("Please rate the clarity of the information.");
        if (usefulnessRating == 0) errors.add("Please rate the usefulness of the information.");
        if (TextUtils.isEmpty(lessonLearned)) errors.add("Please write the biggest lesson you learned.");
        if (selectedIncomeStatementAbilityId == -1) errors.add("Please indicate if you can draw an income statement.");
        if (selectedExistingIncomeStatementId == -1) errors.add("Please specify if you have an existing income statement.");
        if (selectedRecentIncomeStatementId == -1) errors.add("Please confirm if you have a recent income statement.");
        if (selectedRealizationIncomeStatementId == -1) errors.add("Please state if you realize the importance of an income statement.");

        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        String incomeStatementAbility = ((RadioButton) findViewById(selectedIncomeStatementAbilityId)).getText().toString();
        String existingIncomeStatement = ((RadioButton) findViewById(selectedExistingIncomeStatementId)).getText().toString();
        String recentIncomeStatement = ((RadioButton) findViewById(selectedRecentIncomeStatementId)).getText().toString();
        String realizationIncomeStatement = ((RadioButton) findViewById(selectedRealizationIncomeStatementId)).getText().toString();

        Map<String, Object> feedback = new HashMap<>();
        feedback.put("videoRating", videoRating);
        feedback.put("clarityRating", clarityRating);
        feedback.put("usefulnessRating", usefulnessRating);
        feedback.put("lessonLearned", lessonLearned);
        feedback.put("comments", TextUtils.isEmpty(comments) ? "No comments provided" : comments);
        feedback.put("canDrawIncomeStatement", incomeStatementAbility);
        feedback.put("hasExistingIncomeStatement", existingIncomeStatement);
        feedback.put("hasRecentIncomeStatement", recentIncomeStatement);
        feedback.put("realizesIncomeStatementImportance", realizationIncomeStatement);
        feedback.put("timestamp", System.currentTimeMillis());

        databaseReference.child(String.valueOf(System.currentTimeMillis()))
                .setValue(feedback)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Feedback submitted successfully.");
                        setResult(RESULT_OK);
                        navigateToEndofPart3Activity();
                    } else {
                        Log.e(TAG, "Error submitting feedback", task.getException());
                    }
                });

        setResult(RESULT_OK);
        navigateToEndofPart3Activity();
    }


    private void showErrorDialog(List<String> errors) {
        StringBuilder errorMessage = new StringBuilder();
        for (String error : errors) {
            errorMessage.append("• ").append(error).append("\n");
        }

        new AlertDialog.Builder(this)
                .setTitle("Errors")
                .setMessage(errorMessage.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private void navigateToEndofPart3Activity() {
        Intent endOfPartIntent = new Intent(AfterVideo15Activity.this, OverallAssessmentActivity.class);
        startActivity(endOfPartIntent);
        finish(); // Finish current activity to remove it from the back stack
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
            startActivity(new Intent(AfterVideo15Activity.this, TopicsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(AfterVideo15Activity.this, HelpActivity.class));
            overridePendingTransition(0,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
