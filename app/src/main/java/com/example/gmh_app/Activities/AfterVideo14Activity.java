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

public class AfterVideo14Activity extends AppCompatActivity {

    private static final String TAG = "AfterVideo14Activity";

    private RatingBar ratingBarVideo, ratingBarClarity, ratingBarUsefulness, ratingBarCurrentHabits;
    private EditText editTextLesson, editTextChanges, editTextComments;
    private ImageView btnBack;
    private RadioGroup radioGroupChanges;
    private Button buttonSubmit;
    private TextView changesExplained, reminderNextVideo;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_after_video14);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback After Video 14");
        databaseReference.keepSynced(true);

        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        ratingBarVideo = findViewById(R.id.ratingBarVideo);
        ratingBarClarity = findViewById(R.id.ratingBarClarity);
        ratingBarUsefulness = findViewById(R.id.ratingBarUsefulness);
        ratingBarCurrentHabits = findViewById(R.id.current_habits_rating);
        editTextLesson = findViewById(R.id.et_lesson);
        radioGroupChanges = findViewById(R.id.rg_changes);
        editTextChanges = findViewById(R.id.et_changes);
        editTextComments = findViewById(R.id.et_comments);
        buttonSubmit = findViewById(R.id.btn_submit);
        changesExplained = findViewById(R.id.text_changes_explained);
        reminderNextVideo = findViewById(R.id.reminder_next_video);
        btnBack = findViewById(R.id.btn_back);

        radioGroupChanges.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_changes_yes) {
                changesExplained.setVisibility(View.VISIBLE);
                editTextChanges.setVisibility(View.VISIBLE);
                reminderNextVideo.setVisibility(View.VISIBLE);
            } else {
                changesExplained.setVisibility(View.GONE);
                editTextChanges.setVisibility(View.GONE);
                reminderNextVideo.setVisibility(View.GONE);
            }
        });

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
        float currentHabitsRating = ratingBarCurrentHabits.getRating();
        String lessonLearned = editTextLesson.getText().toString().trim();
        String plannedChanges = editTextChanges.getText().toString().trim();
        String comments = editTextComments.getText().toString().trim();

        int selectedChangesId = radioGroupChanges.getCheckedRadioButtonId();
        boolean changesPlanned = (selectedChangesId == R.id.rb_changes_yes);

        List<String> errors = new ArrayList<>();

        if (videoRating == 0) errors.add("Please rate the video.");
        if (clarityRating == 0) errors.add("Please rate the clarity of the video.");
        if (usefulnessRating == 0) errors.add("Please rate the usefulness of the video.");
        if (currentHabitsRating == 0) errors.add("Please rate your current habits.");
        if (TextUtils.isEmpty(lessonLearned)) errors.add("Please describe the biggest lesson you learned.");
        if (changesPlanned && TextUtils.isEmpty(plannedChanges)) errors.add("Please describe the changes you plan to make.");

        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        Map<String, Object> feedback = new HashMap<>();
        feedback.put("videoRating", videoRating);
        feedback.put("clarityRating", clarityRating);
        feedback.put("usefulnessRating", usefulnessRating);
        feedback.put("currentHabitsRating", currentHabitsRating);
        feedback.put("lessonLearned", lessonLearned);
        feedback.put("changesPlanned", changesPlanned);
        feedback.put("plannedChanges", TextUtils.isEmpty(plannedChanges) ? "No changes provided" : plannedChanges);
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
            startActivity(new Intent(AfterVideo14Activity.this, TopicsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(AfterVideo14Activity.this, HelpActivity.class));
            overridePendingTransition(0,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
