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

public class AfterVideo3Activity extends AppCompatActivity {

    private static final String TAG = "AfterVideo3Activity";

    // UI components
    private RatingBar ratingVideo, ratingClarity, ratingUsefulness;
    private EditText etLesson, etChangesExplain, etComments;
    private TextView tv_changes_explain, tv_reminder;
    ImageView btnBack;
    private RadioGroup rgChanges;
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

        setContentView(R.layout.activity_after_video3);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback After Video 3");
        databaseReference.keepSynced(true); // Ensures local data is synced when online

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        // Initialize UI components
        ratingVideo = findViewById(R.id.rating_video);
        ratingClarity = findViewById(R.id.rating_clarity);
        ratingUsefulness = findViewById(R.id.rating_usefulness);
        etLesson = findViewById(R.id.et_lesson);
        rgChanges = findViewById(R.id.rg_changes);
        etChangesExplain = findViewById(R.id.et_changes_explain);
        etComments = findViewById(R.id.et_comments);
        btnSubmit = findViewById(R.id.btn_submit);
        tv_changes_explain = findViewById(R.id.tv_changes_explain);
        tv_reminder = findViewById(R.id.tv_reminder);
        btnBack = findViewById(R.id.btn_back);


        rgChanges.setOnCheckedChangeListener((group, checkedId) ->{
            if (checkedId == R.id.rb_yes) {
                tv_changes_explain.setVisibility(View.VISIBLE);
                etChangesExplain.setVisibility(View.VISIBLE);
                tv_reminder.setVisibility(View.VISIBLE);
            } else {
                tv_changes_explain.setVisibility(View.GONE);
                etChangesExplain.setVisibility(View.GONE);
                tv_reminder.setVisibility(View.GONE);

            }
        });

        // Set button click listener
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
        int selectedChangeId = rgChanges.getCheckedRadioButtonId();
        String changesExplain = etChangesExplain.getText().toString();
        String comments = etComments.getText().toString();

        // Create a list to hold error messages
        List<String> errors = new ArrayList<>();

        // Validate inputs
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
            errors.add("Please describe the biggest lesson you learned.");
        }
        if (selectedChangeId == -1) {
            errors.add("Please indicate if you plan to make changes in the way you handle your business money.");
        }
        if (selectedChangeId == R.id.rb_yes && TextUtils.isEmpty(changesExplain)) {
            errors.add("Please explain the changes you want to make.");
        }

        // Show errors if any
        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        // Create feedback data
        Map<String, Object> feedback = new HashMap<>();
        feedback.put("videoRating", videoRating);
        feedback.put("clarityRating", clarityRating);
        feedback.put("usefulnessRating", usefulnessRating);
        feedback.put("lessonLearned", lessonLearned);

        String changes = selectedChangeId == R.id.rb_yes ? "Yes" : "No";
        feedback.put("changes", changes);
        feedback.put("changesExplain", TextUtils.isEmpty(changesExplain) ? "No explanation provided" : changesExplain);
        feedback.put("comments", TextUtils.isEmpty(comments) ? "No comments provided" : comments);

        // Debugging: Log the feedback data
        Log.d(TAG, "Feedback Data: " + feedback);

        // Save feedback to Firebase
        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(feedback)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showMessageDialog("Success", "Feedback submitted successfully!", true);
                    } else {
                        Exception e = task.getException();
                        String errorMessage = e != null ? e.getMessage() : "Unknown error occurred.";
                        Log.e(TAG, "Error submitting feedback", e);
                        showMessageDialog("Error", "Failed to submit feedback: " + errorMessage, false);
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

        // Show the error messages in an AlertDialog
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
            startActivity(new Intent(AfterVideo3Activity.this, TopicsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(AfterVideo3Activity.this, HelpActivity.class));
            overridePendingTransition(0,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
