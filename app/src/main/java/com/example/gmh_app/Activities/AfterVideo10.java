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

public class AfterVideo10 extends AppCompatActivity {

    private static final String TAG = "AfterVideo10";

    private RatingBar ratingClarity, ratingUsefulness, ratingMoneyHabits, ratingFutureMoneyHabits;
    private RadioGroup variableCostChangesGroup;
    private TextView changesExplained, tv_reminder;
    private ImageView btnBack;
    private EditText lessonLearnedEditText, changesExplanationEditText, additionalCommentsEditText;
    private Button submitButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_after_video10);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback After Video 10");
        databaseReference.keepSynced(true); // Ensures local data is synced when online

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        ratingClarity = findViewById(R.id.rating_clarity);
        ratingUsefulness = findViewById(R.id.rating_information_usefulness);
        ratingMoneyHabits = findViewById(R.id.rating_money_habits);
        ratingFutureMoneyHabits = findViewById(R.id.rating_future_money_habits);
        lessonLearnedEditText = findViewById(R.id.lesson_learned);
        changesExplanationEditText = findViewById(R.id.changes_explanation);
        additionalCommentsEditText = findViewById(R.id.additional_comments);
        variableCostChangesGroup = findViewById(R.id.variable_cost_changes);
        submitButton = findViewById(R.id.submit_button);
        changesExplained = findViewById(R.id.text_changes_explained);
        tv_reminder = findViewById(R.id.tv_reminder);
        btnBack = findViewById(R.id.btn_back);

        variableCostChangesGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.variable_cost_changes_yes) {
                changesExplained.setVisibility(View.VISIBLE);
                changesExplanationEditText.setVisibility(View.VISIBLE);
                tv_reminder.setVisibility(View.VISIBLE);
            } else {
                changesExplained.setVisibility(View.GONE);
                changesExplanationEditText.setVisibility(View.GONE);
                tv_reminder.setVisibility(View.GONE);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
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
        float clarityRating = ratingClarity.getRating();
        float usefulnessRating = ratingUsefulness.getRating();
        float moneyHabitsRating = ratingMoneyHabits.getRating();
        float futureMoneyHabitsRating = ratingFutureMoneyHabits.getRating();
        String lessonLearned = lessonLearnedEditText.getText().toString();
        String changesExplanation = changesExplanationEditText.getText().toString();
        String additionalComments = additionalCommentsEditText.getText().toString();

        int selectedChangeOptionId = variableCostChangesGroup.getCheckedRadioButtonId();
        String variableCostChange = "";
        if (selectedChangeOptionId == R.id.variable_cost_changes_yes) {
            variableCostChange = "Yes";
        } else if (selectedChangeOptionId == R.id.variable_cost_changes_no) {
            variableCostChange = "No";
        }

        List<String> errors = new ArrayList<>();
        if (clarityRating == 0) errors.add("Please rate the clarity of the information.");
        if (usefulnessRating == 0) errors.add("Please rate the usefulness of the information.");
        if (moneyHabitsRating == 0) errors.add("Please rate your current money habits.");
        if (futureMoneyHabitsRating == 0) errors.add("Please rate how you'd like your money habits to look in 4 weeks.");
        if (TextUtils.isEmpty(lessonLearned)) errors.add("Please provide the biggest lesson you learned.");
        if (TextUtils.isEmpty(variableCostChange)) errors.add("Please indicate whether you want to make changes to handle variable costs.");
        if (variableCostChange.equals("Yes") && TextUtils.isEmpty(changesExplanation)) {
            errors.add("Please explain the changes you want to make.");
        }

        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        Map<String, Object> feedback = new HashMap<>();
        feedback.put("clarityRating", clarityRating);
        feedback.put("usefulnessRating", usefulnessRating);
        feedback.put("moneyHabitsRating", moneyHabitsRating);
        feedback.put("futureMoneyHabitsRating", futureMoneyHabitsRating);
        feedback.put("lessonLearned", lessonLearned);
        feedback.put("variableCostChange", variableCostChange);
        feedback.put("changesExplanation", changesExplanation.isEmpty() ? "No changes explained" : changesExplanation);
        feedback.put("additionalComments", additionalComments.isEmpty() ? "No comments provided" : additionalComments);

        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(feedback)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showSuccessDialog();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error.";
                        Log.e(TAG, "Failed to submit feedback: " + error);
                        showErrorDialog("Error submitting feedback: " + error);
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
            startActivity(new Intent(AfterVideo10.this, TopicsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(AfterVideo10.this, HelpActivity.class));
            overridePendingTransition(0,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
