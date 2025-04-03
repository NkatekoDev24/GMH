package com.example.gmh_app.Activities;

import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AfterVideo15Activity extends AppCompatActivity {

    private static final String TAG = "AfterVideo15Activity";

    private RatingBar ratingVideo, ratingClarity, ratingUsefulness;
    private RadioGroup rgIncomeStatementAbility, rgExistingIncomeStatement, rgRecentIncomeStatement, rgRealizationIncomeStatement;
    private EditText etLesson, etComments;
    private Button btnSubmit;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_after_video15);

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

        btnSubmit.setOnClickListener(v -> validateAndSubmitFeedback());
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

        Map<String, Object> feedback = new HashMap<>();
        feedback.put("videoRating", videoRating);
        feedback.put("clarityRating", clarityRating);
        feedback.put("usefulnessRating", usefulnessRating);
        feedback.put("lessonLearned", lessonLearned);
        feedback.put("comments", TextUtils.isEmpty(comments) ? "No comments provided" : comments);
        feedback.put("canDrawIncomeStatement", selectedIncomeStatementAbilityId == R.id.rb_income_statement_ability_yes);
        feedback.put("hasExistingIncomeStatement", selectedExistingIncomeStatementId == R.id.rb_existing_income_statement_yes);
        feedback.put("hasRecentIncomeStatement", selectedRecentIncomeStatementId == R.id.rb_recent_income_statement_yes);
        feedback.put("realizesIncomeStatementImportance", selectedRealizationIncomeStatementId == R.id.rb_realization_income_statement_yes);
        feedback.put("timestamp", System.currentTimeMillis());

        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(feedback)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Feedback submitted successfully.");
                    } else {
                        Log.e(TAG, "Error submitting feedback", task.getException());
                    }
                });

        setResult(RESULT_OK);
        finish();
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
}
