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

public class AfterVideo4Activity extends AppCompatActivity {

    private static final String TAG = "AfterVideo4Activity";

    // UI components
    private RatingBar ratingVideo, ratingClarity, ratingUsefulness;
    private EditText etLesson, etChangesExplain, etComments;
    private TextView tv_changes_explain, tv_reminder;

    ImageView btnBack;
    private RadioGroup rgChangePlan, rgRecordBook;
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
        setContentView(R.layout.activity_after_video4);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback After Video 4");
        databaseReference.keepSynced(true); // Ensures local data is synced when online

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        // Initialize UI components
        ratingVideo = findViewById(R.id.rating_video);
        ratingClarity = findViewById(R.id.rating_clarity);
        ratingUsefulness = findViewById(R.id.rating_usefulness);
        etLesson = findViewById(R.id.et_lesson);
        rgChangePlan = findViewById(R.id.change_plan_group);
        etChangesExplain = findViewById(R.id.editText_changes);
        rgRecordBook = findViewById(R.id.record_book_group);
        etComments = findViewById(R.id.editText_comments);
        btnSubmit = findViewById(R.id.button_submit);
        tv_changes_explain = findViewById(R.id.tv_changes_explain);
        tv_reminder = findViewById(R.id.tv_reminder);
        btnBack = findViewById(R.id.btn_back);

        rgChangePlan.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.plan_yes) {
                tv_changes_explain.setVisibility(View.VISIBLE);
                etChangesExplain.setVisibility(View.VISIBLE);
                tv_reminder.setVisibility(View.VISIBLE);
            } else {
                tv_changes_explain.setVisibility(View.GONE);
                etChangesExplain.setVisibility(View.GONE);
                tv_reminder.setVisibility(View.GONE);
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
        String lessonLearned = etLesson.getText().toString();
        String changesExplanation = etChangesExplain.getText().toString();
        String comments = etComments.getText().toString();

        // Get selected radio button values
        int selectedChangePlanId = rgChangePlan.getCheckedRadioButtonId();
        String changePlan = selectedChangePlanId == R.id.plan_yes ? "Yes" :
                selectedChangePlanId == R.id.plan_no ? "No" : "";

        int selectedRecordBookId = rgRecordBook.getCheckedRadioButtonId();
        String recordBook = selectedRecordBookId == R.id.record_yes ? "Yes" :
                selectedRecordBookId == R.id.record_no ? "No" : "";

        // Collect errors
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
        if (TextUtils.isEmpty(lessonLearned)) {
            errors.add("Please write the biggest lesson you learned.");
        }
        if (TextUtils.isEmpty(changePlan)) {
            errors.add("Please indicate whether you want to make changes in handling your business money.");
        }
        if (changePlan.equals("Yes") && TextUtils.isEmpty(changesExplanation)) {
            errors.add("Please explain the changes you want to make.");
        }
        if (TextUtils.isEmpty(recordBook)) {
            errors.add("Please indicate whether you already have a record book.");
        }

        // Show errors in dialog
        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        // Create a feedback map
        Map<String, Object> feedback = new HashMap<>();
        feedback.put("videoRating", videoRating);
        feedback.put("clarityRating", clarityRating);
        feedback.put("usefulnessRating", usefulnessRating);
        feedback.put("lessonLearned", lessonLearned);
        feedback.put("changePlan", changePlan);
        feedback.put("changesExplanation", changesExplanation.isEmpty() ? "No explanation provided" : changesExplanation);
        feedback.put("recordBook", recordBook);
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
        navigateToEndofPart1Activity(); // Close this activity
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
                        navigateToEndofPart1Activity();
                    }
                })
                .show();
    }

    private void navigateToEndofPart1Activity() {
        Intent intent = new Intent(AfterVideo4Activity.this, EndofPart1Activity.class);
        startActivity(intent);
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
            startActivity(new Intent(AfterVideo4Activity.this, TopicsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(AfterVideo4Activity.this, HelpActivity.class));
            overridePendingTransition(0,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
