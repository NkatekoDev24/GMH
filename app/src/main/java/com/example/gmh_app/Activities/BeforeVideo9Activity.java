package com.example.gmh_app.Activities;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.Html;
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

import com.example.gmh_app.Classes.BeforeVideo9Response;
import com.example.gmh_app.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeforeVideo9Activity extends AppCompatActivity {

    private TextView titleTextView, tvCombinedToc, video9IntroTextView, tvChangesExplain, tvChangesExplain2, tvChangesExplain3, encouragementTextView;
    private RadioGroup moneyInflowsGroup, easyAdjustmentGroup, satisfactionGroup, businessLocationGroup;
    private EditText editTextGoodHabits, editTextChanges, editTextOutcome;
    private Button btnSubmit;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_before_video9);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Before Video 9 Response");
        databaseReference.keepSynced(true); // Ensures local data is synced when online

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        // Initialize views
        titleTextView = findViewById(R.id.titleTextView);
        tvCombinedToc = findViewById(R.id.tvCombinedToc);
        video9IntroTextView = findViewById(R.id.video9IntroTextView);
        moneyInflowsGroup = findViewById(R.id.moneyInflowsGroup);
        tvChangesExplain = findViewById(R.id.tv_changes_explain);
        editTextGoodHabits = findViewById(R.id.editTextGoodHabits);
        easyAdjustmentGroup = findViewById(R.id.easyAdjustmentGroup);
        tvChangesExplain2 = findViewById(R.id.tv_changes_explain2);
        editTextChanges = findViewById(R.id.editTextChanges);
        tvChangesExplain3 = findViewById(R.id.tv_changes_explain3);
        editTextOutcome = findViewById(R.id.editTextOutcome);
        satisfactionGroup = findViewById(R.id.satisfactionGroup);
        encouragementTextView = findViewById(R.id.encouragementTextView);
        businessLocationGroup = findViewById(R.id.businessLocationGroup);
        btnSubmit = findViewById(R.id.button_submit);

        // Set text content
        titleTextView.setText(Html.fromHtml("<u>PART 3 START PAGE</u>"));
        tvCombinedToc.setText(Html.fromHtml(
                "<b>Part 3. Counting and Recording Money OUTFLOWS (EXPENSES)</b><br>" +
                        "<span style='color:#00ff00;'><b><u>Video 9: Correctly counting and recording Money Outflows: Variable costs versus Fixed costs.</b></u></span><br>" +
                        "Video 10: Correctly counting and recording stock purchases and other variable costs.<br>" +
                        "Video 11: Fixed costs / Monthly basic costs / Overhead costs."
        ));
        video9IntroTextView.setText(Html.fromHtml("<u>VIDEO 9</u>"));

        moneyInflowsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.inflows_yes) {
                tvChangesExplain.setVisibility(View.VISIBLE);
                editTextGoodHabits.setVisibility(View.VISIBLE);
            } else {
                tvChangesExplain.setVisibility(View.GONE);
                editTextGoodHabits.setVisibility(View.GONE);
            }
        });

        // Set up submit button listener
        btnSubmit.setOnClickListener(v -> submitResponses());
    }

    private void submitResponses() {
        // Collect input data
        String moneyInflows = getSelectedRadioText(moneyInflowsGroup);
        String goodHabits = editTextGoodHabits.getText().toString().trim();
        String easyAdjustment = getSelectedRadioText(easyAdjustmentGroup);
        String changes = editTextChanges.getText().toString().trim();
        String outcome = editTextOutcome.getText().toString().trim();
        String satisfaction = getSelectedRadioText(satisfactionGroup);
        String businessLocation = getSelectedRadioText(businessLocationGroup);

        // Create a list to hold error messages
        List<String> errors = new ArrayList<>();

        // Validate input
        if (TextUtils.isEmpty(moneyInflows)) errors.add("Please select an option for 'Money Inflows'.");
        if ("Yes".equals(moneyInflows) && TextUtils.isEmpty(goodHabits)) {
            errors.add("Please describe your good habits since you selected 'Yes' for Money Inflows.");
        }
        if (TextUtils.isEmpty(easyAdjustment)) errors.add("Please select an option for 'Easy Adjustment'.");
        if (TextUtils.isEmpty(changes)) errors.add("Please describe your changes.");
        if (TextUtils.isEmpty(outcome)) errors.add("Please describe the outcome.");
        if (TextUtils.isEmpty(satisfaction)) errors.add("Please select an option for 'Satisfaction'.");
        if (TextUtils.isEmpty(businessLocation)) errors.add("Please select an option for 'Business Location'.");

        // Show errors if any
        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        // Create response data
        Map<String, Object> response = new HashMap<>();
        response.put("moneyInflows", moneyInflows);
        response.put("goodHabits", TextUtils.isEmpty(goodHabits) ? "Not provided" : goodHabits);
        response.put("easyAdjustment", easyAdjustment);
        response.put("changes", changes);
        response.put("outcome", outcome);
        response.put("satisfaction", satisfaction);
        response.put("businessLocation", businessLocation);
        response.put("timestamp", System.currentTimeMillis());

        // Save to Firebase
        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(response)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Responses submitted successfully.");
                        showMessageDialog("Success", "Responses submitted successfully!", true);
                    } else {
                        Log.e(TAG, "Error submitting responses", task.getException());
                        showMessageDialog("Error", "Failed to submit responses. Please try again.", false);
                    }
                });

        // Proceed to the next activity immediately
        setResult(RESULT_OK);
        finish(); // Close this activity
    }

    private String getSelectedRadioText(RadioGroup group) {
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
}
