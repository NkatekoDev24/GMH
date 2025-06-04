package com.example.gmh_app.Activities;

import android.content.DialogInterface;
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

public class AfterVideo11Activity extends AppCompatActivity {

    private static final String TAG = "AfterVideo11Activity";

    private RatingBar ratingVideo, ratingClarity, ratingUsefulness, ratingControlCurrent, ratingControlFuture;
    private RadioGroup fixedCostChangesGroup;
    private TextView changesExplained;
    private EditText lessonLearnedEditText, changesExplanationEditText, additionalCommentsEditText;
    private Button submitButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_after_video11);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback After Video 11");
        databaseReference.keepSynced(true);

        // Initialize views
        ratingVideo = findViewById(R.id.rating_video);
        ratingClarity = findViewById(R.id.rating_clarity);
        ratingUsefulness = findViewById(R.id.rating_usefulness);
        ratingControlCurrent = findViewById(R.id.rating_control_current);
        ratingControlFuture = findViewById(R.id.rating_control_future);
        fixedCostChangesGroup = findViewById(R.id.fixed_cost_changes);
        lessonLearnedEditText = findViewById(R.id.lesson_learned);
        changesExplanationEditText = findViewById(R.id.changes_explanation);
        additionalCommentsEditText = findViewById(R.id.additional_comments);
        submitButton = findViewById(R.id.submit_button);
        changesExplained = findViewById(R.id.text_changes_explained);

        // Toggle explanation field based on radio button
        fixedCostChangesGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean show = (checkedId == R.id.fixed_cost_changes_yes);
            changesExplained.setVisibility(show ? View.VISIBLE : View.GONE);
            changesExplanationEditText.setVisibility(show ? View.VISIBLE : View.GONE);
        });

        submitButton.setOnClickListener(v -> validateAndSubmitFeedback());
    }

    private void validateAndSubmitFeedback() {
        float videoRating = ratingVideo.getRating();
        float clarityRating = ratingClarity.getRating();
        float usefulnessRating = ratingUsefulness.getRating();
        float currentControlRating = ratingControlCurrent.getRating();
        float futureControlRating = ratingControlFuture.getRating();

        String lessonLearned = lessonLearnedEditText.getText().toString().trim();
        String changesExplanation = changesExplanationEditText.getText().toString().trim();
        String additionalComments = additionalCommentsEditText.getText().toString().trim();

        int selectedChangeOptionId = fixedCostChangesGroup.getCheckedRadioButtonId();
        String fixedCostChange = selectedChangeOptionId == R.id.fixed_cost_changes_yes ? "Yes" :
                selectedChangeOptionId == R.id.fixed_cost_changes_no ? "No" : "";

        List<String> errors = new ArrayList<>();
        if (videoRating == 0) errors.add("Please rate the video.");
        if (clarityRating == 0) errors.add("Please rate the clarity of the information.");
        if (usefulnessRating == 0) errors.add("Please rate the usefulness of the information.");
        if (currentControlRating == 0) errors.add("Please rate your current control of overhead costs.");
        if (futureControlRating == 0) errors.add("Please rate how you'd like to control overhead costs in the future.");
        if (TextUtils.isEmpty(lessonLearned)) errors.add("Please provide the biggest lesson you learned.");
        if (TextUtils.isEmpty(fixedCostChange)) errors.add("Please indicate whether you plan to make changes to handle fixed costs.");
        if (fixedCostChange.equals("Yes") && TextUtils.isEmpty(changesExplanation)) errors.add("Please explain the changes you want to make.");

        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        Map<String, Object> feedback = new HashMap<>();
        feedback.put("videoRating", videoRating);
        feedback.put("clarityRating", clarityRating);
        feedback.put("usefulnessRating", usefulnessRating);
        feedback.put("currentControlRating", currentControlRating);
        feedback.put("futureControlRating", futureControlRating);
        feedback.put("lessonLearned", lessonLearned);
        feedback.put("fixedCostChange", fixedCostChange);
        feedback.put("changesExplanation", changesExplanation.isEmpty() ? "No changes explained" : changesExplanation);
        feedback.put("additionalComments", additionalComments.isEmpty() ? "No comments provided" : additionalComments);

        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(feedback)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showSuccessDialog();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error.";
                        Log.e(TAG, "Failed to submit feedback: " + error);
                        showErrorDialog(error);
                    }
                });

        setResult(RESULT_OK);
        navigateToEndofPart3Activity();
    }

    private void showErrorDialog(List<String> errors) {
        StringBuilder message = new StringBuilder();
        for (String error : errors) {
            message.append("• ").append(error).append("\n");
        }
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private void showErrorDialog(String errorMessage) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(errorMessage)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage("Feedback submitted successfully!")
                .setPositiveButton("OK", (dialog, which) -> navigateToEndofPart3Activity())
                .show();
    }

    private void navigateToEndofPart3Activity() {
        Intent intent = new Intent(AfterVideo11Activity.this, OverallPart3Activity.class);
        startActivity(intent);
        finish();
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
            startActivity(new Intent(this, TopicsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(this, HelpActivity.class));
            overridePendingTransition(0, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
