package com.example.gmh_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class BeforeVideo14Activity extends AppCompatActivity {

    private static final String TAG = "BeforeVideo14Activity";

    private RadioGroup pastProfitCalculationGroup, sellOnCreditGroup, recordDebtsGroup,
            remindCustomersGroup, collectDebtsGroup, writeOffDebtsGroup, sellToDebtorsGroup;
    private EditText grossProfitInput, netProfitInput;
    private Button submitButton;
    TextView tvCombinedToc, video14;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_before_video14);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Before Video 14 Response");
        databaseReference.keepSynced(true); // Ensures local data is synced when online

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        // Initialize UI components
        pastProfitCalculationGroup = findViewById(R.id.calcProfitGroup);
        grossProfitInput = findViewById(R.id.grossProfitInput);
        netProfitInput = findViewById(R.id.netProfitInput);
        sellOnCreditGroup = findViewById(R.id.sellOnCreditGroup);
        recordDebtsGroup = findViewById(R.id.recordDebtsGroup);
        remindCustomersGroup = findViewById(R.id.remindCustomersGroup);
        collectDebtsGroup = findViewById(R.id.collectDebtsGroup);
        writeOffDebtsGroup = findViewById(R.id.writeOffDebtGroup);
        sellToDebtorsGroup = findViewById(R.id.sellToIndebtedGroup);
        submitButton = findViewById(R.id.submitButton);
        tvCombinedToc = findViewById(R.id.tvCombinedToc);
        video14 = findViewById(R.id.video14);

        video14.setText(Html.fromHtml("<u>VIDEO 14</u>"));


        tvCombinedToc.setText(Html.fromHtml(
                "<b>Part 4. Counting and Recording PROFIT – and the risk of customer credit</b><br>" +
                        "Video 12: Calculating Profit correctly.<br>" +
                        "Video 13: Must I use gross profit or net profit for management purposes?<br>" +
                        "<span style='color:#00ff00;'><b><u>Video 14: The risk of customer credit - another hazard.</b></u></span><br>" +
                        "Video 15: Revenue, costs & profits – a complete weekly example with numbers."
        ));


        // Set button listener to validate and submit data
        submitButton.setOnClickListener(v -> validateAndSubmitData());
    }

    private void validateAndSubmitData() {
        List<String> errors = new ArrayList<>();

        String pastProfitResponse = getSelectedRadioButtonText(pastProfitCalculationGroup);
        String grossProfit = grossProfitInput.getText().toString().trim();
        String netProfit = netProfitInput.getText().toString().trim();
        String sellOnCreditExtent = getSelectedRadioButtonText(sellOnCreditGroup);
        String recordDebtsExtent = getSelectedRadioButtonText(recordDebtsGroup);
        String remindCustomersExtent = getSelectedRadioButtonText(remindCustomersGroup);
        String collectDebtsExtent = getSelectedRadioButtonText(collectDebtsGroup);
        String writeOffDebtsExtent = getSelectedRadioButtonText(writeOffDebtsGroup);
        String sellToDebtorsExtent = getSelectedRadioButtonText(sellToDebtorsGroup);

        // Validation
        if (TextUtils.isEmpty(pastProfitResponse)) errors.add("Please select an option for 'Past Profit Calculation'.");
        if (TextUtils.isEmpty(grossProfit) || !isNumeric(grossProfit)) errors.add("Please enter a valid gross profit.");
        if (TextUtils.isEmpty(netProfit) || !isNumeric(netProfit)) errors.add("Please enter a valid net profit.");
        if (TextUtils.isEmpty(sellOnCreditExtent)) errors.add("Please select an option for 'Sell on Credit'.");
        if (TextUtils.isEmpty(recordDebtsExtent)) errors.add("Please select an option for 'Record Debts'.");
        if (TextUtils.isEmpty(remindCustomersExtent)) errors.add("Please select an option for 'Remind Customers'.");
        if (TextUtils.isEmpty(collectDebtsExtent)) errors.add("Please select an option for 'Collect Debts'.");
        if (TextUtils.isEmpty(writeOffDebtsExtent)) errors.add("Please select an option for 'Write Off Debts'.");
        if (TextUtils.isEmpty(sellToDebtorsExtent)) errors.add("Please select an option for 'Sell to Debtors'.");

        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        // Prepare data map
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("PastProfitCalculation", pastProfitResponse);
        dataMap.put("GrossProfit", grossProfit);
        dataMap.put("NetProfit", netProfit);
        dataMap.put("SellOnCredit", sellOnCreditExtent);
        dataMap.put("RecordDebts", recordDebtsExtent);
        dataMap.put("RemindCustomers", remindCustomersExtent);
        dataMap.put("CollectDebts", collectDebtsExtent);
        dataMap.put("WriteOffDebts", writeOffDebtsExtent);
        dataMap.put("SellToDebtors", sellToDebtorsExtent);
        dataMap.put("timestamp", System.currentTimeMillis());

        // Save to Firebase
        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(dataMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Data submitted successfully.");
                    } else {
                        Log.e(TAG, "Error submitting data", task.getException());
                    }
                });

        // Proceed to the next activity
        setResult(RESULT_OK);
        finish();
    }

    private String getSelectedRadioButtonText(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            return "";
        }
        RadioButton selectedButton = findViewById(selectedId);
        return selectedButton.getText().toString();
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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
            startActivity(new Intent(BeforeVideo14Activity.this, TopicsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(BeforeVideo14Activity.this, HelpActivity.class));
            overridePendingTransition(0,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
