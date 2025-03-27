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

import com.example.gmh_app.Classes.BeforeVideo11Response;
import com.example.gmh_app.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BeforeVideo11Activity extends AppCompatActivity {

    private RadioGroup radioGroupRent, radioGroupOwn, radioGroupFree, radioGroupStructure, radioGroupLocation;
    private EditText editTextOtherArrangements, editTextMoveReason;
    private Button btnSubmit;
    private TextView tvCombinedToc, intro_text_video11;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_before_video11);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Before Video 11 Response");
        databaseReference.keepSynced(true); // Ensures local data is synced when online

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        // Initialize views
        radioGroupRent = findViewById(R.id.radioGroupRent);
        radioGroupOwn = findViewById(R.id.radioGroupOwn);
        radioGroupFree = findViewById(R.id.radioGroupFree);
        editTextOtherArrangements = findViewById(R.id.editTextOtherArrangements);
        radioGroupStructure = findViewById(R.id.radioGroupStructure);
        radioGroupLocation = findViewById(R.id.radioGroupLocation);
        editTextMoveReason = findViewById(R.id.editTextMoveReason);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvCombinedToc = findViewById(R.id.tvCombinedToc);
        intro_text_video11 = findViewById(R.id.intro_text_video11);

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
    }

    private void submitResponses() {
        // Gather user inputs
        String rent = getSelectedOption(radioGroupRent);
        String own = getSelectedOption(radioGroupOwn);
        String free = getSelectedOption(radioGroupFree);
        String otherArrangements = editTextOtherArrangements.getText().toString().trim();
        String structure = getSelectedOption(radioGroupStructure);
        String location = getSelectedOption(radioGroupLocation);
        String moveReason = editTextMoveReason.getText().toString().trim();

        // rules
        if (rent.isEmpty()) {
            showMessageDialog("Error", "Please select an option for 'Do you rent your business premises?'.", false);
            return;
        }

        if (own.isEmpty()) {
            showMessageDialog("Error", "Please select an option for 'Do you own your business premises?'.", false);
            return;
        }

        if (free.isEmpty()) {
            showMessageDialog("Error", "Please select an option for 'Do you use the premises for free?'.", false);
            return;
        }

        if (rent.equals("Other") && otherArrangements.isEmpty()) {
            showMessageDialog("Error", "Please provide details about your other arrangements for rent.", false);
            return;
        }

        if (structure.isEmpty()) {
            showMessageDialog("Error", "Please select an option for 'What is the structure of the premises?'.", false);
            return;
        }

        if (location.isEmpty()) {
            showMessageDialog("Error", "Please select an option for 'What is the location of the premises?'.", false);
            return;
        }

        if (location.equals("Move to a new location") && moveReason.isEmpty()) {
            showMessageDialog("Error", "Please provide a reason for moving to a new location.", false);
            return;
        }

        // Create a response object
        BeforeVideo11Response response = new BeforeVideo11Response(
                rent, own, free, otherArrangements, structure, location, moveReason
        );

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
}
