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

public class AfterVideo9Activity extends AppCompatActivity {

    private static final String TAG = "AfterVideo9Activity";

    private RatingBar ratingVideo, ratingClarity, ratingUsefulness, ratingMoneyHabits, likingMoneyHabits;
    private TextView changesExplained, tv_reminder;

    private ImageView btnBack;
    private RadioGroup changePlanGroup;
    private EditText editTextLesson, editTextChanges, editTextComments;
    private Button buttonSubmit;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_after_video9);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback After Video 9");
        databaseReference.keepSynced(true); // Ensures local data is synced when online

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);


        ratingVideo = findViewById(R.id.rating_overall);
        ratingClarity = findViewById(R.id.rating_clarity);
        ratingUsefulness = findViewById(R.id.rating_usefulness);
        ratingMoneyHabits = findViewById(R.id.rating_money_habits);
        likingMoneyHabits = findViewById(R.id.liking_money_habits);
        editTextLesson = findViewById(R.id.editText_lesson);
        editTextChanges = findViewById(R.id.editText_changes);
        editTextComments = findViewById(R.id.editText_comments);
        changePlanGroup = findViewById(R.id.change_plan_group);
        buttonSubmit = findViewById(R.id.button_submit);
        changesExplained = findViewById(R.id.tv_changes_explain);
        tv_reminder = findViewById(R.id.tv_reminder);
        btnBack = findViewById(R.id.btn_back);

        changePlanGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.plan_yes) {
                changesExplained.setVisibility(View.VISIBLE);
                editTextChanges.setVisibility(View.VISIBLE);
                tv_reminder.setVisibility(View.VISIBLE);
            } else {
                changesExplained.setVisibility(View.GONE);
                editTextChanges.setVisibility(View.GONE);
                tv_reminder.setVisibility(View.GONE);
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSubmitFeedback();
            }
        });

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
        float moneyHabitsRating = ratingMoneyHabits.getRating();
        float desiredMoneyHabitsRating = likingMoneyHabits.getRating();
        String lessonLearned = editTextLesson.getText().toString();
        String changesExplanation = editTextChanges.getText().toString();
        String comments = editTextComments.getText().toString();

        int selectedChangePlanId = changePlanGroup.getCheckedRadioButtonId();
        String changePlan = selectedChangePlanId == R.id.plan_yes ? "Yes" : selectedChangePlanId == R.id.plan_no ? "No" : "";

        List<String> errors = new ArrayList<>();
        if (videoRating == 0) errors.add("Please rate the video.");
        if (clarityRating == 0) errors.add("Please rate the clarity of the information.");
        if (usefulnessRating == 0) errors.add("Please rate the usefulness of the information.");
        if (moneyHabitsRating == 0) errors.add("Please rate your money habits.");
        if (desiredMoneyHabitsRating == 0) errors.add("Please rate how you’d like your money habits to look.");
        if (TextUtils.isEmpty(lessonLearned)) errors.add("Please write the biggest lesson you learned.");
        if (TextUtils.isEmpty(changePlan)) errors.add("Please indicate whether you want to make changes.");
        if (changePlan.equals("Yes") && TextUtils.isEmpty(changesExplanation)) errors.add("Please explain the changes you want to make.");

        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        Map<String, Object> feedback = new HashMap<>();
        feedback.put("videoRating", videoRating);
        feedback.put("clarityRating", clarityRating);
        feedback.put("usefulnessRating", usefulnessRating);
        feedback.put("moneyHabitsRating", moneyHabitsRating);
        feedback.put("desiredMoneyHabitsRating", desiredMoneyHabitsRating);
        feedback.put("lessonLearned", lessonLearned);
        feedback.put("changePlan", changePlan);
        feedback.put("changesExplanation", changesExplanation.isEmpty() ? "No explanation provided" : changesExplanation);
        feedback.put("comments", comments.isEmpty() ? "No comments provided" : comments);

        Log.d(TAG, "Feedback Data: " + feedback);

        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(feedback)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showSuccessDialog();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error occurred.";
                        Log.e(TAG, "Error submitting feedback: " + error);
                        showErrorDialog("Failed to submit feedback: " + error);
                    }
                });

        // Proceed to the next activity immediately
        setResult(RESULT_OK);
        finish(); // Close this activity
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
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
            startActivity(new Intent(AfterVideo9Activity.this, TopicsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(AfterVideo9Activity.this, HelpActivity.class));
            overridePendingTransition(0,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
