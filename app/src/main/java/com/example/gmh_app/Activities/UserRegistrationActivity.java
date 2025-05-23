package com.example.gmh_app.Activities;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gmh_app.R;
import com.example.gmh_app.Classes.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRegistrationActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etCellphone, etAge, etLocation, etCity, etCountry, etBusinessName, etBusinessDescription;
    private RadioGroup rgEducation, rgGender, rgBusinessName, rbBusinessYes,rbBusinessNo, rgNameDisplayed;
    private Spinner spinnerProvince;
    private TextView tvCombinedToc, video2, tvBusinessFollowup, tvBusinessName;
    private Button btnSubmit;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_user_registration);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("User Registration");
        databaseReference.keepSynced(true); // Ensures local data is synced when online

        // Debugging: Log Firebase Database path
        Log.d(TAG, "Firebase Database Path: " + databaseReference);

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etCellphone = findViewById(R.id.etCellphone);
        etAge = findViewById(R.id.etAge);
        etLocation = findViewById(R.id.etLocation);
        etCity = findViewById(R.id.etCity);
        etCountry = findViewById(R.id.etCountry);
        etBusinessName = findViewById(R.id.etBusinessName);
        etBusinessDescription = findViewById(R.id.etBusinessDescription);
        rgEducation = findViewById(R.id.rgEducation);
        rgGender = findViewById(R.id.rgGender);
        rgBusinessName = findViewById(R.id.rgBusinessName);
        rgNameDisplayed = findViewById(R.id.rgNameDisplayed);
        spinnerProvince = findViewById(R.id.spinnerProvince);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvCombinedToc = findViewById(R.id.tvCombinedToc);
        video2 = findViewById(R.id.video2);
        tvBusinessFollowup = findViewById(R.id.tvBusinessNameFollowup);
        tvBusinessName = findViewById(R.id.tvBusinessName);


        rgBusinessName.setOnCheckedChangeListener((group, checkedId ) ->{
            if (checkedId == R.id.rbBusinessYes) {
                tvBusinessFollowup.setVisibility(View.VISIBLE);
                rgNameDisplayed.setVisibility(View.VISIBLE);
                tvBusinessName.setVisibility(View.VISIBLE);
                etBusinessName.setVisibility(View.VISIBLE);
            } else {
                tvBusinessFollowup.setVisibility(View.GONE);
                rgNameDisplayed.setVisibility(View.GONE);
                tvBusinessName.setVisibility(View.GONE);
                etBusinessName.setVisibility(View.GONE);
            }
        });

                // Populate the spinner
        List<String> provinces = new ArrayList<>(Arrays.asList("Select Province", "Gauteng", "Western Cape", "KwaZulu-Natal", "Eastern Cape", "Free State", "Limpopo", "Mpumalanga", "Northern Cape", "North West"));
        String defaultOption = provinces.remove(0);
        Collections.sort(provinces);
        provinces.add(0, defaultOption);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, provinces);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvince.setAdapter(adapter);

        tvCombinedToc.setText(Html.fromHtml(
                "<b>Part 1. BASICS: Why Good Money Habits – and the Separation rule</b><br>" +
                        "Video 1: Introduction – Why good money habits?<br>" +
                        "<span style='color:#00ff00;'><b><u>Video 2: Making a profit – and not a loss.</u></b></span><br>" +
                        "Video 3: Profit in good & bad weeks: Good decisions & avoiding losses.<br>" +
                        "Video 4: The Separation Rule – Most important for hazard avoidance."
        ));

        video2.setText(Html.fromHtml("<u>VIDEO 2</u>"));

        btnSubmit.setOnClickListener(v -> {
            validateAndSubmitForm();
        });
    }

    private void validateAndSubmitForm() {
        // Collect input data
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String cellphone = etCellphone.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        String businessName = etBusinessName.getText().toString().trim();
        String businessDescription = etBusinessDescription.getText().toString().trim();
        String province = spinnerProvince.getSelectedItem().toString();

        // Get selected options
        String gender = getSelectedRadioText(rgGender);
        String education = getSelectedRadioText(rgEducation);
        String hasBusinessName = getSelectedRadioText(rgBusinessName);
        String isNameDisplayed = getSelectedRadioText(rgNameDisplayed);

        // Create a list to hold error messages
        List<String> errors = new ArrayList<>();

        // Validate input
        if (TextUtils.isEmpty(username)) errors.add("Username is required.");
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) errors.add("Please enter a valid email address.");
        if (!isValidSouthAfricanCellNumber(cellphone) || cellphone.length() < 10) errors.add("Please enter a valid South African cellphone number (at least 10 characters). ");
        if (spinnerProvince.getSelectedItemPosition() == 0) errors.add("Please select a province.");
        if (rgGender.getCheckedRadioButtonId() == -1) errors.add("Please select a gender.");
        if (rgEducation.getCheckedRadioButtonId() == -1) errors.add("Please select your education level.");
        if (rgBusinessName.getCheckedRadioButtonId() == -1) errors.add("Please specify if you have a business.");
        if (TextUtils.isEmpty(age) || !isAge(age)) errors.add("Please enter a valid age (14-120 years).");
        if (!hasMinimumWords(businessDescription, 3)) errors.add("Business description must contain at least three words.");
        if (rgBusinessName.getCheckedRadioButtonId() == R.id.rbBusinessYes && TextUtils.isEmpty(businessName)) errors.add("Business name is required.");
        if (rgBusinessName.getCheckedRadioButtonId() == R.id.rbBusinessYes && rgNameDisplayed.getCheckedRadioButtonId() == -1) errors.add("Please select if your name should be displayed.");

        // Show errors if any
        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        // Create user data map
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);
        userData.put("cellphone", cellphone);
        userData.put("age", age);
        userData.put("city", city);
        userData.put("country", country);
        userData.put("businessName", businessName);
        userData.put("businessDescription", businessDescription);
        userData.put("gender", gender);
        userData.put("education", education);
        userData.put("hasBusinessName", hasBusinessName);
        userData.put("isNameDisplayed", isNameDisplayed);
        userData.put("province", province);
        userData.put("timestamp", System.currentTimeMillis());

        // Store in Firebase
        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showDialogWithAction("Success", "Registration successful!", true);
                    } else {
                        showErrorDialog(List.of("Failed to register. Try again!"));
                    }
                });

        // Proceed to the next activity immediately
        setResult(RESULT_OK);
        finish(); // Close this activity
    }

    private String getSelectedRadioText(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) return "Not Selected";
        RadioButton selectedButton = findViewById(selectedId);
        return selectedButton.getText().toString();
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

    private void showDialogWithAction(String title, String message, boolean clearForm) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (clearForm) {
                        clearForm();
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .show();
    }

    private void clearForm() {
        etUsername.setText("");
        etEmail.setText("");
        etCellphone.setText("");
        etAge.setText("");
        etCity.setText("");
        etCountry.setText("");
        etBusinessName.setText("");
        etBusinessDescription.setText("");
        rgEducation.clearCheck();
        rgGender.clearCheck();
        rgBusinessName.clearCheck();
        rgNameDisplayed.clearCheck();
        spinnerProvince.setSelection(0);
    }

    // Validation helper method
    private boolean isValidSouthAfricanCellNumber(String password) {
        //Check if the number starts with +27 or 0
        if (password.startsWith("+27")) {
            //Validate length: +27XXXXXXXXX (12 characters in total)
            return password.length() == 12 && password.substring(3).matches("\\d+");
        } else if (password.startsWith("0")) {
            //Validate length: 0XXXXXXXXX (10 characters in total)
            return password.length() == 10 && password.substring(1).matches("\\d+");
        } else {
            //Invalid prefix
            return false;
        }
    }
    private boolean isAge(String str) {
        int noAge = Integer.parseInt(str);
        return noAge > 13 && noAge < 121;
    }

    private boolean hasMinimumWords(String text, int minWords) {
        String[] words = text.split("\\s+"); // Split by whitespace
        return words.length >= minWords;
    }
}