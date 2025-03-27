package com.example.gmh_app.Activities;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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

public class BusinessPracticesActivity extends AppCompatActivity {

    private static final String TAG = "BusinessPracticesActivity";

    // UI Components
    private RadioGroup rgSeparateFinances, rgTakeCash, rgWriteDownCash, rgConsumeProducts, rgWriteDownConsume;
    private TextView tvCashFollowUp, tvConsumeFollowUp, tvCombinedToc, video4;
    private Button btnSubmit;

    // Firebase Database Reference
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_business_practices);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Before Video 4 Response");
        databaseReference.keepSynced(true); // Ensures local data is synced when online

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        // Initialize UI components
        rgSeparateFinances = findViewById(R.id.rgSeparateFinances);
        rgTakeCash = findViewById(R.id.rgTakeCash);
        rgWriteDownCash = findViewById(R.id.rgWriteDownCash);
        rgConsumeProducts = findViewById(R.id.rgConsumeProducts);
        rgWriteDownConsume = findViewById(R.id.rgWriteDownConsume);

        tvCashFollowUp = findViewById(R.id.tvCashFollowUp);
        tvConsumeFollowUp = findViewById(R.id.tvConsumeFollowUp);
        tvCombinedToc = findViewById(R.id.tvCombinedToc);
        video4 = findViewById(R.id.video4);

        btnSubmit = findViewById(R.id.btnSubmit);

        // Handle visibility for cash-related follow-up question
        rgTakeCash.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbTakeCashYes) {
                tvCashFollowUp.setVisibility(View.VISIBLE);
                rgWriteDownCash.setVisibility(View.VISIBLE);
            } else {
                tvCashFollowUp.setVisibility(View.GONE);
                rgWriteDownCash.setVisibility(View.GONE);
            }
        });

        // Handle visibility for consume products-related follow-up question
        rgConsumeProducts.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbConsumeProductsYes) {
                tvConsumeFollowUp.setVisibility(View.VISIBLE);
                rgWriteDownConsume.setVisibility(View.VISIBLE);
            } else {
                tvConsumeFollowUp.setVisibility(View.GONE);
                rgWriteDownConsume.setVisibility(View.GONE);
            }
        });

        // Set text for combined TOC and Video 4 header
        tvCombinedToc.setText(Html.fromHtml(
                "<b>Part 1. BASICS: Why Good Money Habits – and the Separation rule</b><br>" +
                        "Video 1: Introduction – Why good money habits?<br>" +
                        "Video 2: Making a profit – and not a loss.<br>" +
                        "Video 3: Profit in good & bad weeks: Good decisions & avoiding losses.<br>" +
                        "<span style='color:#00ff00;'><b><u>Video 4: The Separation Rule – Most important for hazard avoidance.</b></u></span>"
        ));
        video4.setText(Html.fromHtml("<u>VIDEO 4</u>"));

        // Submit button click listener
        btnSubmit.setOnClickListener(v -> validateAndSubmit());
    }

    private void validateAndSubmit() {
        boolean isValid = validateRadioGroup(rgSeparateFinances, "Please specify if you separate business and personal finances.");

        if (!validateRadioGroup(rgTakeCash, "Please specify if you take cash from the business.")) {
            isValid = false;
        }

        if (tvCashFollowUp.getVisibility() == View.VISIBLE &&
                !validateRadioGroup(rgWriteDownCash, "Please specify if you write down cash taken from the business.")) {
            isValid = false;
        }

        if (!validateRadioGroup(rgConsumeProducts, "Please specify if you consume products from the business.")) {
            isValid = false;
        }

        if (tvConsumeFollowUp.getVisibility() == View.VISIBLE &&
                !validateRadioGroup(rgWriteDownConsume, "Please specify if you write down consumed products.")) {
            isValid = false;
        }

        if (!isValid) {
            return; // Stop submission if validation fails
        }

        // Prepare data for Firebase
        Map<String, Object> businessPractices = new HashMap<>();
        businessPractices.put("separateFinances", getSelectedRadioValue(rgSeparateFinances));
        businessPractices.put("takeCash", getSelectedRadioValue(rgTakeCash));
        businessPractices.put("writeDownCash", (tvCashFollowUp.getVisibility() == View.VISIBLE) ? getSelectedRadioValue(rgWriteDownCash) : "Not applicable");
        businessPractices.put("consumeProducts", getSelectedRadioValue(rgConsumeProducts));
        businessPractices.put("writeDownConsume", (tvConsumeFollowUp.getVisibility() == View.VISIBLE) ? getSelectedRadioValue(rgWriteDownConsume) : "Not applicable");

        // Save data to Firebase
        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(businessPractices)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showSuccessDialog();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error.";
                        Log.e(TAG, "Failed to submit business practices: " + error);
                        showErrorDialog("Error submitting data: " + error);
                    }
                });

        // Proceed to the next activity immediately
        setResult(RESULT_OK);
        finish(); // Close this activity
    }

    // Helper method to validate a RadioGroup
    private boolean validateRadioGroup(RadioGroup group, String errorMessage) {
        if (group.getCheckedRadioButtonId() == -1) {
            showErrorDialog(errorMessage);
            return false;
        }
        return true;
    }

    // Helper method to get selected value from a RadioGroup
    private String getSelectedRadioValue(RadioGroup group) {
        int selectedId = group.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedButton = findViewById(selectedId);
            return selectedButton.getText().toString();
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

    // Helper method to show success dialog
    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage("Business Practices submitted successfully!")
                .setPositiveButton("OK", (dialog, which) -> {
                    setResult(RESULT_OK);
                    finish(); // Close activity
                })
                .show();
    }
}
