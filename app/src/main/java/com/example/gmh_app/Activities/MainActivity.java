package com.example.gmh_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gmh_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                Intent intent = null;
                if (id == R.id.navigation_home) {
                    // Home is already the current activity, no need to create new intent
                    return true;
                }else if(id == R.id.navigation_forum){

//                    intent = new Intent(MainActivity.this, ForumActivity.class);
//                    startActivity(intent);
//                    return true;
                } else if (id == R.id.navigation_account) {
//                    intent = new Intent(MainActivity.this, AccountActivity.class);
                }
                if (intent != null) {
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        Button btnStarted = findViewById(R.id.getStartedButton);
        btnStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TopicsActivity.class);
                startActivity(intent);
            }
        });
    }
}
