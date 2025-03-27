package com.example.gmh_app.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gmh_app.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class OverallPart3Activity extends AppCompatActivity {

    private RadioGroup changesMadeGroup, progressGroup, moneyHelpGroup;
    private EditText changesAdopted, changesPostponed, reasonPostponed, part3Comments;
    private Button submitButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_overall_part3);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Overall Part 3 Response");
        databaseReference.keepSynced(true); // Ensures local data is cached offline

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        // Initialize views
        changesMadeGroup = findViewById(R.id.changes_made_group);
        progressGroup = findViewById(R.id.progress_group);
        moneyHelpGroup = findViewById(R.id.money_help_group);
        changesAdopted = findViewById(R.id.changes_adopted);
        changesPostponed = findViewById(R.id.changes_postponed);
        reasonPostponed = findViewById(R.id.reason_postponed);
        part3Comments = findViewById(R.id.part3_comments);
        submitButton = findViewById(R.id.submit_button);

        // Set button click listener
        submitButton.setOnClickListener(v -> submitDataToFirebase());
    }

    private void submitDataToFirebase() {
        if (!isFormValid()) {
            showDialog("Incomplete Form", "Please fill in all required fields.");
            return;
        }

        Map<String, Object> formData = new HashMap<>();
        formData.put("changes_made", getSelectedRadioText(changesMadeGroup));
        formData.put("progress", getSelectedRadioText(progressGroup));
        formData.put("money_help", getSelectedRadioText(moneyHelpGroup));
        formData.put("changes_adopted", changesAdopted.getText().toString().trim());
        formData.put("changes_postponed", changesPostponed.getText().toString().trim());
        formData.put("reason_postponed", reasonPostponed.getText().toString().trim());
        formData.put("part3_comments", part3Comments.getText().toString().trim());

        // Generate unique ID using timestamp
        String entryId = String.valueOf(System.currentTimeMillis());

        // Save data to Firebase (works offline)
        databaseReference.child(entryId).setValue(formData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Data saved successfully (online or offline)."))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to save data!", e));

        // Move to next activity immediately
        setResult(RESULT_OK);
        navigateToNextActivity();
    }

    private boolean isFormValid() {
        return changesMadeGroup.getCheckedRadioButtonId() != -1 &&
                progressGroup.getCheckedRadioButtonId() != -1 &&
                moneyHelpGroup.getCheckedRadioButtonId() != -1 &&
                !changesAdopted.getText().toString().trim().isEmpty() &&
                !changesPostponed.getText().toString().trim().isEmpty() &&
                !reasonPostponed.getText().toString().trim().isEmpty();
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
        Intent nextIntent = new Intent(OverallPart3Activity.this, EnfofPart3Activity.class);
        startActivity(nextIntent);
        finish();
    }
}
