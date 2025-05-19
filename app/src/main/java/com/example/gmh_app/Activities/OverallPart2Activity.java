package com.example.gmh_app.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gmh_app.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OverallPart2Activity extends AppCompatActivity {

    private RadioGroup changesInflowsGroup, progressGroup, moneyHelpGroup;
    private TextView changesExplained;
    private EditText importantHabits, part2Comments;
    private Button submitButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_overall_part2);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Overall Part 2 Assessment Response");
        databaseReference.keepSynced(true); // Ensures local data is cached offline

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        // Initialize views
        changesInflowsGroup = findViewById(R.id.changes_inflows_group);
        progressGroup = findViewById(R.id.progress_group);
        moneyHelpGroup = findViewById(R.id.money_help_group);
        importantHabits = findViewById(R.id.important_habits);
        part2Comments = findViewById(R.id.part2_comments);
        submitButton = findViewById(R.id.submit_button);
        changesExplained = findViewById(R.id.text_changes_explained);

        changesInflowsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.inflows_yes) {
                changesExplained.setVisibility(View.VISIBLE);
                importantHabits.setVisibility(View.VISIBLE);
            } else {
                changesExplained.setVisibility(View.GONE);
                importantHabits.setVisibility(View.GONE);
            }
        });

        // Set the button click listener
        submitButton.setOnClickListener(v -> validateAndSubmitData());
    }

    private void validateAndSubmitData() {
        // Collect input data
        String changesInflows = getSelectedRadioText(changesInflowsGroup);
        String progress = getSelectedRadioText(progressGroup);
        String moneyHelp = getSelectedRadioText(moneyHelpGroup);
        String importantHabitsText = importantHabits.getText().toString().trim();
        String commentsText = part2Comments.getText().toString().trim();

        // Create a list to hold error messages
        List<String> errors = new ArrayList<>();

        // Validate input
        if (TextUtils.isEmpty(changesInflows)) errors.add("Please select whether there are changes in inflows.");
        if (TextUtils.isEmpty(progress)) errors.add("Please select your progress.");
        if (TextUtils.isEmpty(moneyHelp)) errors.add("Please select if money helped you.");

        // If 'Yes' is selected for inflows, importantHabits must not be empty
        if (changesInflows.equals("Yes") && TextUtils.isEmpty(importantHabitsText)) {
            errors.add("Please explain the important habits if there are changes in inflows.");
        }

        // Show errors if any
        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        // Prepare data
        Map<String, Object> formData = new HashMap<>();
        formData.put("changesInflows", changesInflows);
        formData.put("progress", progress);
        formData.put("moneyHelp", moneyHelp);
        formData.put("importantHabits", importantHabitsText);
        formData.put("comments", TextUtils.isEmpty(commentsText) ? "No comments provided" : commentsText);
        formData.put("timestamp", System.currentTimeMillis());

        // Store data in Firebase (works offline)
        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(formData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Data submitted successfully.");
                    } else {
                        Log.e(TAG, "Error submitting data", task.getException());
                    }
                });

        // Proceed to the next activity immediately
//        setResult(RESULT_OK);
//        finish(); // Close this activity

        navigateToNextActivity();
    }



    private String getSelectedRadioText(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) return "Not Selected";

        RadioButton selectedRadioButton = findViewById(selectedId);
        if (selectedRadioButton == null || selectedRadioButton.getTag() == null) {
            return "Not Selected";
        }

        return selectedRadioButton.getTag().toString();
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

    private void navigateToNextActivity() {
        Intent nextIntent = new Intent(OverallPart2Activity.this, EndofPart2Activity.class);
        startActivity(nextIntent);
        finish(); // Close this activity
    }
}
