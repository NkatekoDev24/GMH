package com.example.gmh_app.Activities;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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

public class BeforeVideo14Activity extends AppCompatActivity {

    private RadioGroup pastProfitCalculationGroup, sellOnCreditGroup, recordDebtsGroup, remindCustomersGroup,
            collectDebtsGroup, writeOffDebtsGroup, sellToDebtorsGroup;
    private EditText grossProfitInput, netProfitInput;
    private Button submitButton;
    private TextView tvCombinedToc, tvCredit, video14;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_before_video14);

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
        tvCredit = findViewById(R.id.tvCredit);
        video14 = findViewById(R.id.video14);

        // Set text for TextViews
        tvCombinedToc.setText(Html.fromHtml(
                "<b>Part 4. Counting and Recording PROFIT; the risk of customer credit</b><br>" +
                        "Video 12: Calculating Profit correctly.<br>" +
                        "Video 13: Must I use gross profit or net profit for management purposes?<br>" +
                        "<span style='color:#00ff00;'><b><u>Video 14: The risk of customer credit - another hazard.</b></u></span><br>" +
                        "Video 15: Revenue, costs & profits – a complete weekly example with numbers."
        ));

        tvCredit.setText(Html.fromHtml("In this short video, we revisit the issue of hazards – in fact, we meet a <b>major hazard</b> that you need to pay attention to.<br><br>" +
                "Before you watch the video, let’s think about CREDIT a bit:"));

        video14.setText(Html.fromHtml("<u>VIDEO 14</u>"));

        // Set button listener to save data
        submitButton.setOnClickListener(v -> saveDataToFirebase());
    }

    private void saveDataToFirebase() {
        // Get data from UI components
        String pastProfitResponse = getSelectedRadioButtonText(pastProfitCalculationGroup);
        String grossProfit = grossProfitInput.getText().toString().trim();
        String netProfit = netProfitInput.getText().toString().trim();
        String sellOnCreditExtent = getSelectedRadioButtonText(sellOnCreditGroup);
        String recordDebtsExtent = getSelectedRadioButtonText(recordDebtsGroup);
        String remindCustomersExtent = getSelectedRadioButtonText(remindCustomersGroup);
        String collectDebtsExtent = getSelectedRadioButtonText(collectDebtsGroup);
        String writeOffDebtsExtent = getSelectedRadioButtonText(writeOffDebtsGroup);
        String sellToDebtorsExtent = getSelectedRadioButtonText(sellToDebtorsGroup);

        // Validate fields
        if (pastProfitResponse.isEmpty()) {
            showMessageDialog("Error", "Please select an option for 'Past Profit Calculation'.", false);
            return;
        }

        if (grossProfit.isEmpty() || !isNumeric(grossProfit)) {
            showMessageDialog("Error", "Please enter a valid gross profit.", false);
            return;
        }

        if (netProfit.isEmpty() || !isNumeric(netProfit)) {
            showMessageDialog("Error", "Please enter a valid net profit.", false);
            return;
        }

        if (sellOnCreditExtent.isEmpty()) {
            showMessageDialog("Error", "Please select an option for 'Sell on Credit'.", false);
            return;
        }

        if (recordDebtsExtent.isEmpty()) {
            showMessageDialog("Error", "Please select an option for 'Record Debts'.", false);
            return;
        }

        if (remindCustomersExtent.isEmpty()) {
            showMessageDialog("Error", "Please select an option for 'Remind Customers'.", false);
            return;
        }

        if (collectDebtsExtent.isEmpty()) {
            showMessageDialog("Error", "Please select an option for 'Collect Debts'.", false);
            return;
        }

        if (writeOffDebtsExtent.isEmpty()) {
            showMessageDialog("Error", "Please select an option for 'Write Off Debts'.", false);
            return;
        }

        if (sellToDebtorsExtent.isEmpty()) {
            showMessageDialog("Error", "Please select an option for 'Sell to Debtors'.", false);
            return;
        }

        // Prepare data map
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("PastProfitCalculation", pastProfitResponse);
        dataMap.put("GrossProfit", grossProfit);
        dataMap.put("NetProfit", netProfit);
        dataMap.put("SellOnCredit", sellOnCreditExtent);
        dataMap.put("RecordDebts", recordDebtsExtent);
        dataMap.put("RemindCustomers", remindCustomersExtent);
        dataMap.put("CollectDebts", collectDebtsExtent);
        dataMap.put("WriteOffDebts", writeOffDebtsExtent);
        dataMap.put("SellToDebtors", sellToDebtorsExtent);

        // Save to Firebase
        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(dataMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showMessageDialog("Success", "Data saved successfully!", true);
            } else {
                showMessageDialog("Error", "Failed to save data. Try again.", false);
            }
        });

        // Proceed to the next activity immediately
        setResult(RESULT_OK);
        finish(); // Close this activity
    }

    private String getSelectedRadioButtonText(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            return "";
        }
        RadioButton selectedButton = findViewById(selectedId);
        return selectedButton.getText().toString();
    }

    private void showMessageDialog(String title, String message, boolean isSuccess) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (isSuccess) {
                        setResult(RESULT_OK);
                        finish();
                    }
                })
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
}
