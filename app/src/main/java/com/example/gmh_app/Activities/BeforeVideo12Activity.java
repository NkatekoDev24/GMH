package com.example.gmh_app.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
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

public class BeforeVideo12Activity extends AppCompatActivity {

    private RadioGroup rgAdjustHabits, rgHappyResults, rgConfidenceProfit, rgSatisfactionProfit;
    private TextView changesExplained, resultsExplained, txtIfYes, title;
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

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

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
        title = findViewById(R.id.title);
        txtIfYes = findViewById(R.id.txtIfYes);

        etCurrentProfit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        tvCombinedToc.setText(Html.fromHtml("<u>PART 4 START PAGE</u>"));

        title.setText(Html.fromHtml("<u>COUNTING AND RECORDING PROFIT – AND THE RISK OF CUSTOMER CREDIT</u>"));

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

        btnSubmit.setOnClickListener(view -> validateAndSubmitResponses());
    }

    private void validateAndSubmitResponses() {
        List<String> errors = new ArrayList<>();

        String adjustHabits = getSelectedOption(rgAdjustHabits);
        String confidenceProfit = getSelectedOption(rgConfidenceProfit);
        String satisfactionProfit = getSelectedOption(rgSatisfactionProfit);
        String currentProfit = etCurrentProfit.getText().toString().trim();
        boolean isVeryEasySelected = rgAdjustHabits.getCheckedRadioButtonId() == R.id.rb_easy_yes_very;

        if (TextUtils.isEmpty(adjustHabits)) errors.add("Please select how easy it was to adjust habits.");
        if (TextUtils.isEmpty(confidenceProfit)) errors.add("Please select your confidence in profit.");
        if (TextUtils.isEmpty(satisfactionProfit)) errors.add("Please select your satisfaction with profit.");
        if (TextUtils.isEmpty(currentProfit) || !isNumeric(currentProfit)) errors.add("Please enter a valid current profit.");

        if (isVeryEasySelected) {
            String whatChanged = etWhatChanged.getText().toString().trim();
            String result = etResult.getText().toString().trim();
            String happyResults = getSelectedOption(rgHappyResults);

            if (TextUtils.isEmpty(whatChanged)) errors.add("Please describe what changed.");
            if (TextUtils.isEmpty(result)) errors.add("Please describe the results.");
            if (TextUtils.isEmpty(happyResults)) errors.add("Please select your happiness with results.");
        }

        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("adjustHabits", adjustHabits);
        response.put("confidenceProfit", confidenceProfit);
        response.put("currentProfit", currentProfit);
        response.put("satisfactionProfit", satisfactionProfit);
        response.put("whatChanged", isVeryEasySelected ? etWhatChanged.getText().toString().trim() : "");
        response.put("result", isVeryEasySelected ? etResult.getText().toString().trim() : "");
        response.put("happyResults", isVeryEasySelected ? getSelectedOption(rgHappyResults) : "");
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
            startActivity(new Intent(BeforeVideo12Activity.this, TopicsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(BeforeVideo12Activity.this, HelpActivity.class));
            overridePendingTransition(0,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
