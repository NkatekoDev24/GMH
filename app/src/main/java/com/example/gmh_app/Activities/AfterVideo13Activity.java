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

public class AfterVideo13Activity extends AppCompatActivity {

    private static final String TAG = "AfterVideo13Activity";

    private RatingBar ratingBarVideo, ratingBarClarity, ratingBarUsefulness;
    private EditText editTextLesson, editTextUnderstanding, editTextComments;
    private ImageView btnBack;
    private RadioGroup radioGroupGrossNetProfit, radioGroupWhenToUseProfit;
    private Button buttonSubmit;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_after_video13);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback After Video 13");
        databaseReference.keepSynced(true);
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        ratingBarVideo = findViewById(R.id.ratingBarVideo);
        ratingBarClarity = findViewById(R.id.ratingBarClarity);
        ratingBarUsefulness = findViewById(R.id.ratingBarUsefulness);
        editTextLesson = findViewById(R.id.editTextLesson);
        editTextUnderstanding = findViewById(R.id.editTextUnderstanding);
        editTextComments = findViewById(R.id.editTextComments);
        radioGroupGrossNetProfit = findViewById(R.id.radioGroupGrossNetProfit);
        radioGroupWhenToUseProfit = findViewById(R.id.radioGroupWhenToUseProfit);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        btnBack = findViewById(R.id.btn_back);

        buttonSubmit.setOnClickListener(v -> validateAndSubmitFeedback());
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setResult(RESULT_CANCELED);
//                finish();
//            }
//        });
    }

    private void validateAndSubmitFeedback() {
        float videoRating = ratingBarVideo.getRating();
        float clarityRating = ratingBarClarity.getRating();
        float usefulnessRating = ratingBarUsefulness.getRating();
        String lessonLearned = editTextLesson.getText().toString().trim();
        String understandingBenefit = editTextUnderstanding.getText().toString().trim();
        String comments = editTextComments.getText().toString().trim();

        int selectedGrossNetProfitId = radioGroupGrossNetProfit.getCheckedRadioButtonId();
        Boolean understandsGrossNetProfit = selectedGrossNetProfitId == R.id.radioYesGrossNetProfit ? true :
                (selectedGrossNetProfitId == R.id.radioNoGrossNetProfit ? false : null);

        int selectedWhenToUseProfitId = radioGroupWhenToUseProfit.getCheckedRadioButtonId();
        Boolean understandsWhenToUseProfit = selectedWhenToUseProfitId == R.id.radioYesWhenToUseProfit ? true :
                (selectedWhenToUseProfitId == R.id.radioNoWhenToUseProfit ? false : null);

        List<String> errors = new ArrayList<>();

        if (videoRating == 0) errors.add("Please rate the video.");
        if (clarityRating == 0) errors.add("Please rate the clarity.");
        if (usefulnessRating == 0) errors.add("Please rate the usefulness.");
        if (TextUtils.isEmpty(lessonLearned)) errors.add("Please write what you learned from the video.");
        if (TextUtils.isEmpty(understandingBenefit)) errors.add("Please describe your understanding.");
        if (understandsGrossNetProfit == null) errors.add("Please select an option for Gross vs. Net Profit.");
        if (understandsWhenToUseProfit == null) errors.add("Please select an option for When to Use Profit.");

        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        Map<String, Object> feedback = new HashMap<>();
        feedback.put("videoRating", videoRating);
        feedback.put("clarityRating", clarityRating);
        feedback.put("usefulnessRating", usefulnessRating);
        feedback.put("lessonLearned", lessonLearned);
        feedback.put("understandingBenefit", understandingBenefit);
        feedback.put("understandsGrossNetProfit", understandsGrossNetProfit);
        feedback.put("understandsWhenToUseProfit", understandsWhenToUseProfit);
        feedback.put("comments", TextUtils.isEmpty(comments) ? "No comments provided" : comments);
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
                .setTitle("Validation Errors")
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
            startActivity(new Intent(AfterVideo13Activity.this, TopicsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(AfterVideo13Activity.this, HelpActivity.class));
            overridePendingTransition(0,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
