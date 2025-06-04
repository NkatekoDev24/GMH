package com.example.gmh_app.Activities;

import static android.content.ContentValues.TAG;

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

import com.example.gmh_app.Classes.BeforeVideo11Response;
import com.example.gmh_app.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeforeVideo11Activity extends AppCompatActivity {

    private RadioGroup radioGroupRent, radioGroupOwn, radioGroupFree, radioGroupStructure, radioGroupLocation;
    private EditText editTextOtherArrangements, editTextMoveReason;
    private Button btnSubmit;
    private TextView tvCombinedToc, intro_text_video11, moveReason;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_before_video11);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Before Video 11 Response");
        databaseReference.keepSynced(true); // Ensures local data is synced when online

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        // Initialize views
        radioGroupRent = findViewById(R.id.radioGroupRent);
/*        radioGroupOwn = findViewById(R.id.radioGroupOwn);
        radioGroupFree = findViewById(R.id.radioGroupFree);*/
        editTextOtherArrangements = findViewById(R.id.editTextOtherArrangements);
        radioGroupStructure = findViewById(R.id.radioGroupStructure);
        radioGroupLocation = findViewById(R.id.radioGroupLocation);
        editTextMoveReason = findViewById(R.id.editTextMoveReason);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvCombinedToc = findViewById(R.id.tvCombinedToc);
        intro_text_video11 = findViewById(R.id.intro_text_video11);
        moveReason = findViewById(R.id.tvMoveReason);

        // Set text content
        tvCombinedToc.setText(Html.fromHtml(
                "<b>Part 3. Counting and Recording Money OUTFLOWS (EXPENSES)</b><br>" +
                        "Video 9: Correctly counting and recording Money Outflows: Variable costs versus Fixed costs.<br>" +
                        "Video 10: Correctly counting and recording stock purchases and other variable costs.<br>" +
                        "<span style='color:#00ff00;'><b><u>Video 11: Fixed costs / Monthly basic costs / Overhead costs.</b></u></span>"
        ));

        intro_text_video11.setText(Html.fromHtml("<u>VIDEO 11</u>"));

        // Set up submit button listener
        btnSubmit.setOnClickListener(view -> submitResponses());

        radioGroupLocation.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.radioLocationYes)
            {
                moveReason.setVisibility(View.GONE);
                editTextMoveReason.setVisibility(View.GONE);
            }else {
                moveReason.setVisibility(View.VISIBLE);
                editTextMoveReason.setVisibility(View.VISIBLE);
            }
        });
    }

    private void submitResponses() {
        // Gather user inputs
        String rent = getSelectedOption(radioGroupRent);
       /* String own = getSelectedOption(radioGroupOwn);
        String free = getSelectedOption(radioGroupFree);*/
        String otherArrangements = editTextOtherArrangements.getText().toString().trim();
        String structure = getSelectedOption(radioGroupStructure);
        String location = getSelectedOption(radioGroupLocation);
        String moveReason = editTextMoveReason.getText().toString().trim();

        List<String> errors = new ArrayList<>();

        // rules
        if (rent.isEmpty()) errors.add("Please select an option for 'Regarding your current business premises:'.");
        /*if (own.isEmpty()) errors.add("Please select an option for 'Do you own your business premises?'.");
        if (free.isEmpty()) errors.add("Please select an option for 'Do you use the premises for free?'.");*/
        if (rent.equals("Other") && otherArrangements.isEmpty()) errors.add("Please provide details about your other arrangements for rent.");
        if (structure.isEmpty()) errors.add("Please select an option for 'What is the structure of the premises?'.");
        if (location.isEmpty()) errors.add("Please select an option for 'What is the location of the premises?'.");
        if (location.equals("Move to a new location") && moveReason.isEmpty()) errors.add( "Please provide a reason for moving to a new location.");

        // Show errors if any
        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        // Create feedback data
        Map<String, Object> response = new HashMap<>();
        response.put("rent", rent);
/*        response.put("own", own);
        response.put("free", free);*/
        response.put("otherArrangements", TextUtils.isEmpty(otherArrangements) ? "N/A" : otherArrangements);
        response.put("structure", structure);
        response.put("location", location);
        response.put("moveReason", TextUtils.isEmpty(moveReason) ? "N/A" : moveReason);
        response.put("timestamp", System.currentTimeMillis());

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

    private String getSelectedOption(RadioGroup group) {
        int selectedId = group.getCheckedRadioButtonId();
        if (selectedId == -1) {
            return ""; // No option selected
        }
        RadioButton selectedRadioButton = findViewById(selectedId);
        return selectedRadioButton.getText().toString();
    }

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
            startActivity(new Intent(BeforeVideo11Activity.this, TopicsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(BeforeVideo11Activity.this, HelpActivity.class));
            overridePendingTransition(0,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
