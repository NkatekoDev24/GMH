package com.example.gmh_app.Activities;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
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

import com.example.gmh_app.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class BeforeVideo12Activity extends AppCompatActivity {

    private RadioGroup rgAdjustHabits, rgHappyResults, rgConfidenceProfit, rgSatisfactionProfit;
    private TextView changesExplained, resultsExplained, txtIfYes;
    private EditText etWhatChanged, etResult, etCurrentProfit;
    private Button btnSubmit;
    private TextView tvCombinedToc, tvVideoList, video12;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_before_video12);

        databaseReference = FirebaseDatabase.getInstance().getReference("Before Video 12 Response");
        databaseReference.keepSynced(true);

        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        rgAdjustHabits = findViewById(R.id.rg_adjust_habits);
        rgHappyResults = findViewById(R.id.rg_happy_results);
        rgConfidenceProfit = findViewById(R.id.rg_confidence_profit);
        rgSatisfactionProfit = findViewById(R.id.rg_satisfaction_profit);
        etWhatChanged = findViewById(R.id.et_what_changed);
        etResult = findViewById(R.id.et_result);
        etCurrentProfit = findViewById(R.id.et_current_profit);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvCombinedToc = findViewById(R.id.tvCombinedToc);
        tvVideoList = findViewById(R.id.tv_video_list);
        video12 = findViewById(R.id.vid12);
        changesExplained = findViewById(R.id.text_changes_explained);
        resultsExplained = findViewById(R.id.results_explained);
        txtIfYes = findViewById(R.id.txtIfYes);

        etCurrentProfit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        tvCombinedToc.setText(Html.fromHtml("<u>PART 4 START PAGE</u>"));

        tvVideoList.setText(Html.fromHtml(
                "<b>Part 4. Counting and Recording PROFIT – and the risk of customer credit</b><br>" +
                        "<span style='color:#00ff00;'><b><u>Video 12: Calculating Profit correctly.</b></u></span><br>" +
                        "Video 13: Must I use gross profit or net profit for management purposes?<br>" +
                        "Video 14: The risk of customer credit - another hazard.<br>" +
                        "Video 15: Revenue, costs & profits – a complete weekly example with numbers."
        ));

        video12.setText(Html.fromHtml("<u>VIDEO 12</u>"));

        rgAdjustHabits.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_easy_yes_very) {
                changesExplained.setVisibility(View.VISIBLE);
                etWhatChanged.setVisibility(View.VISIBLE);
                resultsExplained.setVisibility(View.VISIBLE);
                etResult.setVisibility(View.VISIBLE);
                txtIfYes.setVisibility(View.VISIBLE);
                rgHappyResults.setVisibility(View.VISIBLE);
            } else {
                changesExplained.setVisibility(View.GONE);
                etWhatChanged.setVisibility(View.GONE);
                resultsExplained.setVisibility(View.GONE);
                etResult.setVisibility(View.GONE);
                txtIfYes.setVisibility(View.GONE);
                rgHappyResults.setVisibility(View.GONE);
            }
        });

        btnSubmit.setOnClickListener(view -> submitResponses());
    }

    private void submitResponses() {
        String adjustHabits = getSelectedOption(rgAdjustHabits);
        String whatChanged = etWhatChanged.getText().toString().trim();
        String result = etResult.getText().toString().trim();
        String happyResults = getSelectedOption(rgHappyResults);
        String confidenceProfit = getSelectedOption(rgConfidenceProfit);
        String currentProfit = etCurrentProfit.getText().toString().trim();
        String satisfactionProfit = getSelectedOption(rgSatisfactionProfit);

        if (adjustHabits.isEmpty() || confidenceProfit.isEmpty() || currentProfit.isEmpty() || satisfactionProfit.isEmpty() || !isNumeric(currentProfit)) {
            showMessageDialog("Validation Error", "Please complete all required fields with valid input.", false);
            return;
        }

        boolean isVeryEasySelected = rgAdjustHabits.getCheckedRadioButtonId() == R.id.rb_easy_yes_very;

        if (isVeryEasySelected && (whatChanged.isEmpty() || result.isEmpty() || happyResults.isEmpty())) {
            showMessageDialog("Error", "Please complete the follow-up fields when 'Very Easy' is selected.", false);
            return;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("adjustHabits", adjustHabits);
        response.put("whatChanged", isVeryEasySelected ? whatChanged : "");
        response.put("result", isVeryEasySelected ? result : "");
        response.put("happyResults", isVeryEasySelected ? happyResults : "");
        response.put("confidenceProfit", confidenceProfit);
        response.put("currentProfit", currentProfit);
        response.put("satisfactionProfit", satisfactionProfit);

        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(response)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showMessageDialog("Success", "Responses submitted successfully!", true);
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error.";
                        showMessageDialog("Error", "Failed to submit responses. Please try again.\n\nError: " + error, false);
                    }
                });

        setResult(RESULT_OK);
        finish();
    }

    private String getSelectedOption(RadioGroup group) {
        int selectedId = group.getCheckedRadioButtonId();
        if (selectedId == -1) return "";
        return ((RadioButton) findViewById(selectedId)).getText().toString();
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
