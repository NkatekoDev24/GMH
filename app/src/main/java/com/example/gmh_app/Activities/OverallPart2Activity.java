package com.example.gmh_app.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;
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
        submitButton.setOnClickListener(v -> submitDataToFirebase());
    }

    private void submitDataToFirebase() {
        // Validate form inputs
        if (!isFormValid()) {
            showDialog("Incomplete Form", "Please complete all required fields.");
            return;
        }

        // Prepare data
        Map<String, Object> formData = new HashMap<>();
        formData.put("changes_inflows", getSelectedRadioText(changesInflowsGroup));
        formData.put("progress", getSelectedRadioText(progressGroup));
        formData.put("money_help", getSelectedRadioText(moneyHelpGroup));
        formData.put("important_habits", importantHabits.getText().toString().trim());
        formData.put("part2_comments", part2Comments.getText().toString().trim());

        // Generate a unique entry ID using timestamp
        String entryId = String.valueOf(System.currentTimeMillis());

        // Store data in Firebase (works offline)
        databaseReference.child(entryId).setValue(formData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Data saved successfully (online or offline)."))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to save data!", e));

        // Proceed to the next activity immediately
        setResult(RESULT_OK);
        navigateToNextActivity();
    }

    private boolean isFormValid() {
        boolean isValid = true;

        if (changesInflowsGroup.getCheckedRadioButtonId() == -1) {
            showDialog("Error", "Please select whether there are changes in inflows.");
            isValid = false;
        } else {
            int inflowsCheckedId = changesInflowsGroup.getCheckedRadioButtonId();
            if (inflowsCheckedId == R.id.inflows_yes && importantHabits.getText().toString().trim().isEmpty()) {
                showDialog("Error", "Please explain the important habits if there are changes in inflows.");
                isValid = false;
            }
        }

        if (progressGroup.getCheckedRadioButtonId() == -1) {
            showDialog("Error", "Please select your progress.");
            isValid = false;
        }

        if (moneyHelpGroup.getCheckedRadioButtonId() == -1) {
            showDialog("Error", "Please select if money helped you.");
            isValid = false;
        }

        return isValid;
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

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void navigateToNextActivity() {
        Intent nextIntent = new Intent(OverallPart2Activity.this, EndofPart2Activity.class);
        startActivity(nextIntent);
        finish(); // Close this activity
    }
}
