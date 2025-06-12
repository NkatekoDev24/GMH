package com.example.gmh_app.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
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

public class OverallPart3Activity extends AppCompatActivity {

    private RadioGroup changesMadeGroup, progressGroup, moneyHelpGroup;
    private EditText changesAdopted, changesPostponed, reasonPostponed, part3Comments;
    private TextView changesExplained;
    private Button submitButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_overall_part3);

        databaseReference = FirebaseDatabase.getInstance().getReference("Overall Part 3 Response");
        databaseReference.keepSynced(true);

        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        changesMadeGroup = findViewById(R.id.changes_made_group);
        progressGroup = findViewById(R.id.progress_group);
        moneyHelpGroup = findViewById(R.id.money_help_group);
        changesExplained = findViewById(R.id.changes_explained);
        changesAdopted = findViewById(R.id.changes_adopted);
        changesPostponed = findViewById(R.id.changes_postponed);
        reasonPostponed = findViewById(R.id.reason_postponed);
        part3Comments = findViewById(R.id.part3_comments);
        submitButton = findViewById(R.id.submit_button);

        changesMadeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.changes_yes) {
                changesExplained.setVisibility(View.VISIBLE);
                changesAdopted.setVisibility(View.VISIBLE);
            } else {
                changesExplained.setVisibility(View.GONE);
                changesAdopted.setVisibility(View.GONE);
            }
        });

        submitButton.setOnClickListener(v -> validateAndSubmitData());
    }

    private void validateAndSubmitData() {
        String changesMade = getSelectedRadioText(changesMadeGroup);
        String progress = getSelectedRadioText(progressGroup);
        String moneyHelp = getSelectedRadioText(moneyHelpGroup);
        String changesAdoptedText = changesAdopted.getText().toString().trim();
        String changesPostponedText = changesPostponed.getText().toString().trim();
        String reasonPostponedText = reasonPostponed.getText().toString().trim();
        String part3CommentsText = part3Comments.getText().toString().trim();

        List<String> errors = new ArrayList<>();

        if (changesMade == null) errors.add("Please specify the changes made.");
        if (progress == null) errors.add("Please specify your progress.");
        if (moneyHelp == null) errors.add("Please specify if money management has helped.");

        if (changesAdopted.getVisibility() == View.VISIBLE && TextUtils.isEmpty(changesAdoptedText)) {
            errors.add("Please specify the MAIN changes you have adopted.");
        }

        if (TextUtils.isEmpty(changesPostponedText)) errors.add("Please specify the changes you have postponed.");
        if (!"none".equalsIgnoreCase(changesPostponedText) && !"0".equals(changesPostponedText)) {
            if (TextUtils.isEmpty(reasonPostponedText)) {
                errors.add("Please specify the reason for postponing changes.");
            }
        }

        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        Map<String, Object> formData = new HashMap<>();
        formData.put("changes_made", changesMade);
        formData.put("progress", progress);
        formData.put("money_help", moneyHelp);
        formData.put("changes_adopted", changesAdoptedText);
        formData.put("changes_postponed", changesPostponedText);
        formData.put("reason_postponed", reasonPostponedText);
        formData.put("part3_comments", TextUtils.isEmpty(part3CommentsText) ? "No comments provided" : part3CommentsText);
        formData.put("timestamp", System.currentTimeMillis());

        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(formData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Data submitted successfully.");
                    } else {
                        Log.e(TAG, "Error submitting data", task.getException());
                    }
                });

        setResult(RESULT_OK);
        navigateToNextActivity();
    }

    private String getSelectedRadioText(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) return "Not Selected";

        RadioButton selectedRadioButton = findViewById(selectedId);
        if (selectedRadioButton == null || selectedRadioButton.getTag() == null) {
            return "Not Selected";
        }
        return selectedRadioButton.getTag().toString();
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

    private void navigateToNextActivity() {
        Intent nextIntent = new Intent(OverallPart3Activity.this, EnfofPart3Activity.class);
        startActivity(nextIntent);
        finish();
    }
}
