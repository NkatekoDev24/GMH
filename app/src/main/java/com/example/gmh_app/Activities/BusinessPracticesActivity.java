package com.example.gmh_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

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
        btnSubmit.setOnClickListener(v -> validateAndSubmitBusinessPractices());
    }

    private void validateAndSubmitBusinessPractices() {
        // Collect input data
        String separateFinances = getSelectedRadioValue(rgSeparateFinances);
        String takeCash = getSelectedRadioValue(rgTakeCash);
        String writeDownCash = (tvCashFollowUp.getVisibility() == View.VISIBLE) ? getSelectedRadioValue(rgWriteDownCash) : "Not applicable";
        String consumeProducts = getSelectedRadioValue(rgConsumeProducts);
        String writeDownConsume = (tvConsumeFollowUp.getVisibility() == View.VISIBLE) ? getSelectedRadioValue(rgWriteDownConsume) : "Not applicable";

        // Create a list to hold error messages
        List<String> errors = new ArrayList<>();

        // Validate input
        if (validateRadioGroup(rgSeparateFinances, "Please specify if you separate business and personal finances.")) errors.add("Please specify if you separate business and personal finances.");
        if (validateRadioGroup(rgTakeCash, "Please specify if you take cash from the business.")) errors.add("Please specify if you take cash from the business.");
        if (tvCashFollowUp.getVisibility() == View.VISIBLE && validateRadioGroup(rgWriteDownCash, "Please specify if you write down cash taken from the business.")) errors.add("Please specify if you write down cash taken from the business.");
        if (validateRadioGroup(rgConsumeProducts, "Please specify if you consume products from the business.")) errors.add("Please specify if you consume products from the business.");
        if (tvConsumeFollowUp.getVisibility() == View.VISIBLE && validateRadioGroup(rgWriteDownConsume, "Please specify if you write down consumed products.")) errors.add("Please specify if you write down consumed products.");

        // Show errors if any
        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        // Create business practices data
        Map<String, Object> businessPractices = new HashMap<>();
        businessPractices.put("separateFinances", separateFinances);
        businessPractices.put("takeCash", takeCash);
        businessPractices.put("writeDownCash", writeDownCash);
        businessPractices.put("consumeProducts", consumeProducts);
        businessPractices.put("writeDownConsume", writeDownConsume);
        businessPractices.put("timestamp", System.currentTimeMillis());

        // Save data to Firebase (offline support enabled)
        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(businessPractices)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Business practices submitted successfully.");
                    } else {
                        Log.e(TAG, "Error submitting business practices", task.getException());
                    }
                });

        // Proceed to the next activity immediately
        setResult(RESULT_OK);
        finish(); // Close this activity
    }


    // Helper method to validate a RadioGroup
    private boolean validateRadioGroup(RadioGroup group, String errorMessage) {
        if (group.getCheckedRadioButtonId() == -1) {
            showErrorDialog(List.of(errorMessage));
            return true;
        }
        return false;
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
            startActivity(new Intent(BusinessPracticesActivity.this, TopicsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(BusinessPracticesActivity.this, HelpActivity.class));
            overridePendingTransition(0,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
