package com.example.gmh_app.Activities;

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

import java.util.HashMap;
import java.util.Map;

public class OverallAssessmentActivity extends AppCompatActivity {

    private static final String TAG = "OverallAssessmentActivity";

    private EditText videosWatched, weeksWatched, mainChanges, finalComments, profitWatched, netProfitWatched, yesWatched, paidWatched;
    private TextView changesExplained, profitGrow, changesMadeExplained;
    private RadioGroup benefitGroup, confidenceGroup, moneyManagementChangesGroup, progressSkillsGroup, controlGroup, planGroup, profitIncreaseGroup, changesMadeGroup;
    private Button submitButton;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_overall_assessment);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Overall Assessment Response");
        databaseReference.keepSynced(true); // Ensures local data is synced when online

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        // Initialize UI components
        videosWatched = findViewById(R.id.videos_watched);
        weeksWatched = findViewById(R.id.weeks_watched);
        benefitGroup = findViewById(R.id.benefit_group);
        confidenceGroup = findViewById(R.id.comfortable_groups);
        moneyManagementChangesGroup = findViewById(R.id.changed_group);
        progressSkillsGroup = findViewById(R.id.progress_skills_group);
        controlGroup = findViewById(R.id.control_group);
        planGroup = findViewById(R.id.plan_group);
        profitIncreaseGroup = findViewById(R.id.profit_increase_group);
        changesMadeGroup = findViewById(R.id.changes_made_group);
        mainChanges = findViewById(R.id.changed_watched);
        finalComments = findViewById(R.id.final_comments);
        profitWatched = findViewById(R.id.profit_watched);
        netProfitWatched = findViewById(R.id.net_profit_watched);
        yesWatched = findViewById(R.id.yes_watched);
        paidWatched = findViewById(R.id.paid_watched);
        submitButton = findViewById(R.id.submit_button);
        changesExplained = findViewById(R.id.text_changes_explained);
        profitGrow = findViewById(R.id.profit_grow);
        changesMadeExplained = findViewById(R.id.changes_explained);

        moneyManagementChangesGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.changed_not_at_all){
                changesMadeExplained.setVisibility(View.GONE);
                mainChanges.setVisibility(View.GONE);
            }else{
                changesMadeExplained.setVisibility(View.VISIBLE);
                mainChanges.setVisibility(View.VISIBLE);
            }
        });

        changesMadeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.changes_yes) {
                changesExplained.setVisibility(View.VISIBLE);
                yesWatched.setVisibility(View.VISIBLE);
            } else {
                changesExplained.setVisibility(View.GONE);
                yesWatched.setVisibility(View.GONE);
            }
        });

        profitIncreaseGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.profit_not_at_all){
                profitGrow.setVisibility(View.GONE);
                profitWatched.setVisibility(View.GONE);
            }else{
                profitGrow.setVisibility(View.VISIBLE);
                profitWatched.setVisibility(View.VISIBLE);
            }
        });

        // Set submit button click listener
        submitButton.setOnClickListener(v -> submitAssessment());
    }

    private void submitAssessment() {
        // Validate required fields
        if (!isFormValid()) {
            return;
        }

        // Prepare data to be saved
        Map<String, Object> assessmentData = new HashMap<>();
        assessmentData.put("videosWatched", videosWatched.getText().toString().trim());
        assessmentData.put("weeksWatched", weeksWatched.getText().toString().trim());
        assessmentData.put("benefit", getSelectedRadioText(benefitGroup));
        assessmentData.put("confidence", getSelectedRadioText(confidenceGroup));
        assessmentData.put("moneyManagementChanges", getSelectedRadioText(moneyManagementChangesGroup));
        assessmentData.put("progressSkills", getSelectedRadioText(progressSkillsGroup));
        assessmentData.put("control", getSelectedRadioText(controlGroup));
        assessmentData.put("plan", getSelectedRadioText(planGroup));
        assessmentData.put("profitIncrease", getSelectedRadioText(profitIncreaseGroup));
        assessmentData.put("changesMade", getSelectedRadioText(changesMadeGroup));
        assessmentData.put("mainChanges", mainChanges.getText().toString().trim());
        assessmentData.put("finalComments", finalComments.getText().toString().trim());
        assessmentData.put("profitIncreaseAmount", profitWatched.getText().toString().trim());
        assessmentData.put("netProfitNow", netProfitWatched.getText().toString().trim());
        assessmentData.put("additionalEmployees", yesWatched.getText().toString().trim());
        assessmentData.put("totalPaidEmployees", paidWatched.getText().toString().trim());

        // Save data to Firebase
        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(assessmentData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        navigateToGMHBonusActivity();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error.";
                        Log.e(TAG, "Error submitting data: " + error);
                        showErrorDialog("Error submitting data: " + error);
                    }
                });

        setResult(RESULT_OK);
        navigateToGMHBonusActivity();
    }

    // Navigate to GMHBonusActivity
    private void navigateToGMHBonusActivity() {
        Intent intent = new Intent(this, GmhBonusActivity.class);
        startActivity(intent);
        finish(); // Close this activity
    }

    // Helper method to validate required fields
    private boolean isFormValid() {
        // Enforce numeric input types
        videosWatched.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        weeksWatched.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        profitWatched.setInputType(android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_CLASS_NUMBER);
        netProfitWatched.setInputType(android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_CLASS_NUMBER);
        yesWatched.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        paidWatched.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        if (TextUtils.isEmpty(videosWatched.getText().toString().trim())) {
            showErrorDialog("Please enter how many videos you have watched.");
            return false;
        }
        if (TextUtils.isEmpty(weeksWatched.getText().toString().trim())) {
            showErrorDialog("Please enter the number of weeks you watched the videos.");
            return false;
        }
        if (benefitGroup.getCheckedRadioButtonId() == -1) {
            showErrorDialog("Please select how much you benefited from the videos.");
            return false;
        }
        if (confidenceGroup.getCheckedRadioButtonId() == -1) {
            showErrorDialog("Please select if you feel more confident in your business.");
            return false;
        }
        if (moneyManagementChangesGroup.getCheckedRadioButtonId() == -1) {
            showErrorDialog("Please select if the videos changed your money management practices.");
            return false;
        }
        if (profitWatched.getVisibility() == View.VISIBLE) {
            if (profitWatched.getText().toString().trim().isEmpty()) {
                showErrorDialog("Please enter the profit increase amount.");
                return false;
            }
            if (!isDecimal(profitWatched.getText().toString().trim())) {
                showErrorDialog("Please enter a valid decimal amount for profit increase.");
                return false;
            }
        }
        if (!isDecimal(netProfitWatched.getText().toString().trim())) {
            showErrorDialog("Please enter a valid decimal amount for net profit.");
            return false;
        }
        if (yesWatched.getVisibility() == View.VISIBLE && !isInteger(yesWatched.getText().toString().trim())) {
            showErrorDialog("Please enter a valid integer for additional employees.");
            return false;
        }
        if (!isInteger(paidWatched.getText().toString().trim())) {
            showErrorDialog("Please enter a valid integer for total paid employees.");
            return false;
        }
        return true;
    }

    // Helper method to get selected value from a RadioGroup
    private String getSelectedRadioText(RadioGroup group) {
        int selectedId = group.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            return selectedRadioButton.getText().toString();
        }
        return null;
    }

    // Helper method to show an error dialog
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private boolean isDecimal(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Helper method to check if a string is a valid integer
    private boolean isInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
