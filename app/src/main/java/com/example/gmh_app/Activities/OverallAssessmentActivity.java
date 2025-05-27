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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

        // Setup Toolbar
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
//            getSupportActionBar().setTitle("");
//        }

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
        submitButton.setOnClickListener(v -> validateAndSubmitAssessment());
    }

    private void validateAndSubmitAssessment() {
        // Collect input data
        String videos = videosWatched.getText().toString().trim();
        String weeks = weeksWatched.getText().toString().trim();
        String profitAmount = profitWatched.getText().toString().trim();
        String netProfitAmount = netProfitWatched.getText().toString().trim();
        String additionalEmployees = yesWatched.getText().toString().trim();
        String totalPaidEmployees = paidWatched.getText().toString().trim();

        // Get selected radio button values
        String benefit = getSelectedRadioText(benefitGroup);
        String confidence = getSelectedRadioText(confidenceGroup);
        String moneyManagementChanges = getSelectedRadioText(moneyManagementChangesGroup);
        String progressSkills = getSelectedRadioText(progressSkillsGroup);
        String control = getSelectedRadioText(controlGroup);
        String plan = getSelectedRadioText(planGroup);
        String profitIncrease = getSelectedRadioText(profitIncreaseGroup);
        String changesMade = getSelectedRadioText(changesMadeGroup);
        String mainChange = mainChanges.getText().toString().trim();
        String finalComment = finalComments.getText().toString().trim();

        // Create a list to hold error messages
        List<String> errors = new ArrayList<>();

        // Validate inputs
        if (TextUtils.isEmpty(videos)) errors.add("Please enter how many videos you have watched.");
        if (TextUtils.isEmpty(weeks)) errors.add("Please enter the number of weeks you watched the videos.");
        if (benefit == null) errors.add("Please select how much you benefited from the videos.");
        if (confidence == null) errors.add("Please select if you feel more confident in your business.");
        if (moneyManagementChanges == null) errors.add("Please select if the videos changed your money management practices.");

        if (profitWatched.getVisibility() == View.VISIBLE) {
            if (TextUtils.isEmpty(profitAmount)) errors.add("Please enter the profit increase amount.");
            if (!isDecimal(profitAmount)) errors.add("Please enter a valid decimal amount for profit increase.");
        }

        if (!isDecimal(netProfitAmount)) errors.add("Please enter a valid decimal amount for net profit.");
        if (yesWatched.getVisibility() == View.VISIBLE && !isInteger(additionalEmployees)) {
            errors.add("Please enter a valid integer for additional employees.");
        }
        if (!isInteger(totalPaidEmployees)) errors.add("Please enter a valid integer for total paid employees.");

        // Show errors if any
        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        // Prepare data to be saved
        Map<String, Object> assessmentData = new HashMap<>();
        assessmentData.put("videosWatched", videos);
        assessmentData.put("weeksWatched", weeks);
        assessmentData.put("benefit", benefit);
        assessmentData.put("confidence", confidence);
        assessmentData.put("moneyManagementChanges", moneyManagementChanges);
        assessmentData.put("progressSkills", progressSkills);
        assessmentData.put("control", control);
        assessmentData.put("plan", plan);
        assessmentData.put("profitIncrease", profitIncrease);
        assessmentData.put("changesMade", changesMade);
        assessmentData.put("mainChanges", mainChange);
        assessmentData.put("finalComments", finalComment);
        assessmentData.put("profitIncreaseAmount", profitAmount);
        assessmentData.put("netProfitNow", netProfitAmount);
        assessmentData.put("additionalEmployees", additionalEmployees);
        assessmentData.put("totalPaidEmployees", totalPaidEmployees);
        assessmentData.put("timestamp", System.currentTimeMillis());

        // Save data to Firebase
        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(assessmentData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Assessment submitted successfully.");
//                        navigateToGMHBonusActivity();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error.";
                        Log.e(TAG, "Error submitting assessment: " + error);
                    }
                });

        setResult(RESULT_OK);
        navigateToGMHBonusActivity();
    }


    // Navigate to GMHBonusActivity
    private void navigateToGMHBonusActivity() {
        Intent intent = new Intent(OverallAssessmentActivity.this, EndofPart4Activity.class);
        startActivity(intent);
        finish(); // Close this activity
    }

    // Helper method to validate required fields

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == android.R.id.home) {
//            setResult(RESULT_CANCELED);
//            finish();
//            return true;
//        } else if (id == R.id.action_home) {
//            startActivity(new Intent(OverallAssessmentActivity.this, TopicsActivity.class));
//            overridePendingTransition(0, 0);
//            return true;
//        } else if (id == R.id.action_help) {
//            startActivity(new Intent(OverallAssessmentActivity.this, HelpActivity.class));
//            overridePendingTransition(0,0);
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
