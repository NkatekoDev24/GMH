package com.example.gmh_app.Activities;

import android.os.Bundle;
import android.util.Log;
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

import com.example.gmh_app.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AfterVideo14Activity extends AppCompatActivity {

    private RatingBar ratingBarVideo;
    private RatingBar ratingBarClarity;
    private RatingBar ratingBarUsefulness;
    private RatingBar ratingBarCurrentHabits;
    private TextView changesExplained;
    private EditText editTextLesson;
    private RadioGroup radioGroupChanges;
    private EditText editTextChanges;
    private EditText editTextComments;
    private Button buttonSubmit;

    private DatabaseReference databaseReference;
    private static final String TAG = "AfterVideo14Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_after_video14);

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

        radioGroupChanges.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_changes_yes) {
                changesExplained.setVisibility(View.VISIBLE);
                editTextChanges.setVisibility(View.VISIBLE);
            } else {
                changesExplained.setVisibility(View.GONE);
                editTextChanges.setVisibility(View.GONE);
            }
        });

        buttonSubmit.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        float videoRating = ratingBarVideo.getRating();
        float clarityRating = ratingBarClarity.getRating();
        float usefulnessRating = ratingBarUsefulness.getRating();
        float currentHabitsRating = ratingBarCurrentHabits.getRating();
        String lessonLearned = editTextLesson.getText().toString().trim();
        String plannedChanges = editTextChanges.getText().toString().trim();
        String comments = editTextComments.getText().toString().trim();

        int selectedChangesId = radioGroupChanges.getCheckedRadioButtonId();
        boolean changesPlanned = (selectedChangesId == R.id.rb_changes_yes);

        if (lessonLearned.isEmpty() || (changesPlanned && plannedChanges.isEmpty())) {
            showMessageDialog("Validation Error", "Please complete all required fields before submitting.", false);
            return;
        }

        Map<String, Object> feedback = new HashMap<>();
        feedback.put("videoRating", videoRating);
        feedback.put("clarityRating", clarityRating);
        feedback.put("usefulnessRating", usefulnessRating);
        feedback.put("currentHabitsRating", currentHabitsRating);
        feedback.put("lessonLearned", lessonLearned);
        feedback.put("changesPlanned", changesPlanned);
        feedback.put("plannedChanges", plannedChanges.isEmpty() ? "No changes provided" : plannedChanges);
        feedback.put("comments", comments.isEmpty() ? "No comments provided" : comments);

        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(feedback)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showMessageDialog("Success", "Feedback submitted successfully!", true);
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error.";
                        Log.e(TAG, "Failed to submit feedback: " + error);
                        showMessageDialog("Error", "Error submitting feedback: " + error, false);
                    }
                });

        setResult(RESULT_OK);
        finish();
    }

    private void showMessageDialog(String title, String message, boolean isSuccess) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (isSuccess) {
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .show();
    }
}
