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

public class AfterVideo8Activity extends AppCompatActivity {

    private static final String TAG = "AfterVideo8Activity";

    private RatingBar ratingVideo, ratingClarity, ratingUsefulness, ratingTransactions;
    private RadioGroup changePlanGroup;
    private RadioButton transactionsNotApplicable;
    private TextView changesExplained, reminderNextVideo;

    private EditText editTextChanges, editTextComments;
    private Button buttonSubmit;
    private ImageView btnBack;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_after_video8);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback After Video 8");
        databaseReference.keepSynced(true);

        ratingVideo = findViewById(R.id.rating_overall);
        ratingClarity = findViewById(R.id.rating_clarity);
        ratingUsefulness = findViewById(R.id.rating_usefulness);
        ratingTransactions = findViewById(R.id.rating_transactions);
        editTextChanges = findViewById(R.id.editText_changes);
        editTextComments = findViewById(R.id.editText_comments);
        changePlanGroup = findViewById(R.id.change_plan_group);
        buttonSubmit = findViewById(R.id.button_submit);
        changesExplained = findViewById(R.id.tv_changes_explain);
        reminderNextVideo = findViewById(R.id.reminder_next_video);
        transactionsNotApplicable = findViewById(R.id.transactions_not_applicable);
        btnBack = findViewById(R.id.btn_back);

        changePlanGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.plan_yes) {
                changesExplained.setVisibility(View.VISIBLE);
                editTextChanges.setVisibility(View.VISIBLE);
                reminderNextVideo.setVisibility(View.VISIBLE);
            } else {
                changesExplained.setVisibility(View.GONE);
                editTextChanges.setVisibility(View.GONE);
                reminderNextVideo.setVisibility(View.GONE);
            }
        });

        ratingTransactions.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser && rating > 0) {
                transactionsNotApplicable.setChecked(false);
                transactionsNotApplicable.setVisibility(View.GONE);
            }
        });

        transactionsNotApplicable.setOnClickListener(v -> {
            ratingTransactions.setRating(0);
            ratingTransactions.setVisibility(View.GONE);
        });

        buttonSubmit.setOnClickListener(v -> validateAndSubmitFeedback());
    }

    private void validateAndSubmitFeedback() {
        float videoRating = ratingVideo.getRating();
        float clarityRating = ratingClarity.getRating();
        float usefulnessRating = ratingUsefulness.getRating();
        float transactionsRating = ratingTransactions.getRating();
        String changesExplanation = editTextChanges.getText().toString();
        String comments = editTextComments.getText().toString();

        int selectedChangePlanId = changePlanGroup.getCheckedRadioButtonId();
        String changePlan = selectedChangePlanId == R.id.plan_yes ? "Yes" :
                selectedChangePlanId == R.id.plan_no ? "No" :
                        selectedChangePlanId == R.id.plan_not_applicable ? "Not applicable" : "";

        List<String> errors = new ArrayList<>();
        if (videoRating == 0) errors.add("Please rate the video.");
        if (clarityRating == 0) errors.add("Please rate the clarity of the information.");
        if (usefulnessRating == 0) errors.add("Please rate the usefulness of the information.");
        if (transactionsRating == 0 && !transactionsNotApplicable.isChecked())
            errors.add("Please rate how well you record individual transactions or select Not applicable.");
        if (TextUtils.isEmpty(changePlan)) errors.add("Please indicate whether you want to make changes.");
        if (changePlan.equals("Yes") && TextUtils.isEmpty(changesExplanation))
            errors.add("Please explain the changes you want to make.");

        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        Map<String, Object> feedback = new HashMap<>();
        feedback.put("videoRating", videoRating);
        feedback.put("clarityRating", clarityRating);
        feedback.put("usefulnessRating", usefulnessRating);
        feedback.put("transactionsRating", transactionsNotApplicable.isChecked() ? "Not applicable" : transactionsRating);
        feedback.put("changePlan", changePlan);
        feedback.put("changesExplanation", changesExplanation.isEmpty() ? "No explanation provided" : changesExplanation);
        feedback.put("comments", comments.isEmpty() ? "No comments provided" : comments);

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

        setResult(RESULT_OK);
        navigateToEndofPart2Activity();
    }

    private void showErrorDialog(List<String> errors) {
        StringBuilder errorMessage = new StringBuilder();
        for (String error : errors) {
            errorMessage.append("• ").append(error).append("\n");
        }

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
                        navigateToEndofPart2Activity();
                    }
                })
                .show();
    }

    private void navigateToEndofPart2Activity() {
        Intent intent = new Intent(AfterVideo8Activity.this, OverallPart2Activity.class);
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
