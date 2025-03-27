// File path: src/main/java/com/example/gmh_app/EndofPart4Activity.java
package com.example.gmh_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gmh_app.R;

public class EndofPart4Activity extends AppCompatActivity {

    private RadioGroup recommendationGroup;
    private RadioButton rbYes, rbNo;
    private EditText etTopics;
    private Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_endof_part4);

        // Initialize UI elements
//        rbYes = findViewById(R.id.rbYes);
//        rbNo = findViewById(R.id.rbNo);
        etTopics = findViewById(R.id.etTopics);
        btnContinue = findViewById(R.id.btnContinue);

        // Set button click listener
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the next part of the app (replace TargetActivity with the actual activity class)
                Intent intent = new Intent(EndofPart4Activity.this, TopicsActivity.class);
                startActivity(intent);

                // Finish the current activity to prevent returning to it on back press
                finish();
            }
        });
    }
}
