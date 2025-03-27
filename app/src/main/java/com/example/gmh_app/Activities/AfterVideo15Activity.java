package com.example.gmh_app.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gmh_app.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AfterVideo15Activity extends AppCompatActivity {

    private RatingBar ratingVideo, ratingClarity, ratingUsefulness;
    private RadioGroup rgIncomeStatementAbility, rgExistingIncomeStatement, rgRecentIncomeStatement, rgRealizationIncomeStatement;
    private EditText etLesson, etComments;
    private Button btnSubmit;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_after_video15);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback After Video 15");
        databaseReference.keepSynced(true); // Ensures local data is synced when online

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        // Link UI elements to their corresponding views
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

        btnSubmit.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        // Collect data from UI elements
        float videoRating = ratingVideo.getRating();
        float clarityRating = ratingClarity.getRating();
        float usefulnessRating = ratingUsefulness.getRating();
        String lessonLearned = etLesson.getText().toString().trim();
        String comments = etComments.getText().toString().trim();

        int selectedIncomeStatementAbilityId = rgIncomeStatementAbility.getCheckedRadioButtonId();
        boolean canDrawIncomeStatement = (selectedIncomeStatementAbilityId == R.id.rb_income_statement_ability_yes);

        int selectedExistingIncomeStatementId = rgExistingIncomeStatement.getCheckedRadioButtonId();
        boolean hasExistingIncomeStatement = (selectedExistingIncomeStatementId == R.id.rb_existing_income_statement_yes);

        int selectedRecentIncomeStatementId = rgRecentIncomeStatement.getCheckedRadioButtonId();
        boolean hasRecentIncomeStatement = (selectedRecentIncomeStatementId == R.id.rb_recent_income_statement_yes);

        int selectedRealizationIncomeStatementId = rgRealizationIncomeStatement.getCheckedRadioButtonId();
        boolean realizesIncomeStatementImportance = (selectedRealizationIncomeStatementId == R.id.rb_realization_income_statement_yes);

        // Validation
        if (lessonLearned.isEmpty()) {
            showMessageDialog("Validation Error", "Please fill out all required fields.", false);
            return;
        }

        // Create a feedback map to send to Firebase
        Map<String, Object> feedback = new HashMap<>();
        feedback.put("videoRating", videoRating);
        feedback.put("clarityRating", clarityRating);
        feedback.put("usefulnessRating", usefulnessRating);
        feedback.put("lessonLearned", lessonLearned);
        feedback.put("canDrawIncomeStatement", canDrawIncomeStatement);
        feedback.put("hasExistingIncomeStatement", hasExistingIncomeStatement);
        feedback.put("hasRecentIncomeStatement", hasRecentIncomeStatement);
        feedback.put("realizesIncomeStatementImportance", realizesIncomeStatementImportance);
        feedback.put("comments", comments.isEmpty() ? "No comments provided" : comments);

        // Send data to Firebase
        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(feedback)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showMessageDialog("Success", "Feedback submitted successfully!", true);
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error.";
                        showMessageDialog("Error", "Failed to submit feedback: " + error, false);
                    }
                });
    }

    // Helper method to show a message dialog
    private void showMessageDialog(String title, String message, boolean isSuccess) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (isSuccess) {
                        navigateToEndofPart4Activity();
                    }
                })
                .show();
    }

    // Navigates to EndofPart4Activity after submitting
    private void navigateToEndofPart4Activity() {
        Intent endOfPartIntent = new Intent(AfterVideo15Activity.this, OverallAssessmentActivity.class);
        startActivity(endOfPartIntent);
        finish(); // Finish current activity to remove it from the back stack
    }
}
