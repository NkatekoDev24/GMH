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

public class AfterVideo7Activity extends AppCompatActivity {

    private static final String TAG = "AfterVideo7Activity";

    // UI components
    private RatingBar ratingVideo, ratingClarity, ratingUsefulness, currentHabitsRating, desiredHabitsRating;
    private EditText editTextLesson, editTextChanges, editTextComments;
    private TextView txtChangesExplained, reminderNextVideo;

    private ImageView btnBack;
    private RadioGroup changePlanGroup;
    private Button btnSubmit;

    // Firebase reference
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set the layout
        setContentView(R.layout.activity_after_video7);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback After Video 7");
        databaseReference.keepSynced(true); // Ensures local data is synced when online

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        // Initialize UI components
        ratingVideo = findViewById(R.id.rating_video);
        ratingClarity = findViewById(R.id.rating_clarity);
        ratingUsefulness = findViewById(R.id.rating_usefulness);
        currentHabitsRating = findViewById(R.id.current_habits_rating);
        desiredHabitsRating = findViewById(R.id.desired_habits_rating);
        editTextLesson = findViewById(R.id.editText_lesson);
        editTextChanges = findViewById(R.id.editText_changes);
        editTextComments = findViewById(R.id.editText_comments);
        changePlanGroup = findViewById(R.id.change_plan_group);
        btnSubmit = findViewById(R.id.button_submit);
        txtChangesExplained = findViewById(R.id.tv_changes_explain);
        reminderNextVideo = findViewById(R.id.reminder_next_video);
        btnBack = findViewById(R.id.btn_back);

        changePlanGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.plan_yes) {
                txtChangesExplained.setVisibility(View.VISIBLE);
                editTextChanges.setVisibility(View.VISIBLE);
                reminderNextVideo.setVisibility(View.VISIBLE);
            } else {
                txtChangesExplained.setVisibility(View.GONE);
                editTextChanges.setVisibility(View.GONE);
                reminderNextVideo.setVisibility(View.GONE);
            }
        });

        // Set up button click listener
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
        // Collect input data
        float videoRating = ratingVideo.getRating();
        float clarityRating = ratingClarity.getRating();
        float usefulnessRating = ratingUsefulness.getRating();
        float currentHabits = currentHabitsRating.getRating();
        float desiredHabits = desiredHabitsRating.getRating();
        String lessonLearned = editTextLesson.getText().toString();
        String changesExplanation = editTextChanges.getText().toString();
        String comments = editTextComments.getText().toString();

        // Get selected radio button value
        int selectedChangePlanId = changePlanGroup.getCheckedRadioButtonId();
        String changePlan = selectedChangePlanId == R.id.plan_yes ? "Yes" :
                selectedChangePlanId == R.id.plan_no ? "No" : "";

        // Validate inputs
        List<String> errors = new ArrayList<>();
        if (videoRating == 0) {
            errors.add("Please rate the video.");
        }
        if (clarityRating == 0) {
            errors.add("Please rate the clarity of the information.");
        }
        if (usefulnessRating == 0) {
            errors.add("Please rate the usefulness of the information.");
        }
        if (currentHabits == 0) {
            errors.add("Please rate your current money habits.");
        }
        if (desiredHabits == 0) {
            errors.add("Please rate your desired money habits for 4 weeks from now.");
        }
        if (TextUtils.isEmpty(lessonLearned)) {
            errors.add("Please write the biggest lesson you learned.");
        }
        if (TextUtils.isEmpty(changePlan)) {
            errors.add("Please indicate whether you want to make changes.");
        }
        if (changePlan.equals("Yes") && TextUtils.isEmpty(changesExplanation)) {
            errors.add("Please explain the changes you want to make.");
        }

        // Show errors if any
        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        // Prepare feedback data
        Map<String, Object> feedback = new HashMap<>();
        feedback.put("videoRating", videoRating);
        feedback.put("clarityRating", clarityRating);
        feedback.put("usefulnessRating", usefulnessRating);
        feedback.put("currentHabitsRating", currentHabits);
        feedback.put("desiredHabitsRating", desiredHabits);
        feedback.put("lessonLearned", lessonLearned);
        feedback.put("changePlan", changePlan);
        feedback.put("changesExplanation", changesExplanation.isEmpty() ? "No explanation provided" : changesExplanation);
        feedback.put("comments", comments.isEmpty() ? "No comments provided" : comments);

        // Debugging: Log feedback data
        Log.d(TAG, "Feedback Data: " + feedback);

        // Save feedback to Firebase
        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(feedback)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showMessageDialog("Success", "Feedback submitted successfully!", true);
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error occurred.";
                        Log.e(TAG, "Error submitting feedback: " + error);
                        showMessageDialog("Error", "Failed to submit feedback: " + error, false);
                    }
                });

        // Proceed to the next activity immediately
        setResult(RESULT_OK);
        finish(); // Close this activity
    }

    private void showErrorDialog(List<String> errors) {
        // Combine error messages into a single string
        StringBuilder errorMessage = new StringBuilder();
        for (String error : errors) {
            errorMessage.append("• ").append(error).append("\n");
        }

        // Show an AlertDialog with the errors
        new AlertDialog.Builder(this)
                .setTitle("Error")
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
                        finish(); // Close activity after successful submission
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
            startActivity(new Intent(AfterVideo7Activity .this, TopicsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(AfterVideo7Activity.this, HelpActivity.class));
            overridePendingTransition(0,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
