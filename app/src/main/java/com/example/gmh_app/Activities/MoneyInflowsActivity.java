package com.example.gmh_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class MoneyInflowsActivity extends AppCompatActivity {

    private static final String TAG = "MoneyInflowsActivity";

    // UI Components
    private RadioGroup businessDurationGroup, countCashStartGroup, countCashEndGroup, writeTransactionsGroup, countStockGroup;
    private TextView tvCombinedToc, tvParts2, video5, tvWelcome;
    private Button submitButton;

    // Firebase Database Reference
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_money_inflows);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Before Video 5 Response");
        databaseReference.keepSynced(true); // Ensures local data is synced when online

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        // Initialize UI Components
        businessDurationGroup = findViewById(R.id.business_duration_group);
        countCashStartGroup = findViewById(R.id.count_cash_start_group);
        countCashEndGroup = findViewById(R.id.count_cash_end_group);
        writeTransactionsGroup = findViewById(R.id.write_transactions_group);
        countStockGroup = findViewById(R.id.count_stock_group);
        tvCombinedToc = findViewById(R.id.tvCombinedToc);
        tvParts2 = findViewById(R.id.tvparts2);
        video5 = findViewById(R.id.video5);
        submitButton = findViewById(R.id.submit_button);
        tvWelcome = findViewById(R.id.tvWelcome);

        tvWelcome.setText(Html.fromHtml("<u>COUNTING AND RECORDING MONEY INFLOWS</u>"));

        // Set Text for TextViews
        tvParts2.setText(Html.fromHtml("<u>PART 2 START PAGE</u>"));
        tvCombinedToc.setText(Html.fromHtml(
                "<b>Part 2. Counting and Recording Money INFLOWS</b><br>" +
                        "<span style='color:#00ff00;'><b><u>Video 5: Daily steps to count Money Inflows correctly – and avoid financial hazards.</b></u></span><br>" +
                        "Video 6: More hazards when counting daily money inflows.<br>" +
                        "Video 7: Correcting daily calculations of money inflows for purchases and hazards.<br>" +
                        "Video 8: Using transaction values to calculate money inflows for a day and week."
        ));
        video5.setText(Html.fromHtml("<u>VIDEO 5</u>"));

        // Set up Submit Button listener
        submitButton.setOnClickListener(v -> validateAndSubmit());
    }

    private void validateAndSubmit() {
        // Create a list to hold error messages
        List<String> errors = new ArrayList<>();

        // Validate input
        if (!validateRadioGroup(businessDurationGroup, "Please specify the duration of your business operations.")) {
            errors.add("Please specify the duration of your business operations.");
        }
        if (!validateRadioGroup(countCashStartGroup, "Please specify if you count cash at the start of the day.")) {
            errors.add("Please specify if you count cash at the start of the day.");
        }
        if (!validateRadioGroup(countCashEndGroup, "Please specify if you count cash at the end of the day.")) {
            errors.add("Please specify if you count cash at the end of the day.");
        }
        if (!validateRadioGroup(writeTransactionsGroup, "Please specify if you write down daily transactions.")) {
            errors.add("Please specify if you write down daily transactions.");
        }
        if (!validateRadioGroup(countStockGroup, "Please specify if you count your stock.")) {
            errors.add("Please specify if you count your stock.");
        }

        // Show errors if any
        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        // Collect data from RadioGroups
        String businessDuration = getSelectedRadioText(businessDurationGroup);
        String countCashStart = getSelectedRadioText(countCashStartGroup);
        String countCashEnd = getSelectedRadioText(countCashEndGroup);
        String writeTransactions = getSelectedRadioText(writeTransactionsGroup);
        String countStock = getSelectedRadioText(countStockGroup);

        // Prepare data for Firebase
        Map<String, Object> moneyInflowsData = new HashMap<>();
        moneyInflowsData.put("businessDuration", businessDuration);
        moneyInflowsData.put("countCashStart", countCashStart);
        moneyInflowsData.put("countCashEnd", countCashEnd);
        moneyInflowsData.put("writeTransactions", writeTransactions);
        moneyInflowsData.put("countStock", countStock);
        moneyInflowsData.put("timestamp", System.currentTimeMillis());

        // Save data to Firebase
        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(moneyInflowsData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Data submitted successfully.");
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error.";
                        Log.e(TAG, "Error submitting data: " + error);
//                        showErrorDialog("Error submitting data: " + error);
                    }
                });

        // Proceed to the next activity immediately
        setResult(RESULT_OK);
        finish(); // Close this activity
    }


    // Helper method to validate a RadioGroup and show a dialog if invalid
    private boolean validateRadioGroup(RadioGroup group, String errorMessage) {
        if (group.getCheckedRadioButtonId() == -1) {
            showErrorDialog(List.of(errorMessage));
            return false;
        }
        return true;
    }

    // Helper method to get the selected RadioButton text from a RadioGroup
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

    // Helper method to show a success dialog
    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage("Data submitted successfully!")
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
            startActivity(new Intent(MoneyInflowsActivity.this, TopicsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(MoneyInflowsActivity.this, HelpActivity.class));
            overridePendingTransition(0,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
