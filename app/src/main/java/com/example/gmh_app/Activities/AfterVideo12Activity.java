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
import android.widget.TextView;

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

public class AfterVideo12Activity extends AppCompatActivity {

    private static final String TAG = "AfterVideo12Activity";

    private RatingBar ratingVideo, ratingClarity, ratingUsefulness;
    private RadioGroup profitChangesGroup, profitDifferenceGroup, profitConfidenceGroup;
    private TextView changesExplained, txtYes, reminderNextVideo;

    private ImageView btnBack;
    private EditText lessonLearnedEditText, changesExplanationEditText, profitAmountEditText, additionalCommentsEditText;
    private Button submitButton;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_after_video12);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback After Video 12");
        databaseReference.keepSynced(true);

        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        ratingVideo = findViewById(R.id.rating_video);
        ratingClarity = findViewById(R.id.rating_clarity);
        ratingUsefulness = findViewById(R.id.rating_usefulness);
        profitChangesGroup = findViewById(R.id.profit_changes);
        profitDifferenceGroup = findViewById(R.id.profit_difference);
        profitConfidenceGroup = findViewById(R.id.profit_confidence);
        lessonLearnedEditText = findViewById(R.id.lesson_learned);
        changesExplanationEditText = findViewById(R.id.changes_explanation);
        profitAmountEditText = findViewById(R.id.profit_amount);
        additionalCommentsEditText = findViewById(R.id.additional_comments);
        submitButton = findViewById(R.id.submit_button);
        changesExplained = findViewById(R.id.text_changes_explained);
        txtYes = findViewById(R.id.txtIfYes);
//        reminderNextVideo = findViewById(R.id.reminder_next_video);
        btnBack = findViewById(R.id.btn_back);

        profitChangesGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.profit_changes_yes) {
                changesExplained.setVisibility(View.VISIBLE);
                changesExplanationEditText.setVisibility(View.VISIBLE);
            } else {
                changesExplained.setVisibility(View.GONE);
                changesExplanationEditText.setVisibility(View.GONE);
            }
        });

        profitDifferenceGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.profit_difference_yes) {
                txtYes.setVisibility(View.VISIBLE);
                profitAmountEditText.setVisibility(View.VISIBLE);
//                reminderNextVideo.setVisibility(View.VISIBLE);
            } else {
                txtYes.setVisibility(View.GONE);
                profitAmountEditText.setVisibility(View.GONE);
//                reminderNextVideo.setVisibility(View.GONE);
            }
        });

        submitButton.setOnClickListener(v -> validateAndSubmitFeedback());
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
        String lessonLearned = lessonLearnedEditText.getText().toString();
        String changesExplanation = changesExplanationEditText.getText().toString();
        String profitAmount = profitAmountEditText.getText().toString();
        String additionalComments = additionalCommentsEditText.getText().toString();

        String profitChanges = getSelectedRadioValue(profitChangesGroup);
        String profitDifference = getSelectedRadioValue(profitDifferenceGroup);
        String profitConfidence = getSelectedRadioValue(profitConfidenceGroup);

        List<String> errors = new ArrayList<>();

        if (videoRating == 0 || clarityRating == 0 || usefulnessRating == 0 ||
                TextUtils.isEmpty(lessonLearned) || TextUtils.isEmpty(profitChanges) || TextUtils.isEmpty(profitConfidence)) {
            errors.add("Please complete all required fields before submitting.");
        }

        if (profitChanges.equals("Yes") && TextUtils.isEmpty(changesExplanation)) {
            errors.add("Please explain the changes you want to make to your profits.");
        }

        if (profitDifference.equals("Yes") && TextUtils.isEmpty(profitAmount)) {
            errors.add("Please provide an approximate profit amount if you expect it to change.");
        }


        // Show errors if any
        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        Map<String, Object> feedback = new HashMap<>();
        feedback.put("videoRating", videoRating);
        feedback.put("clarityRating", clarityRating);
        feedback.put("usefulnessRating", usefulnessRating);
        feedback.put("lessonLearned", lessonLearned);
        feedback.put("profitChanges", profitChanges);
        feedback.put("changesExplanation", TextUtils.isEmpty(changesExplanation) ? "No changes explained" : changesExplanation);
        feedback.put("profitDifference", profitDifference);
        feedback.put("profitAmount", TextUtils.isEmpty(profitAmount) ? "Not provided" : profitAmount);
        feedback.put("profitConfidence", profitConfidence);
        feedback.put("additionalComments", TextUtils.isEmpty(additionalComments) ? "No comments provided" : additionalComments);

        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(feedback)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showMessageDialog("Success", "Feedback submitted successfully!", true);
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error.";
                        Log.e(TAG, "Failed to submit feedback: " + error);
                        showMessageDialog("Error", "Error submitting feedback: " + error, false);
                    }
                });

        setResult(RESULT_OK);
        finish();
    }

    private String getSelectedRadioValue(RadioGroup group) {
        int selectedId = group.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedButton = findViewById(selectedId);
            return selectedButton.getText().toString();
        }
        return "";
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

    private void showMessageDialog(String title, String message, boolean isSuccess) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (isSuccess) {
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .show();
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
            startActivity(new Intent(AfterVideo12Activity.this, TopicsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(AfterVideo12Activity.this, HelpActivity.class));
            overridePendingTransition(0,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
