package com.example.gmh_app.Activities;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.Html;
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
        // Gather user inputs
        String moneyInflows = getSelectedRadioText(moneyInflowsGroup);
        String goodHabits = editTextGoodHabits.getText().toString().trim();
        String easyAdjustment = getSelectedRadioText(easyAdjustmentGroup);
        String changes = editTextChanges.getText().toString().trim();
        String outcome = editTextOutcome.getText().toString().trim();
        String satisfaction = getSelectedRadioText(satisfactionGroup);
        String businessLocation = getSelectedRadioText(businessLocationGroup);

        // Validation
        if (moneyInflows.isEmpty()) {
            showMessageDialog("Error", "Please select an option for 'Money Inflows'.", false);
            return;
        }

        if (moneyInflows.equals("Yes") && goodHabits.isEmpty()) {
            showMessageDialog("Error", "Please describe your good habits since you selected 'Yes' for Money Inflows.", false);
            return;
        }

        if (easyAdjustment.isEmpty()) {
            showMessageDialog("Error", "Please select an option for 'Easy Adjustment'.", false);
            return;
        }

        if (changes.isEmpty()) {
            showMessageDialog("Error", "Please describe your changes.", false);
            return;
        }

        if (outcome.isEmpty()) {
            showMessageDialog("Error", "Please describe the outcome.", false);
            return;
        }

        if (satisfaction.isEmpty()) {
            showMessageDialog("Error", "Please select an option for 'Satisfaction'.", false);
            return;
        }

        if (businessLocation.isEmpty()) {
            showMessageDialog("Error", "Please select an option for 'Business Location'.", false);
            return;
        }

        // Create a response object
        BeforeVideo9Response response = new BeforeVideo9Response(
                moneyInflows, goodHabits, easyAdjustment, changes, outcome, satisfaction, businessLocation
        );

        // Save to Firebase
        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(response)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showMessageDialog("Success", "Responses submitted successfully!", true);
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error.";
                        showMessageDialog("Error", "Failed to submit responses. Please try again.\n\nError: " + error, false);
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
