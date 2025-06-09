package com.example.gmh_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class AfterVideo6Activity extends AppCompatActivity {

    private static final String TAG = "AfterVideo6Activity";

    // UI components
    private RatingBar ratingVideo, ratingClarity, ratingUsefulness, ratingSeparation;
    private EditText editTextLesson, editTextChanges, editTextComments;
    private TextView changesExplained, reminderNextVideo;
    private ImageView btnBack;
    private RadioGroup changePlanGroup;

    private CheckBox cbHazard1, cbHazard2, cbHazard3, cbNone;
    private ViewGroup hazardGroup;
    private Button buttonSubmit;

    // Firebase reference
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Set the layout
        setContentView(R.layout.activity_after_video6);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback After Video 6");
        databaseReference.keepSynced(true); // Ensures local data is synced when online

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        // Initialize UI components
        ratingVideo = findViewById(R.id.rating_video);
        ratingClarity = findViewById(R.id.rating_clarity);
        ratingUsefulness = findViewById(R.id.rating_usefulness);
        ratingSeparation = findViewById(R.id.rating_separation);
        editTextLesson = findViewById(R.id.editText_lesson);
        editTextChanges = findViewById(R.id.editText_changes);
        editTextComments = findViewById(R.id.editText_comments);
        changePlanGroup = findViewById(R.id.change_plan_group);
        buttonSubmit = findViewById(R.id.button_submit);
        changesExplained = findViewById(R.id.tv_changes_explain);
        hazardGroup = findViewById(R.id.hazardGroup);
        cbHazard1 = findViewById(R.id.hazard1);
        cbHazard2 = findViewById(R.id.hazard2);
        cbHazard3 = findViewById(R.id.hazard3);
        cbNone = findViewById(R.id.hazards_none);
        reminderNextVideo = findViewById(R.id.reminder_next_video);
        btnBack = findViewById(R.id.btn_back);


        CheckBox checkBox1 = findViewById(R.id.hazard1);
        checkBox1.setText(Html.fromHtml(
                "<b>Milking the business</b> – Taking money from the business to pay for household expenses without recording it as a salary advance.",
                Html.FROM_HTML_MODE_LEGACY));

        CheckBox checkBox2 = findViewById(R.id.hazard2);
        checkBox2.setText(Html.fromHtml(
                "<b>Eating your profits</b> – Eating or taking products/stock of your business for you or your family members without paying for it or recording it as a salary advance to you.",
                Html.FROM_HTML_MODE_LEGACY));

        CheckBox checkBox3 = findViewById(R.id.hazard3);
        checkBox3.setText(Html.fromHtml(
                "<b>Milking the household</b> – Taking household money to pay for business expenses without recording it as a cash loan from the household to the business, thus owed to the household.",
                Html.FROM_HTML_MODE_LEGACY));

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
        
        cbNone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cbHazard1.setChecked(false);
                cbHazard2.setChecked(false);
                cbHazard3.setChecked(false);
            }
        });

        CompoundButton.OnCheckedChangeListener otherCheckboxListener = (buttonView, isChecked) -> {
            if (isChecked) {
                cbNone.setChecked(false);
            }
        };

        cbHazard1.setOnCheckedChangeListener(otherCheckboxListener);
        cbHazard2.setOnCheckedChangeListener(otherCheckboxListener);
        cbHazard3.setOnCheckedChangeListener(otherCheckboxListener);

        // Set up the Submit button click listener
        buttonSubmit.setOnClickListener(v -> validateAndSubmitFeedback());
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setResult(RESULT_CANCELED);
//                finish();
//            }
//        });
    }

    private List<String> getSelectedHazardGroup(ViewGroup parent) {
        List<String> selectedOptions = new ArrayList<>();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) child;
                if (checkBox.isChecked()) {
                    selectedOptions.add(checkBox.getText().toString());
                }
            }
        }
        return selectedOptions;
    }
    private void validateAndSubmitFeedback() {
        // Collect input data
        float videoRating = ratingVideo.getRating();
        float clarityRating = ratingClarity.getRating();
        float usefulnessRating = ratingUsefulness.getRating();
        float separationRating = ratingSeparation.getRating();
        String lessonLearned = editTextLesson.getText().toString();
        String changesExplanation = editTextChanges.getText().toString();
        String comments = editTextComments.getText().toString();

        List<String> hazardGroupList = getSelectedHazardGroup(hazardGroup);
        String hazardType = TextUtils.join(", ", hazardGroupList);

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
        if (separationRating == 0) {
            errors.add("Please rate how well you separate business and household money.");
        }
        if (TextUtils.isEmpty(lessonLearned)) {
            errors.add("Please write the biggest lesson you learned.");
        }
        if (hazardGroupList.isEmpty()) errors.add("Please select at least one option for 'Types of Hazard'.");
        if (TextUtils.isEmpty(changePlan)) {
            errors.add("Please indicate whether you want to make changes.");
        }
        if (changePlan.equals("Yes") && TextUtils.isEmpty(changesExplanation)) {
            errors.add("Please explain the changes you want to make.");
        }

        // If there are validation errors, show them in an AlertDialog
        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        // Prepare feedback data
        Map<String, Object> feedback = new HashMap<>();
        feedback.put("videoRating", videoRating);
        feedback.put("clarityRating", clarityRating);
        feedback.put("usefulnessRating", usefulnessRating);
        feedback.put("separationRating", separationRating);
        feedback.put("lessonLearned", lessonLearned);
        feedback.put("changePlan", changePlan);
        feedback.put("hazardType", hazardType);
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
            startActivity(new Intent(AfterVideo6Activity.this, TopicsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(AfterVideo6Activity.this, HelpActivity.class));
            overridePendingTransition(0,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}