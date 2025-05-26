package com.example.gmh_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.gmh_app.R;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_help);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
            getSupportActionBar().setTitle("");
        }

        TextView helpContent = findViewById(R.id.help_text);
        helpContent.setText(getHelpText());
    }

    private String getHelpText() {
        return "Welcome to the GMH App!\n\n"
                + "Here are some tips to get you started:\n\n"
                + "\u2022 Navigate through the four main topics: Orientation, Inflows, Outflows, and Profit.\n"
                + "\u2022 Watch each video and complete the feedback section to unlock the next topic.\n"
                + "\u2022 Earn badges as you complete each section. Check them out in your Profile.\n"
                + "\u2022 Use the bottom navigation to quickly jump between Home, Topics, and Profile.\n\n"
                + "Need more help? Contact our support at help@gmhapp.com.";
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
            startActivity(new Intent(HelpActivity.this, TopicsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(HelpActivity.this, HelpActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.action_achievements) {
            startActivity(new Intent(HelpActivity.this, ProfileActivity.class));
            overridePendingTransition(0, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
