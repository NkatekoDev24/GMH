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

import java.util.HashMap;
import java.util.Map;

public class BusinessDetailsActivity extends AppCompatActivity {

    private static final String TAG = "BusinessDetailsActivity";

    // UI Components
    private RadioGroup rgRole, rgSalary, rgMoreWorkers, rgFinancialRecords;
    private EditText etPaidEmployees, etUnpaidWorkers;
    private Button btnSubmit;
    private TextView tvCombinedToc, video3;

    // Firebase Database Reference
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_business_details);
        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Before Video 3 Response");
        databaseReference.keepSynced(true); // Ensures local data is synced when online

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        // Initialize UI components
        rgRole = findViewById(R.id.rgRole);
        rgSalary = findViewById(R.id.rgSalary);
        rgMoreWorkers = findViewById(R.id.rgMoreWorkers);
        tvCombinedToc = findViewById(R.id.tvCombinedToc);
        video3 = findViewById(R.id.video3);
        rgFinancialRecords = findViewById(R.id.rgFinancialRecords);
        etPaidEmployees = findViewById(R.id.etPaidEmployees);
        etUnpaidWorkers = findViewById(R.id.etUnpaidWorkers);
        btnSubmit = findViewById(R.id.btnSubmit);

        video3.setText(Html.fromHtml("<b><u>Video 3</u></b>"));

        tvCombinedToc.setText(Html.fromHtml(
                "<b>Part 1. BASICS: Why Good Money Habits – and the Separation rule</b><br>" +
                        "Video 1: Introduction – Why good money habits?<br>" +
                        "Video 2: Making a profit – and not a loss.<br>" +
                        "<span style='color:#00ff00;'><b><u>Video 3: Profit in good & bad weeks: Good decisions & avoiding losses.</b></u></span><br>" +
                        "Video 4: The Separation Rule – Most important for hazard avoidance."
        ));

        // Set OnClickListener for submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitBusinessDetails();
            }
        });
    }

    private void submitBusinessDetails() {
        // Collect data from inputs
        String role = getSelectedRadioValue(rgRole);
        String salary = getSelectedRadioValue(rgSalary);
        String moreWorkers = getSelectedRadioValue(rgMoreWorkers);
        String financialRecords = getSelectedRadioValue(rgFinancialRecords);
        String paidEmployeesInput = etPaidEmployees.getText().toString().trim();
        String unpaidWorkersInput = etUnpaidWorkers.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(role)) {
            showErrorDialog("Please select your role in the business.");
            return;
        }

        if (TextUtils.isEmpty(salary)) {
            showErrorDialog("Please specify if you receive a salary.");
            return;
        }

        if (TextUtils.isEmpty(moreWorkers)) {
            showErrorDialog("Please indicate if you have more workers.");
            return;
        }

        if (TextUtils.isEmpty(financialRecords)) {
            showErrorDialog("Please specify if you keep financial records.");
            return;
        }

        if (!isValidNumber(paidEmployeesInput)) {
            etPaidEmployees.setError("Please enter a valid non-negative number for paid employees.");
            return;
        }

        if (!isValidNumber(unpaidWorkersInput)) {
            etUnpaidWorkers.setError("Please enter a valid non-negative number for unpaid workers.");
            return;
        }

        // Convert numbers with default values
        int paidEmployees = TextUtils.isEmpty(paidEmployeesInput) ? 0 : Integer.parseInt(paidEmployeesInput);
        int unpaidWorkers = TextUtils.isEmpty(unpaidWorkersInput) ? 0 : Integer.parseInt(unpaidWorkersInput);

        // Prepare data to be saved
        Map<String, Object> businessDetails = new HashMap<>();
        businessDetails.put("role", role);
        businessDetails.put("salary", salary);
        businessDetails.put("moreWorkers", moreWorkers);
        businessDetails.put("financialRecords", financialRecords);
        businessDetails.put("paidEmployees", paidEmployees);
        businessDetails.put("unpaidWorkers", unpaidWorkers);

        // Save data to Firebase
        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(businessDetails)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showSuccessDialog();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error.";
                        Log.e(TAG, "Failed to submit business details: " + error);
                        showErrorDialog("Error submitting details: " + error);
                    }
                });

        // Proceed to the next activity immediately
        setResult(RESULT_OK);
        finish(); // Close this activity
    }

    // Helper method to get the selected RadioButton value from a RadioGroup
    private String getSelectedRadioValue(RadioGroup group) {
        int selectedId = group.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            return selectedRadioButton.getText().toString();
        }
        return null;
    }

    // Helper method to validate numeric inputs
    private boolean isValidNumber(String input) {
        if (TextUtils.isEmpty(input)) return true; // Empty is valid (default to 0)
        try {
            int number = Integer.parseInt(input);
            return number >= 0; // Ensure the number is non-negative
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Helper method to show error dialog
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    // Helper method to show success dialog
    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage("Business details submitted successfully!")
                .setPositiveButton("OK", (dialog, which) -> {
                    setResult(RESULT_OK);
                    finish(); // Close activity
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
            startActivity(new Intent(BusinessDetailsActivity.this, TopicsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(BusinessDetailsActivity.this, HelpActivity.class));
            overridePendingTransition(0,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
