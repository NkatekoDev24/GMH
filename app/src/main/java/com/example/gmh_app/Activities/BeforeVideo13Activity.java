package com.example.gmh_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
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

public class BeforeVideo13Activity extends AppCompatActivity {

    private static final String TAG = "BeforeVideo13Activity";

    private RadioGroup emergencyFundGroup, planEmergencyFundGroup, changesInProfitCalculationGroup;
    private EditText profitPerMonthInput, changesExplanationInput;
    private Button submitButton;
    private TextView changesExplained, emergencyFollowUp, tvCombinedToc, introductionText;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_before_video13);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Before Video 13 Response");
        databaseReference.keepSynced(true);

        // Initialize UI components
        emergencyFundGroup = findViewById(R.id.emergencyFundGroup);
        planEmergencyFundGroup = findViewById(R.id.planEmergencyFundGroup);
        changesInProfitCalculationGroup = findViewById(R.id.changesInProfitCalculationGroup);
        profitPerMonthInput = findViewById(R.id.profitPerMonthInput);
        changesExplanationInput = findViewById(R.id.changesExplanationInput);
        submitButton = findViewById(R.id.submitButton);
        changesExplained = findViewById(R.id.text_changes_explained);
        emergencyFollowUp = findViewById(R.id.emergencyFundFollowUp);
        introductionText = findViewById(R.id.introductionText);
        tvCombinedToc = findViewById(R.id.tvCombinedToc);

        introductionText.setText(Html.fromHtml("<u>VIDEO 13</U>"));

        tvCombinedToc.setText(Html.fromHtml(
                "<b>Part 4. Counting and Recording PROFIT – and the risk of customer credit</b><br>" +
                        "Video 12: Calculating Profit correctly.<br>" +
                        "<span style='color:#00ff00;'><b><u>Video 13: Must I use gross profit or net profit for management purposes?</b></u></span><br>" +
                        "Video 14: The risk of customer credit - another hazard.<br>" +
                        "Video 15: Revenue, costs & profits – a complete weekly example with numbers."
        ));

        emergencyFundGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.emergencyFundNo) {
                emergencyFollowUp.setVisibility(View.VISIBLE);
                planEmergencyFundGroup.setVisibility(View.VISIBLE);
            } else {
                emergencyFollowUp.setVisibility(View.GONE);
                planEmergencyFundGroup.setVisibility(View.GONE);
            }
        });

        changesInProfitCalculationGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.changesInProfitYes) {
                changesExplained.setVisibility(View.VISIBLE);
                changesExplanationInput.setVisibility(View.VISIBLE);
            } else {
                changesExplained.setVisibility(View.GONE);
                changesExplanationInput.setVisibility(View.GONE);
            }
        });

        submitButton.setOnClickListener(view -> validateAndSubmitResponses());
    }

    private void validateAndSubmitResponses() {
        List<String> errors = new ArrayList<>();

        String emergencyFund = getSelectedRadioButtonText(emergencyFundGroup);
        String planEmergencyFund = getSelectedRadioButtonText(planEmergencyFundGroup);
        String changesInProfitCalculation = getSelectedRadioButtonText(changesInProfitCalculationGroup);
        String profitPerMonth = profitPerMonthInput.getText().toString().trim();
        String changesExplanation = changesExplanationInput.getText().toString().trim();

        if (TextUtils.isEmpty(emergencyFund)) errors.add("Please select an option for 'Do you have an emergency fund?'.");
        if (planEmergencyFundGroup.getVisibility() == View.VISIBLE && TextUtils.isEmpty(planEmergencyFund))
            errors.add("Please select an option for 'Do you plan to create an emergency fund?'.");
        if (TextUtils.isEmpty(changesInProfitCalculation))
            errors.add("Please select an option for 'Have you made changes in your profit calculation?'.");
        if (TextUtils.isEmpty(profitPerMonth)) errors.add("Please enter your profit per month.");
        if (!isNumeric(profitPerMonth)) errors.add("Profit per month must be a valid numeric value.");
        if (changesInProfitCalculation.equals("Yes") && TextUtils.isEmpty(changesExplanation))
            errors.add("Please explain the changes made in profit calculation.");

        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        // Prepare data to save in Firebase
        Map<String, Object> response = new HashMap<>();
        response.put("emergencyFund", emergencyFund);
        response.put("planEmergencyFund", planEmergencyFund);
        response.put("changesInProfitCalculation", changesInProfitCalculation);
        response.put("profitPerMonth", profitPerMonth);
        response.put("changesExplanation", TextUtils.isEmpty(changesExplanation) ? "No explanation provided" : changesExplanation);
        response.put("timestamp", System.currentTimeMillis());

        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(response)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Responses submitted successfully.");
                    } else {
                        Log.e(TAG, "Error submitting responses", task.getException());
                    }
                });

        setResult(RESULT_OK);
        finish();
    }

    private String getSelectedRadioButtonText(RadioGroup group) {
        int selectedId = group.getCheckedRadioButtonId();
        if (selectedId == -1) {
            return "";
        }
        RadioButton selectedRadioButton = findViewById(selectedId);
        return selectedRadioButton.getText().toString();
    }

    private void showErrorDialog(List<String> errors) {
        StringBuilder errorMessage = new StringBuilder();
        for (String error : errors) {
            errorMessage.append("• ").append(error).append("\n");
        }

        new AlertDialog.Builder(this)
                .setTitle("Errors")
                .setMessage(errorMessage.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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
            startActivity(new Intent(BeforeVideo13Activity.this, TopicsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(BeforeVideo13Activity.this, HelpActivity.class));
            overridePendingTransition(0,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
