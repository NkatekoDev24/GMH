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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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

public class AfterVideo1Activity extends AppCompatActivity {

    private static final String TAG = "AfterVideo1Activity";

    private RatingBar ratingOverall, ratingClarity, ratingUsefulness;
    private EditText etLesson, etComments;
//    ImageView btnBack;

    private Button btnSubmit;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_after_video1);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback After Video 1");
        databaseReference.keepSynced(true); // Ensures local data is synced when online

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        // Initialize UI components
        ratingOverall = findViewById(R.id.rating_overall);
        ratingClarity = findViewById(R.id.rating_clarity);
        ratingUsefulness = findViewById(R.id.rating_usefulness);
        etLesson = findViewById(R.id.et_lesson);
        etComments = findViewById(R.id.et_comments);

        btnSubmit = findViewById(R.id.btn_submit);
//        btnBack = findViewById(R.id.btn_back);

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
        float overallRating = ratingOverall.getRating();
        float clarityRating = ratingClarity.getRating();
        float usefulnessRating = ratingUsefulness.getRating();
        String lessonLearned = etLesson.getText().toString().trim();
        String comments = etComments.getText().toString().trim();

        // Get the selected radio button value
        /*int selectedConsentId = rgConsent.getCheckedRadioButtonId();
        String consentGiven;

        if (selectedConsentId == R.id.rb_consent_yes) {
            consentGiven = "Yes";
        } else if (selectedConsentId == R.id.rb_consent_no) {
            consentGiven = "No";
        } else {
            consentGiven = null; // No selection made
        }*/

        // Create a list to hold error messages
        List<String> errors = new ArrayList<>();

        // Validate input
        if (overallRating == 0) errors.add("Please rate the video overall.");
        if (clarityRating == 0) errors.add("Please rate the clarity of the information.");
        if (usefulnessRating == 0) errors.add("Please rate the usefulness of the information.");
        if (TextUtils.isEmpty(lessonLearned)) errors.add("Please write the biggest lesson you learned.");
        /*if (consentGiven == null) errors.add("Please provide your consent for data collection.");*/

        // Show errors if any
        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        // Create feedback data
        Map<String, Object> feedback = new HashMap<>();
        feedback.put("overallRating", overallRating);
        feedback.put("clarityRating", clarityRating);
        feedback.put("usefulnessRating", usefulnessRating);
        feedback.put("lessonLearned", lessonLearned);
        feedback.put("comments", TextUtils.isEmpty(comments) ? "No comments provided" : comments);
        /*feedback.put("consentGiven", consentGiven);*/
        feedback.put("timestamp", System.currentTimeMillis());

        // Save data to Firebase (offline support enabled)
        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(feedback)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Feedback submitted successfully.");
                    } else {
                        Log.e(TAG, "Error submitting feedback", task.getException());
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

        // Create and show an AlertDialog with the error messages
        new AlertDialog.Builder(this)
                .setTitle("Errors")
                .setMessage(errorMessage.toString())
                .setPositiveButton("OK", null)
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
            startActivity(new Intent(AfterVideo1Activity.this, TopicsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(AfterVideo1Activity.this, HelpActivity.class));
            overridePendingTransition(0,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
